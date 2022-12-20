package io.github.asewhy.apidoc.annotations.documentation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Method {
    /**
     * Информация о возвращаемых значениях этого метода
     *
     * @return информация о возвращаемых значениях этого метода
     */
    Response[] response() default {};

    /**
     * Информация о методах авторизации для доступа к методу
     *
     * @return информация о методах авторизации для доступа к методу
     */
    Security[] security() default {};

    /**
     * Описание метода апи (если метод аннотирован ещё и аннотацией description то будет выбрано первое не пустое описание (в приоритете то которое в аннотации Method))
     *
     * @return описание метода апи
     */
    Description description() default @Description();
}
