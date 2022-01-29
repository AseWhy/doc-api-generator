package io.github.asewhy.apidoc.support.interfaces;

import io.github.asewhy.apidoc.support.DocController;
import io.github.asewhy.apidoc.support.DocDTO;

public interface iDocumentedApi extends iDocProvider {
    /**
     * Получить документация для ДТО
     *
     * @param clazz класс ДТО
     * @return объект документации дто
     */
    DocDTO getDto(Class<?> clazz);

    /**
     * Создать документацию ДТО
     *
     * @param clazz класс ДТО
     * @return документация ДТО
     */
    DocDTO makeDTO(Class<?> clazz);

    /**
     * Получить документация для контроллера
     *
     * @param clazz класс контроллера
     * @return объект документации контроллера
     */
    DocController getController(Class<?> clazz);

    /**
     * Создать документацию контроллера
     *
     * @param clazz класс контроллера
     * @return документация контроллера
     */
    DocController makeController(Class<?> clazz);

    String getDocumentation();

    String getName();

    String toString();
}
