package io.github.asewhy.apidoc.annotations;

import java.lang.annotation.*;

/**
 * Пометить метод или контроллер как скрытый от документирования (если какие-то методы не публичные например)
 */
@Documented
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Hidden {
}
