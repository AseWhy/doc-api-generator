package io.github.asewhy.apidoc.formats.service.support;

import io.github.asewhy.apidoc.formats.openapi.schema.JsonSchema;

/**
 * Фабрика схем для простых java типов (Которые не DTO)
 */
public interface OpenApiSimpleTypeSchemaFactory {
    /**
     * Создать схему из типа который был предоставлен методом getType
     *
     * @param type тип
     * @param context контекст для создания типа
     * @return схема для этого типа
     */
    JsonSchema create(Class<?> type, OpenApiJsonSchemaGenerator context);

    /**
     * Получить тип, который необходим фабрике для создания схемы
     *
     * @return тип
     */
    Class<?> getType();
}
