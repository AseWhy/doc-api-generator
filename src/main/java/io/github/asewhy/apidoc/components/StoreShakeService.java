package io.github.asewhy.apidoc.components;

import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.annotations.Description;
import io.github.asewhy.apidoc.annotations.ForceDocumented;
import io.github.asewhy.apidoc.iApiDocumentationFactory;
import io.github.asewhy.apidoc.support.DocController;
import io.github.asewhy.apidoc.support.DocDTO;
import io.github.asewhy.apidoc.support.DocMethod;
import io.github.asewhy.apidoc.support.DocVariable;
import io.github.asewhy.apidoc.support.interfaces.iDocumentedApi;
import io.github.asewhy.conversions.ConversionProvider;
import io.github.asewhy.conversions.ConversionUtils;
import io.github.asewhy.conversions.support.annotations.ConvertMutator;
import io.github.asewhy.conversions.support.annotations.ConvertRequest;
import io.github.asewhy.conversions.support.annotations.ConvertResponse;
import io.github.asewhy.conversions.support.annotations.ShiftController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class StoreShakeService {
    @Autowired
    protected ConversionProvider provider;
    @Autowired
    protected ApplicationContext context;
    @Autowired
    protected iDocumentedApi api;
    @Autowired
    protected iApiDocumentationFactory apiDocumentationFactory;

    @PostConstruct
    void init() {
        if(!apiDocumentationFactory.requireApiInitialize()) {
            apiDocumentationFactory.afterApiInitialize(api);
            return;
        }

        var factory = provider.getFactory();
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
            shakeController(controller);
        }

        apiDocumentationFactory.afterApiInitialize(api);
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

        var methods = ReflectionUtils.scanMethods(type);

        for(var method: methods) {
            var request = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            var mapping = getMappingName(method);

            if(canProcess(method, mapping) && request != null) {
                result.addMethod(shakeControllerMethod(method, result, request, mapping));
            }
        }

        var request = AnnotatedElementUtils.findMergedAnnotation(type, RequestMapping.class);
        var description = type.getAnnotation(Description.class);

        if(request != null) {
            result.setPath(List.of(request.path()));

            if(!request.name().isBlank()) {
                result.setName(request.name());
            } else {
                result.setName(type.getSimpleName());
            }
        } else {
            result.setName(type.getSimpleName());
        }

        if(description != null) {
            result.setDescription(description.value());
        }

        return result;
    }

    /**
     * Обойти метод контроллера
     *
     * @param controllerMethod метод для обхода
     * @param controller контроллер метода
     * @param request аннотация RequestMapping
     * @param mapping маппинг
     * @return документация метода
     */
    public DocMethod shakeControllerMethod(@NotNull Method controllerMethod, DocController controller, RequestMapping request, String mapping) {
        var factory = provider.getFactory();
        var store = factory.getStore();

        var returnType = controllerMethod.getReturnType();
        var parameters = controllerMethod.getParameters();
        var description = controllerMethod.getAnnotation(Description.class);

        var method = new DocMethod();
        var resolver = store.findResolver(returnType);
        var response = getRequestParameter(parameters);

        if(resolver != null) {
            returnType = resolver.getConversionReference(controllerMethod);
        }

        if(response != null) {
            method.setBody(shakeRequestDto(response));
        }

        if(returnType != null) {
            method.setResponse(shakeResponseDto(returnType, mapping));
        }

        method.setMethods(List.of(request.method()));
        method.setName(controllerMethod.getName());
        method.setVariables(scanVariables(parameters));
        method.setPath(List.of(request.path()));
        method.setOriginal(controllerMethod);
        method.setController(controller);

        if(description != null) {
            method.setDescription(description.value());
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
                parameter.isAnnotationPresent(ConvertRequest.class)
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
    private boolean canProcess(@NotNull Method method, String mapping) {
        var result = method.getReturnType();
        var generic = ReflectionUtils.findXGeneric(method.getGenericReturnType());
        var store = provider.getFactory().getStore();

        if(method.isAnnotationPresent(ForceDocumented.class)) {
            return true;
        }

        if(result == Object.class || result == Void.class || Map.class == result) {
            return true;
        }

        if(store.isPresentResponse(result)) {
            return true;
        } else {
            return provider.canResolveResponse(result, generic, mapping);
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
            return dto;
        } else {
            dto = api.makeDTO(type);
        }

        var factory = provider.getFactory();
        var store = factory.getStore();
        var metadata = store.getResponseBound(type).get(mapping);

        dto.setIsResponse(true);
        dto.setMapping(mapping);

        if(metadata != null) {
            var bound = metadata.getBoundClass();

            dto.setIsRaw(false);
            dto.setBase(bound);
            dto.setName(bound.getSimpleName());

            for(var field: metadata.getBoundFields()) {
                dto.addField(field, this);
            }
        } else {
            dto.setIsRaw(true);
            dto.setBase(type);
            dto.setName(type.getSimpleName());

            for(var field: ReflectionUtils.scanFields(type, Set.of(Object.class))) {
                dto.addField(field, this);
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
            return dto;
        } else {
            dto = api.makeDTO(type);
        }

        var factory = provider.getFactory();
        var store = factory.getStore();
        var metadata = store.getMutatorBound(type);

        dto.setIsResponse(false);
        dto.setMapping(null);
        dto.setBase(type);

        if(metadata.getFoundFields().size() == 0) {
            dto.setIsRaw(true);

            for (var field : ReflectionUtils.scanFields(type, Set.of(Object.class))) {
                dto.addField(field, this);
            }
        } else {
            dto.setIsRaw(false);

            for (var field : metadata.getFoundFields()) {
                dto.addField(field, this);
            }
        }

        return dto;
    }
}
