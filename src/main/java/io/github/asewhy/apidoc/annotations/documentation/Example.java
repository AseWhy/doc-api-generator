package io.github.asewhy.apidoc.annotations.documentation;

import java.lang.annotation.*;

/**
 * Предоставить пример использования, или пример запроса
 */
@Documented
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Example {
    String value() default "";
}
