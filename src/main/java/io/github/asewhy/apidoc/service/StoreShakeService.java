package io.github.asewhy.apidoc.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.ApiDocumentationConfiguration;
import io.github.asewhy.apidoc.annotations.ForceDocumented;
import io.github.asewhy.apidoc.annotations.Hidden;
import io.github.asewhy.apidoc.annotations.documentation.*;
import io.github.asewhy.apidoc.descriptor.*;
import io.github.asewhy.apidoc.descriptor.enums.DocDTOType;
import io.github.asewhy.apidoc.descriptor.enums.DocMethodType;
import io.github.asewhy.conversions.ConversionProvider;
import io.github.asewhy.conversions.ConversionUtils;
import io.github.asewhy.conversions.support.BoundedSource;
import io.github.asewhy.conversions.support.annotations.ConvertMutator;
import io.github.asewhy.conversions.support.annotations.ConvertRequest;
import io.github.asewhy.conversions.support.annotations.ConvertResponse;
import io.github.asewhy.conversions.support.annotations.ShiftController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Component
@SuppressWarnings("UnusedReturnValue")
public class StoreShakeService {
    @Autowired
    protected ConversionProvider provider;
    @Autowired
    protected ApplicationContext context;
    @Autowired
    protected DocumentedApi api;
    @Autowired
    protected ApiDocumentationConfiguration config;

    @PostConstruct
    public void init() {
        config.beforeApiInitialize(api);

        if(!config.requireApiInitialize()) {
            config.afterApiInitialize(api);

            return;
        }

        var factory = provider.getConfig();
        var store = factory.getStore();
        var mutators = store.getMutatorsMap().keySet();
        var responses = store.getResponseMap();
        var controllers = context.getBeansWithAnnotation(ShiftController.class).values();

        for(var mutator: mutators) {
            shakeRequestDto(mutator);
        }

        for(var bag: responses.entrySet()) {
            var type = bag.getKey();
            var data = bag.getValue();

            for(var current: data.entrySet()) {
                shakeResponseDto(type, current.getKey());
            }
        }

        for(var controller: controllers) {
            if(controller.getClass().isAnnotationPresent(Hidden.class)) {
                continue;
            }

            shakeController(controller);
        }

        config.afterApiInitialize(api);
    }

    /**
     * Обойти контроллер
     *
     * @param controller экземпляр контроллера
     * @return документация контроллера
     */
    public DocController shakeController(@NotNull Object controller) {
        var type = ReflectionUtils.skipAnonClasses(controller.getClass());
        var result = api.getController(type);

        if(result != null) {
            return result;
        } else {
            result = api.makeController(type);
        }

        var securityAnnotation = AnnotatedElementUtils.findMergedAnnotation(type, Security.class);
        var controllerAnnotation = AnnotatedElementUtils.findMergedAnnotation(type, Controller.class);
        var requestAnnotation = AnnotatedElementUtils.findMergedAnnotation(type, RequestMapping.class);
        var descriptionAnnotation = type.getAnnotation(Description.class);

        var securityInfo = new HashSet<Security>();

        if(securityAnnotation != null) {
            securityInfo.add(securityAnnotation);
        }

        if(controllerAnnotation != null) {
            securityInfo.addAll(Set.of(controllerAnnotation.security()));
        }

        result.setSecurities(securityInfo);

        if(requestAnnotation != null) {
            result.setPath(List.of(requestAnnotation.path()));

            if(!requestAnnotation.name().isBlank()) {
                result.setName(requestAnnotation.name());
            } else {
                result.setName(type.getSimpleName());
            }
        } else {
            result.setPath(Collections.emptyList());
            result.setName(type.getSimpleName());
        }

        result.setOriginal(type);

        if(descriptionAnnotation != null) {
            result.setDescription(descriptionAnnotation.value());
        }

        var methods = ReflectionUtils.scanMethods(type, Set.of(Object.class));

        for(var method: methods) {
            var request = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            var mapping = getMappingName(method);

            if(request != null && !method.isAnnotationPresent(Hidden.class)) {
                result.addMethod(shakeControllerMethod(method, type, result, request, mapping));
            }
        }

        return result;
    }

    /**
     * Обойти метод контроллера
     *
     * @param controllerMethod метод для обхода
     * @param controller контроллер метода
     * @param controllerType тип контроллера, как корень для обхода generic типов интерфейса контроллера
     * @param request аннотация RequestMapping
     * @param mapping маппинг
     * @return документация метода
     */
    public DocMethod shakeControllerMethod(@NotNull Method controllerMethod, Class<?> controllerType, DocController controller, RequestMapping request, String mapping) {
        var returnType = GenericTypeResolver.resolveReturnType(controllerMethod, controllerType);
        var parameters = controllerMethod.getParameters();

        var descriptionAnnotation = controllerMethod.getAnnotation(Description.class);
        var securityAnnotation = AnnotatedElementUtils.findMergedAnnotation(controllerMethod, Security.class);
        var responseAnnotation = AnnotatedElementUtils.findMergedAnnotation(controllerMethod, Response.class);
        var methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(controllerMethod, io.github.asewhy.apidoc.annotations.documentation.Method.class);
        var exampleAnnotation = controllerMethod.getAnnotation(Example.class);

        var method = new DocMethod();
        var response = getRequestParameter(parameters);

        if(response != null) {
            method.setBody(shakeRequestDto(response));
        }

        var securityInfo = new HashSet<>(controller.getSecurities());
        var responseInfo = new HashSet<Response>();

        if(securityAnnotation != null) {
            securityInfo.add(securityAnnotation);
        }

        if(responseAnnotation != null) {
            responseInfo.add(responseAnnotation);
        }

        if(methodAnnotation != null) {
            securityInfo.addAll(Set.of(methodAnnotation.security()));
            responseInfo.addAll(Set.of(methodAnnotation.response()));
        }

        method.setSecurities(securityInfo);
        method.setResponses(responseInfo);

        method.setType(canProcess(controllerMethod, controllerType, mapping) ? DocMethodType.pure : DocMethodType.nat);

        if(void.class != returnType) {
            method.setResponse(shakeResponseDto(returnType, mapping));
        }

        method.setMethods(List.of(request.method()));
        method.setName(controllerMethod.getName());
        method.setVariables(scanVariables(parameters));
        method.setPath(List.of(request.path()));
        method.setOriginal(controllerMethod);
        method.setController(controller);
        method.setDeprecated(controllerMethod.isAnnotationPresent(Deprecated.class));

        if(exampleAnnotation != null) {
            method.setExample(exampleAnnotation.value());
        }

        if(descriptionAnnotation != null) {
            method.setDescription(descriptionAnnotation.value());
        }

        return method;
    }

    /**
     * Получить список переменных пути из параметров запроса
     *
     * @param parameters параметры запроса
     * @return список переменных пути
     */
    public List<DocVariable> scanVariables(Parameter @NotNull [] parameters) {
        var result = new ArrayList<DocVariable>();

        for(var parameter: parameters) {
            var annotation = AnnotatedElementUtils.findMergedAnnotation(parameter, PathVariable.class);

            if(annotation != null) {
                var description = parameter.getAnnotation(Description.class);
                var variable = new DocVariable();

                variable.setName(annotation.value());
                variable.setAnnotation(annotation);
                variable.setRequired(annotation.required());

                if(description != null) {
                    variable.setDescription(description.value());
                }

                variable.setParameter(parameter);

                result.add(variable);
            }
        }

        return result;
    }

    /**
     * Получить параметр запроса
     *
     * @param parameters параметры метода
     * @return если нашел, параметр запроса вернет его. Если нет null
     */
    private @Nullable Class<?> getRequestParameter(Parameter @NotNull [] parameters) {
        for(var parameter: parameters) {
            if(
                parameter.isAnnotationPresent(ConvertMutator.class) ||
                parameter.isAnnotationPresent(ConvertRequest.class) ||
                parameter.isAnnotationPresent(RequestBody.class) ||
                parameter.isAnnotationPresent(Body.class)
            ) {
                return parameter.getType();
            }
        }

        return null;
    }

    /**
     * Получить название маппинга для текущей конвертируемой сущности
     *
     * @param parameter конвертируемый параметр
     * @return маппинг
     */
    private String getMappingName(@NotNull Method parameter) {
        var annotation = parameter.getAnnotation(ConvertResponse.class);
        var annotatedClass = parameter.getDeclaringClass();

        if(annotation != null) {
            return annotation.mapping();
        }

        annotation = annotatedClass.getAnnotation(ConvertResponse.class);

        if(annotation != null) {
            return annotation.mapping();
        }

        var controller = annotatedClass.getAnnotation(ShiftController.class);

        if(controller != null) {
            return controller.mapping();
        }

        return ConversionUtils.COMMON_MAPPING;
    }

    /**
     * Проверяет, может ли ресолвер обработать этот метод ответа
     *
     * @param method метод
     * @param mapping маппинг
     * @return true если может
     */
    private boolean canProcess(@NotNull Method method, Class<?> controllerRoot, String mapping) {
        var result = GenericTypeResolver.resolveReturnType(method, controllerRoot);
        var store = provider.getConfig().getStore();

        if(method.isAnnotationPresent(ForceDocumented.class)) {
            return true;
        }

        if(result == Void.class || Map.class == result) {
            return true;
        }

        if(store.isPresentResponse(result)) {
            return true;
        } else {
            return provider.canResolveResponse(
                result,
                GenericTypeResolver.resolveType(method.getGenericReturnType(), controllerRoot),
                mapping
            );
        }
    }

    /**
     * Обойти дерево объектов ответа, и найти подходящий для текущего типа сущности
     *
     * @param type тип сущности для поиска объекта ответа
     * @return документация для ответа
     */
    public DocDTO shakeResponseDto(Class<?> type, String mapping) {
        var dto = api.getDto(type);

        if(dto != null) {
            dto.setType(dto.getType() == DocDTOType.request ? DocDTOType.composite : DocDTOType.response);

            return dto;
        } else {
            dto = api.makeDTO(type);
        }

        var factory = provider.getConfig();
        var store = factory.getStore();
        var metadata = store.getResponseBound(type).get(mapping);

        dto.setType(DocDTOType.response);
        dto.setMapping(mapping);

        if(metadata != null) {
            var bound = metadata.getBoundClass();
            var descriptionAnnotation = bound.getAnnotation(Description.class);
            var exampleAnnotation = bound.getAnnotation(Example.class);

            dto.setIsRaw(false);
            dto.setBase(bound);
            dto.setName(bound.getSimpleName());

            if(exampleAnnotation != null) {
                dto.setExample(exampleAnnotation.value());
            }

            if(descriptionAnnotation != null) {
                dto.setDescription(descriptionAnnotation.value());
            }

            for(var metaBound: metadata.getBound()) {
                if(metaBound.isAnnotated(JsonIgnore.class)) {
                    continue;
                }

                dto.addField(metaBound, this);
            }
        } else {
            var descriptionAnnotation = type.getAnnotation(Description.class);
            var exampleAnnotation = type.getAnnotation(Example.class);

            dto.setIsRaw(true);
            dto.setBase(type);
            dto.setName(type.getSimpleName());

            if(exampleAnnotation != null) {
                dto.setExample(exampleAnnotation.value());
            }

            if(descriptionAnnotation != null) {
                dto.setDescription(descriptionAnnotation.value());
            }

            for(var field: ReflectionUtils.scanFields(type)) {
                if(field.isAnnotationPresent(JsonIgnore.class)) {
                    continue;
                }

                dto.addField(new BoundedSource(field, Set.of(field.getDeclaredAnnotations())), this);
            }
        }

        return dto;
    }

    /**
     * Обойти дерево мутаторов, и найти подходящей для текущего типа сущности
     *
     * @param type тип сущности для поиска мутатора
     * @return документация для мутатора
     */
    public DocDTO shakeRequestDto(Class<?> type) {
        var dto = api.getDto(type);

        if(dto != null) {
            dto.setType(dto.getType() == DocDTOType.response ? DocDTOType.composite : DocDTOType.request);

            return dto;
        } else {
            dto = api.makeDTO(type);
        }

        var factory = provider.getConfig();
        var store = factory.getStore();
        var metadata = store.getMutatorBound(type);
        var descriptionAnnotation = type.getAnnotation(Description.class);
        var exampleAnnotation = type.getAnnotation(Example.class);

        dto.setType(DocDTOType.request);
        dto.setMapping(null);
        dto.setBase(type);

        if(exampleAnnotation != null) {
            dto.setExample(exampleAnnotation.value());
        }

        if(descriptionAnnotation != null) {
            dto.setDescription(descriptionAnnotation.value());
        }

        dto.setIsRaw(metadata.getBound().size() == 0);

        if(dto.getIsRaw()) {
            var fields = ReflectionUtils.scanFields(type, Set.of(Object.class));

            for (var field: fields) {
                if(field.isAnnotationPresent(JsonIgnore.class)) {
                    continue;
                }

                dto.addField(new BoundedSource(field, Set.of(field.getDeclaredAnnotations())), this);
            }
        } else {
            for (var metaBound : metadata.getFound()) {
                if(metaBound.isAnnotated(JsonIgnore.class)) {
                    continue;
                }

                dto.addField(metaBound, this);
            }
        }

        return dto;
    }
}
