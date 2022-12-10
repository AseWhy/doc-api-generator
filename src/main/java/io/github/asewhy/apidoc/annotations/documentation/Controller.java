package io.github.asewhy.apidoc.annotations.documentation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    /**
     * Информация о методах авторизации для доступа к контроллеру
     *
     * @return информация о методах авторизации для доступа к контроллеру
     */
    Security[] security();
}
