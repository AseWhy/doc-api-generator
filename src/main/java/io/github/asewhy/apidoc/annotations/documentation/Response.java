package io.github.asewhy.apidoc.annotations.documentation;

import io.github.asewhy.apidoc.annotations.Default;
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
    HttpStatus value() default HttpStatus.OK;

    /**
     * Описание медиа типа возвращаемого значения
     *
     * @return описание медиа типа возвращаемого значения
     */
    String mediaType() default "application/json";

    /**
     * Тип ответа
     *
     * @return тип ответа
     */
    Class<?> type() default Default.class;

    /**
     * Описание возвращаемого значения
     *
     * @return описание возвращаемого значения
     */
    Description description() default @Description(value = "Успешный ответ");
}
