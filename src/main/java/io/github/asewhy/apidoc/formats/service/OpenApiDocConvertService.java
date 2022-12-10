package io.github.asewhy.apidoc.formats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.asewhy.apidoc.DocumentationUtils;
import io.github.asewhy.apidoc.IApiDocumentationConfiguration;
import io.github.asewhy.apidoc.descriptor.DocMethod;
import io.github.asewhy.apidoc.descriptor.DocumentedApi;
import io.github.asewhy.apidoc.descriptor.info.ApiInfo;
import io.github.asewhy.apidoc.descriptor.info.ApiSecurity;
import io.github.asewhy.apidoc.formats.openapi.*;
import io.github.asewhy.apidoc.formats.openapi.schema.JsonSchema;
import io.github.asewhy.apidoc.formats.openapi.schema.ObjectSchema;
import io.github.asewhy.apidoc.formats.openapi.schema.ReferenceSchema;
import io.github.asewhy.apidoc.formats.openapi.support.OpenApiDest;
import io.github.asewhy.apidoc.formats.support.IConversionService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@ConditionalOnBean(IApiDocumentationConfiguration.class)
public class OpenApiDocConvertService implements IConversionService<OpenApi> {
    @Autowired
    protected IApiDocumentationConfiguration config;
    @Autowired
    protected OpenApiJsonSchemaCreator jsonSchemaCreator;

    @Override
    public OpenApi convert(@NotNull DocumentedApi api) {
        return OpenApi.builder()
            .openapi("3.0.3")
            .info(getInfo(api.getInfo()))
            .tags(getTags(api))
            .paths(getPaths(api))
            .components(getComponents(api))
        .build();
    }

    /**
     * Получить информацию об апи в формате openapi
     *
     * @param info инфорация об апи
     * @return информация об api в формате openapi
     */
    private OpenApiInfo getInfo(@NotNull ApiInfo info) {
        return OpenApiInfo.builder()
            .title(info.getName())
            .description(info.getDescription())
            .contact(getContactsInfo(info))
            .license(getLicenseInfo(info))
            .version(info.getVersion())
        .build();
    }

    /**
     * Получить ионфмацию о лицензии
     *
     * @param info инфомрация о лицензии
     * @return информация о лицензии
     */
    private OpenApiLicense getLicenseInfo(@NotNull ApiInfo info) {
        if(info.getLicenseName() == null && info.getLicenseUrl() == null) {
            return null;
        }

        return OpenApiLicense.builder()
            .name(info.getLicenseName())
            .url(info.getLicenseUrl())
        .build();
    }

    /**
     * Получаем инфу о контактах
     *
     * @param info ифнормация об апи
     * @return информация о контактах
     */
    private OpenApiContacts getContactsInfo(@NotNull ApiInfo info) {
        if(info.getContactName() == null && info.getContactEmail() == null && info.getContactWebsite() == null) {
            return null;
        }

        return OpenApiContacts.builder()
            .name(info.getContactName())
            .email(info.getContactEmail())
            .url(info.getContactWebsite())
        .build();
    }

    /**
     * Получить информацию о контроллерах
     *
     * @param api текущий апи приложения
     * @return информация о тегах
     */
    private @NotNull List<OpenApiTag> getTags(@NotNull DocumentedApi api) {
        var tags = new ArrayList<OpenApiTag>();
        var controllers = api.getControllers();

        controllers.values()
            .stream()
            .sorted((a, b) -> CharSequence.compare(Objects.requireNonNullElse(a.getName(), ""), Objects.requireNonNullElse(b.getName(), "")))
        .forEach(current -> {
            tags.add(
                OpenApiTag.builder()
                    .name(current.getName())
                    .description(current.getDescription())
                .build()
            );
        });

        return tags;
    }

    /**
     * Поставить информацию о типах апи
     *
     * @param api текущий апи приложения
     * @return инфомрация о типах приложения
     */
    private OpenApiComponents getComponents(DocumentedApi api) {
        return OpenApiComponents.builder()
            .schemas(getComponentsSchemas(api))
            .securitySchemes(getSecuritySchemas(api))
        .build();
    }

    /**
     * Поставить информацию о безопасности api
     *
     * @param api апи
     * @return информация о безопасности api
     */
    private Map<String, ApiSecurity> getSecuritySchemas(DocumentedApi api) {
        var security = api.getSecurity();

        if(security == null) {
            return null;
        }

        var received = security.getSecurities();
        var computed = new HashMap<String, ApiSecurity>();

        for(var current: received.entrySet()) {
            var name = current.getKey();
            var value = current.getValue();

            computed.put(name, value);
        }

        return computed;
    }

    /**
     * Поставить информацию о типах апи
     *
     * @param api текущий апи приложения
     * @return инфомрация о типах приложения
     */
    private @NotNull Map<String, JsonSchema> getComponentsSchemas(DocumentedApi api) {
        var schemas = new HashMap<String, JsonSchema>();
        var objects = api.getDataTransferObjects();

        for(var current: objects.values()) {
            try {
                schemas.putAll(jsonSchemaCreator.getSchemaForDto(current));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Cannot process json.", e);
            }
        }

        return schemas;
    }

    /**
     * Поставить информацию о маршрутах апи
     *
     * @param api текущий апи приложения
     * @return инфомрация о маршрутах приложения
     */
    private @NotNull Map<String, OpenApiPath> getPaths(@NotNull DocumentedApi api) {
        var paths = new HashMap<String, OpenApiPath>();
        var controllers = api.getControllers();
        var globalSecurity = api.getSecurity();

        for(var controller: controllers.values()) {
            var methods = controller.getMethods();

            for(var method: methods) {
                var absolute = DocumentationUtils.formatPath(String.join("/", controller.getAbsolutePath(method.getPath())));
                var found = paths.get(absolute);

                if(found == null){
                    found = new OpenApiPath();
                }

                var description = method.getDescription();
                var securityInfo = method.getSecurities();
                var security = new HashSet<Map<String, List<String>>>();

                if(securityInfo.isEmpty()) {
                    var def = globalSecurity.getDef();

                    if(def != null) {
                        security.add(Collections.singletonMap(def.getName(), Collections.emptyList()));
                    }
                } else {
                    for (var current : securityInfo) {
                        var securityName = current.value();
                        var securityData = globalSecurity.getSecurity(securityName);

                        if(securityData == null) {
                            throw new RuntimeException("Cannot find security info by name " + securityName);
                        }

                        security.add(
                            Collections.singletonMap(
                                securityName,
                                current.scopes().length == 0 ? Collections.emptyList() : List.of(current.scopes())
                            )
                        );
                    }
                }

                var operation = OpenApiOperation.builder()
                    .summary(method.getName())
                    .description(description)
                    .deprecated(method.getDeprecated())
                    .tags(Set.of(controller.getName()))
                    .parameters(getMethodParameters(method))
                    .security(security)

                    .requestBody(getMethodRequestBody(method))
                    .responses(getMethodResponsesBody(method))
                .build();

                for(var httpMethod: method.getMethods()) {
                    switch (httpMethod) {
                        case GET: found.setGet(operation); break;
                        case DELETE: found.setDelete(operation); break;
                        case HEAD: found.setHead(operation); break;
                        case OPTIONS: found.setOptions(operation); break;
                        case PATCH: found.setPath(operation); break;
                        case PUT: found.setPut(operation); break;
                        case POST: found.setPost(operation); break;
                        case TRACE: found.setTrace(operation); break;
                    }
                }

                paths.put(absolute, found);
            }
        }

        return paths;
    }

    /**
     * Получить информацию о типах ответа который прийдет в ответ на запрос на этот метод
     *
     * @param method метод
     * @return информация о типе сущности ответа
     */
    @Contract(pure = true)
    private @NotNull Map<String, OpenApiBodyInfo> getMethodResponsesBody(@NotNull DocMethod method) {
        var body = method.getResponse();
        var responses = new HashMap<String, OpenApiBodyInfo>();
        var content = new HashMap<String, Object>();

        if(body == null) {
            content.put("application/json", new OpenApiSchema(ObjectSchema.builder().build()));

            responses.put("200",
                OpenApiBodyInfo.builder()
                    .content(content)
                    .description("Успешный ответ")
                .build()
            );
        } else {
            content.put("application/json", new OpenApiSchema(new ReferenceSchema("#/components/schemas/" + body.getName())));

            responses.put("200",
                OpenApiBodyInfo.builder()
                    .content(content)
                    .description(Objects.requireNonNullElse(body.getDescription(), "Успешный ответ"))
                .build()
            );
        }

        return responses;
    }

    /**
     * Поставить информацию о типе сущности необходимой для запроса
     *
     * @param method метод
     * @return информация о типе сущности необходимой для зпроса
     */
    private @Nullable OpenApiBodyInfo getMethodRequestBody(@NotNull DocMethod method) {
        var body = method.getBody();

        if(body == null) {
            return null;
        }

        var content = new HashMap<String, Object>();

        content.put("application/json", new OpenApiSchema(new ReferenceSchema("#/components/schemas/" + body.getName())));

        return OpenApiBodyInfo.builder()
            .required(false)
            .content(content)
            .description(body.getDescription())
        .build();
    }

    /**
     * Поставить информацию о параметрах метода апи
     *
     * @param method метод апи
     * @return информация о парметре
     */
    private @NotNull Set<OpenApiParameter> getMethodParameters(@NotNull DocMethod method) {
        var parameters = new HashSet<OpenApiParameter>();
        var controller =  method.getController();
        var controllerOriginal = controller.getOriginal();

        for(var current: method.getVariables()) {
            var parameter = current.getParameter();
            var original = parameter.getType();
            var generic = GenericTypeResolver.resolveType(parameter.getParameterizedType(), controllerOriginal);

            if(generic instanceof Class<?>) {
                original = (Class<?>) generic;
            }

            parameters.add(
                OpenApiParameter.builder()
                    .required(current.getRequired())
                    .in(OpenApiDest.path)
                    .name(current.getName())
                    .schema(jsonSchemaCreator.getSchemaForSimpleType(original))
                    .description(current.getDescription())
                .build()
            );
        }

        return parameters;
    }
}
