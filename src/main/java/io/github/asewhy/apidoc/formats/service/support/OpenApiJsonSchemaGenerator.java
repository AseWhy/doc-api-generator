package io.github.asewhy.apidoc.formats.service.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.asewhy.apidoc.descriptor.DocDTO;
import io.github.asewhy.apidoc.descriptor.DocDTOField;
import io.github.asewhy.apidoc.formats.openapi.schema.JsonSchema;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public interface OpenApiJsonSchemaGenerator {
    /**
     * Получить схему для простого типа (Строки, Энума, числа, булевого значения, даты)
     *
     * @param type тип
     * @return схема
     */
    JsonSchema getSchemaForSimpleType(Class<?> type);

    /**
     * Получить схему для поля
     *
     * @param field поле для генерации схемы
     * @return сгенерированная схема
     */
    JsonSchema getSchemaForField(@NotNull DocDTOField field);

    /**
     * Создать экземпляр схемы для объекта DTO
     *
     * @param dto объект dto
     * @return схема
     */
    Map<String, JsonSchema> getSchemaForDto(@NotNull DocDTO dto) throws JsonProcessingException;

    /**
     * Получить схему для типа type
     *
     * @param type     тип для которого нужно получить схему
     * @param excludes массив исключений, для них схема в глубину генерироваться не будет.
     * @return схема
     */
    JsonSchema getSchemaForType(Class<?> type, Set<Class<?>> excludes);
}
