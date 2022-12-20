package io.github.asewhy.apidoc.formats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.ApiDocumentationConfiguration;
import io.github.asewhy.apidoc.descriptor.DocDTO;
import io.github.asewhy.apidoc.descriptor.DocDTOField;
import io.github.asewhy.apidoc.descriptor.DocumentedApi;
import io.github.asewhy.apidoc.formats.openapi.schema.*;
import io.github.asewhy.apidoc.formats.service.support.OpenApiJsonSchemaGenerator;
import io.github.asewhy.apidoc.formats.service.support.OpenApiSimpleTypeSchemaFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.asewhy.apidoc.formats.support.DocUtils.isSimpleType;

@Service
@ConditionalOnBean(ApiDocumentationConfiguration.class)
public class OpenApiJsonSchemaGeneratorImpl implements OpenApiJsonSchemaGenerator {
    protected final DocumentedApi api;
    protected final ApiDocumentationConfiguration config;
    protected final ObjectMapper mapper;
    protected final Map<Class<?>, OpenApiSimpleTypeSchemaFactory> descriptorFactories;

    @Autowired
    public OpenApiJsonSchemaGeneratorImpl(
        @NotNull DocumentedApi api,
        @NotNull ApiDocumentationConfiguration config,
        @NotNull List<OpenApiSimpleTypeSchemaFactory> descriptorFactories
    ) {
        this.api = api;
        this.config = config;
        this.mapper = config.objectMapper();
        this.descriptorFactories = descriptorFactories
            .stream()
        .collect(Collectors.toMap(OpenApiSimpleTypeSchemaFactory::getType, e -> e));
    }

    /**
     * Попробовать создать схему при помощи пользовательской фабрики
     *
     * @param type тип для которого создаем схему
     * @return схема или null (если null то схема будет создана обычным способом)
     */
    protected JsonSchema tryCreateWithFactory(Class<?> type) {
        var factory = ReflectionUtils.findOnClassMap(descriptorFactories, type);

        if(factory != null) {
            return factory.create(type, this);
        } else {
            return null;
        }
    }

    /**
     * Получить описание для поля field
     *
     * @param field поле для получения описания
     * @return описание поля
     */
    private String getFieldDescription(@NotNull DocDTOField field) {
        var annotations = field.getBound()
            .getAnnotations()
            .stream()
            .map(e -> new AbstractMap.SimpleImmutableEntry<>(e, api.getAnnotation(e.annotationType())))
            .filter(e -> e.getValue() != null)
        .collect(Collectors.toList());

        if(annotations.size() > 0) {
            if(field.getDescription() == null) {
                return annotations
                    .stream()
                    .map(desc -> "<b>" + desc.getValue().descriptor(desc.getKey()) + "</b>")
                .collect(Collectors.joining("\n"));
            } else {
                return field.getDescription() +
                    "\n\n" +
                annotations
                    .stream()
                    .map(desc -> "<b>" + desc.getValue().descriptor(desc.getKey()) + "</b>")
                .collect(Collectors.joining("\n"));
            }

        } else {
            return field.getDescription();
        }
    }

    @Override
    public JsonSchema getSchemaForSimpleType(Class<?> type) {
        var userSchema = tryCreateWithFactory(type);

        if(userSchema != null) {
            return userSchema;
        }

        if(Enum.class.isAssignableFrom(type)) {
            return EnumSchema.builder()
                .value(
                    Arrays.stream((Object[]) type.getEnumConstants())
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                    .collect(Collectors.toSet())
                )
            .build();
        } else if(
            Integer.class.isAssignableFrom(type) ||
            Long.class.isAssignableFrom(type)
        ) {
            return IntegerSchema.builder()
                .format(Integer.class.isAssignableFrom(type) ? "int32" : "int64")
            .build();
        } else if(Number.class.isAssignableFrom(type)) {
            return NumberSchema.builder().build();
        } else if(Boolean.class.isAssignableFrom(type)) {
            return BooleanSchema.builder().build();
        } else if(
            Temporal.class.isAssignableFrom(type) ||
            Instant.class.isAssignableFrom(type) ||
            Date.class.isAssignableFrom(type)
        ) {
            return StringSchema.builder()
                .format("date-time")
            .build();
        } else if(String.class.isAssignableFrom(type)) {
            return StringSchema.builder().build();
        } else {
            return null;
        }
    }

    @Override
    public JsonSchema getSchemaForField(@NotNull DocDTOField field) {
        var bound = field.getBound();
        var javaType = bound.getType();
        var schema = tryCreateWithFactory(javaType);

        if(schema == null) {
            if (Collection.class.isAssignableFrom(javaType)) {
                var items = (JsonSchema) null;
                var firstGeneric = bound.findXGeneric();

                if (firstGeneric == null) {
                    firstGeneric = Object.class;
                }

                var genericDto = api.getDto(firstGeneric);

                if (genericDto != null) {
                    items = new ReferenceSchema("#/components/schemas/" + genericDto.getName());
                } else if (isSimpleType(firstGeneric)) {
                    items = getSchemaForSimpleType(firstGeneric);
                }

                schema = ArraySchema.builder()
                    .items(items)
                .build();
            } else if (isSimpleType(javaType)) {
                schema = getSchemaForSimpleType(javaType);
            } else {
                var reference = field.getReference();

                if (reference != null) {
                    schema = new ReferenceSchema("#/components/schemas/" + reference.getName());
                } else {
                    schema = getSchemaForType(javaType, new HashSet<>());
                }
            }
        }

        if(schema != null && schema.getDescription() == null) {
            schema.setDescription(getFieldDescription(field));
        }

        return schema;
    }

    @Override
    public Map<String, JsonSchema> getSchemaForDto(@NotNull DocDTO dto) throws JsonProcessingException {
        var schemas = new HashMap<String, JsonSchema>();
        var javaType = dto.getFrom();

        var userSchema = tryCreateWithFactory(javaType);

        if(userSchema != null) {
            schemas.put(dto.getName(), userSchema);
        } else {
            if(Collection.class.isAssignableFrom(javaType)) {
                var superType = javaType.getSuperclass();

                if(superType == null) {
                    schemas.put(
                        dto.getName(),
                        ArraySchema.builder()
                            .items(ObjectSchema.builder().build())
                        .build()
                    );
                } else {
                    var items = (JsonSchema) null;
                    var firstGeneric = ReflectionUtils.findXGeneric(superType);

                    if (firstGeneric == null) {
                        firstGeneric = Object.class;
                    }

                    var genericDto = api.getDto(firstGeneric);

                    if (genericDto != null) {
                        items = new ReferenceSchema("#/components/schemas/" + genericDto.getName());
                    } else if(isSimpleType(firstGeneric)) {
                        items = getSchemaForSimpleType(firstGeneric);
                    } else {
                        items = ObjectSchema.builder().build();
                    }

                    schemas.put(
                        dto.getName(),
                        ArraySchema.builder()
                            .items(items)
                        .build()
                    );
                }
            } else if(isSimpleType(javaType)) {
                schemas.put(dto.getName(), getSchemaForSimpleType(javaType));
            }  else {
                var properties = new HashMap<String, JsonSchema>();
                var fields = dto.getFields();

                for(var field: fields) {
                    properties.put(field.getName(), this.getSchemaForField(field));
                }

                schemas.put(
                    dto.getName(),
                    ObjectSchema.builder()
                        .additionalProperties(Map.class.isAssignableFrom(javaType))
                        .required(Collections.emptySet())
                        .properties(properties)
                    .build()
                );
            }
        }

        for(var schema: schemas.values()) {
            if(schema != null) {
                var example = dto.getExample();
                var description = dto.getDescription();

                if(schema.getExample() == null && example != null && !example.isEmpty()) {
                    schema.setExample(mapper.readValue(example.trim(), HashMap.class));
                }

                if(schema.getDescription() == null && description != null && !description.isEmpty()) {
                    schema.setDescription(
                        description +
                        "\n\n" +
                        "mapping: " + dto.getMapping()
                    );
                } else {
                    schema.setDescription("mapping: " + dto.getMapping());
                }
            }
        }

        return schemas;
    }

    @Override
    public JsonSchema getSchemaForType(Class<?> type, Set<Class<?>> excludes) {
        if(Map.class.isAssignableFrom(type)) {
            return ObjectSchema.builder()
                .additionalProperties(true)
            .build();
        }

        var fields = ReflectionUtils.scanFields(type);
        var properties = new HashMap<String, JsonSchema>();

        for(var current: fields) {
            var fieldType = current.getType();

            if(excludes.contains(fieldType)) {
                continue;
            }

            var userSchema = tryCreateWithFactory(fieldType);

            if(userSchema != null) {
                properties.put(
                    current.getName(),
                    userSchema
                );
            } else {
                if(Collection.class.isAssignableFrom(fieldType)) {
                    var items = (JsonSchema) null;
                    var firstGeneric = ReflectionUtils.findXGeneric(current);

                    if(firstGeneric == null) {
                        firstGeneric = Object.class;
                    }

                    var genericDto = api.getDto(firstGeneric);

                    if(genericDto != null) {
                        items = new ReferenceSchema("#/components/schemas/" + genericDto.getName());
                    } else if(isSimpleType(firstGeneric)) {
                        items = getSchemaForSimpleType(firstGeneric);
                    } else {
                        items = ObjectSchema.builder().build();
                    }

                    properties.put(
                        current.getName(),
                        ArraySchema.builder()
                            .items(items)
                        .build()
                    );
                } else if(isSimpleType(fieldType)) {
                    properties.put(current.getName(), getSchemaForSimpleType(fieldType));
                } else {
                    var computedExcludes = new HashSet<>(excludes);
                    computedExcludes.add(fieldType);
                    properties.put(current.getName(), getSchemaForType(fieldType, computedExcludes));
                }
            }
        }

        return ObjectSchema.builder()
            .properties(properties)
            .additionalProperties(false)
        .build();
    }
}
