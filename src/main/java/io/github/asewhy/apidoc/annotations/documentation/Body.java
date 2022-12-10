package io.github.asewhy.apidoc.annotations.documentation;

import java.lang.annotation.*;

/**
 * Пометить параметр как тело запроса, при создании документации этот парметр будет использован как тело запроса
 */
@Documented
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Body {

}
