package io.github.asewhy.apidoc.support.interfaces;

import io.github.asewhy.apidoc.support.DocAnnotation;
import io.github.asewhy.apidoc.support.DocController;
import io.github.asewhy.apidoc.support.DocDTO;

import java.util.function.Function;

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

    /**
     * Создать документацию для аннтоации класса clazz
     *
     * @param clazz класс для которого нужно получить документацию аннотации
     * @return документация для аннотации класса
     */
    <T> DocAnnotation<T> makeAnnotation(Class<T> clazz, Function<T, String> description);

    /**
     * Получить документацию для аннтоации класса clazz
     *
     * @param clazz класс для которого нужно получить документацию аннотации
     * @return документация для аннотации класса
     */
    <T> DocAnnotation<T> getAnnotation(Class<T> clazz);

    String getDocumentation();

    String getName();

    String toString();
}
