package io.github.asewhy.apidoc.formats.service;

import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.IApiDocumentationConfiguration;
import io.github.asewhy.apidoc.descriptor.DocDTO;
import io.github.asewhy.apidoc.descriptor.DocDTOField;
import io.github.asewhy.apidoc.descriptor.DocumentedApi;
import io.github.asewhy.apidoc.formats.openapi.schema.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@ConditionalOnBean(IApiDocumentationConfiguration.class)
public class OpenApiJsonSchemaCreator {
    protected final DocumentedApi api;
    protected final IApiDocumentationConfiguration config;

    /**
     * Новый инстанс сервиса создания json схемы
     *
     * @param api инстанс апи
     * @param config конфигурация
     */
    public OpenApiJsonSchemaCreator(DocumentedApi api, @NotNull IApiDocumentationConfiguration config) {
        this.api = api;
        this.config = config;
    }

    /**
     * Получить описание для поля field
     *
     * @param field полед ля получения описания
     * @return описание поля
     */
    public String getFieldDescription(@NotNull DocDTOField field) {
        var annotations = field.getBound()
            .getAnnotations()
            .stream()
            .map(e -> new AbstractMap.SimpleEntry<>(e, api.getAnnotation(e.annotationType())))
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

    /**
     * Получить схему для поля
     *
     * @param field поле для генерации схемы
     * @return сгенерированная схема
     */
    public JsonSchema getSchemaForField(@NotNull DocDTOField field) {
        var bound = field.getBound();
        var javaType = bound.getType();
        var schema = (JsonSchema) null;

        if(Collection.class.isAssignableFrom(javaType)) {
            var items = (JsonSchema) null;
            var firstGeneric = bound.findXGeneric();

            if(firstGeneric == null) {
                firstGeneric = Object.class;
            }

            var genericDto = api.getDto(firstGeneric);

            if(genericDto != null) {
                items = new ReferenceSchema("#/components/schemas/" + genericDto.getName());
            } else {
                items = ObjectSchema.builder().build();
            }

            schema = ArraySchema.builder()
                .items(items)
            .build();
        } else if(Enum.class.isAssignableFrom(javaType)) {
            var constants = (Object[]) javaType.getEnumConstants();

            schema = EnumSchema.builder()
                .value(
                    Arrays.stream(constants)
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                    .collect(Collectors.toSet())
                )
            .build();
        } else if(
            Integer.class.isAssignableFrom(javaType) ||
            Long.class.isAssignableFrom(javaType)
        ) {
            schema = IntegerSchema.builder()
                .format(Integer.class.isAssignableFrom(javaType) ? "int32" : "int64")
            .build();
        } else if(Number.class.isAssignableFrom(javaType)) {
            schema = NumberSchema.builder().build();
        } else if(Boolean.class.isAssignableFrom(javaType)) {
            schema = BooleanSchema.builder().build();
        } else if(
            Temporal.class.isAssignableFrom(javaType) ||
            Instant.class.isAssignableFrom(javaType) ||
            Date.class.isAssignableFrom(javaType)
        ) {
            schema = StringSchema.builder()
                .format("date-time")
            .build();
        } else if(String.class.isAssignableFrom(javaType)) {
            schema = StringSchema.builder().build();
        } else {
            var reference = field.getReference();

            if(reference != null) {
                schema = new ReferenceSchema("#/components/schemas/" + reference.getName());
            } else {
                schema = ObjectSchema.builder().build();
            }
        }

        if(schema != null) {
            schema.setDescription(getFieldDescription(field));
        }

        return schema;
    }

    /**
     * Создать инстанс схемы для объекта DTO
     *
     * @param dto объект dto
     * @return схема
     */
    public Map<String, JsonSchema> getSchemaForDto(@NotNull DocDTO dto) {
        var schemas = new HashMap<String, JsonSchema>();
        var javaType = dto.getFrom();

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
                }

                schemas.put(
                    dto.getName(),
                    ArraySchema.builder()
                        .items(items)
                    .build()
                );
            }
        } else if(Enum.class.isAssignableFrom(javaType)) {
            schemas.put(
                dto.getName(),
                EnumSchema.builder()
                    .value(
                        Arrays.stream((Object[]) javaType.getEnumConstants())
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                        .collect(Collectors.toSet())
                    )
                .build()
            );
        } else if(
            Integer.class.isAssignableFrom(javaType) ||
            Long.class.isAssignableFrom(javaType)
        ) {
            schemas.put(
                dto.getName(),
                IntegerSchema.builder()
                    .format(Integer.class.isAssignableFrom(javaType) ? "int32" : "int64")
                .build()
            );
        } else if(Number.class.isAssignableFrom(javaType)) {
            schemas.put(dto.getName(), NumberSchema.builder().build());
        } else if(Boolean.class.isAssignableFrom(javaType)) {
            schemas.put(dto.getName(), BooleanSchema.builder().build());
        } else if(String.class.isAssignableFrom(javaType)) {
            schemas.put(dto.getName(), StringSchema.builder().build());
        } else if(
            Temporal.class.isAssignableFrom(javaType) ||
            Instant.class.isAssignableFrom(javaType) ||
            Date.class.isAssignableFrom(javaType)
        ) {
            schemas.put(
                dto.getName(),
                StringSchema.builder()
                    .format("date-time")
                .build()
            );
        } else {
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

        for(var schema: schemas.values()) {
            if(schema != null) {
                var description = dto.getDescription();

                if(description != null && !description.isEmpty()) {
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
}
