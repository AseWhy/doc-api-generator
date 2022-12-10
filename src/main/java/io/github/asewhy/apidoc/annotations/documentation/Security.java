package io.github.asewhy.apidoc.annotations.documentation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Security {
    /**
     * Название метода авторизации, который будет использован при генерации документации
     *
     * @return название метода авторизации
     */
    String value();

    /**
     * Области действия (необязательно)
     *
     * @return области действия
     */
    String[] scopes();
}
