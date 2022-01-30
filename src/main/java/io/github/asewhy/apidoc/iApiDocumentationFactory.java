package io.github.asewhy.apidoc;

import io.github.asewhy.apidoc.support.interfaces.iDocumentedApi;

public interface iApiDocumentationFactory {
    /**
     * Получить экземпляр документации api
     *
     * @return экземпляр документации api
     */
    iDocumentedApi provideDocApi();

    /**
     * Вызывается после автоматического добавления всех имеющихся в api данных
     *
     * @param api поставляемая документация api
     */
    default void afterApiInitialize(iDocumentedApi api) {

    }

    /**
     * Получить эндпоинт, по которому будет доступна документация
     *
     * @return эндпоинт документации
     */
    default String provideDocApiPath() {
        return "/api/docs";
    }

    /**
     * Нужна ли инициализация api по умолчанию, тоесть если будет возвращен true, то
     * будут добавлены все методы всех контроллеров ShiftController, и все модели имеющиеся на данный момент в сторе
     *
     * @return true если нужна первичная инициализация api
     */
    default boolean requireApiInitialize() {
        return true;
    }
}
