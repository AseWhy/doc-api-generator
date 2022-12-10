package io.github.asewhy.apidoc.annotations.documentation;

import org.springframework.http.HttpStatus;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Response {
    /**
     * Описать статус, который может вернуть текущий метод
     *
     * @return информация о статусе, которым может ответить этот метод
     */
    HttpStatus value();

    /**
     * Описание медиа типа возвращаемого значения
     *
     * @return описание медиа типа возвращаемого значения
     */
    String mediaType();

    /**
     * Описание возвращаемого значения
     *
     * @return описание возвращаемого значения
     */
    Description description();
}
