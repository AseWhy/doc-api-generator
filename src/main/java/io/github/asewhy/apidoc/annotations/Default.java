package io.github.asewhy.apidoc.annotations;

import java.lang.annotation.*;

/**
 * Говорит использовать результат выполнения метода как сущность ответа
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
}
