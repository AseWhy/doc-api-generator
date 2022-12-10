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
    Response[] response();

    /**
     * Информация о методах авторизации для доступа к методу
     *
     * @return информация о методах авторизации для доступа к методу
     */
    Security[] security();
}
