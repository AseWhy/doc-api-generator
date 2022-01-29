package io.github.asewhy.apidoc.annotations;

import java.lang.annotation.*;

/**
 * Указать описание для элемента взаимодействия с REST
 */
@Documented
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    /**
     * Локализированное описание поставляемых данных, методом, полем, или классом
     *
     * @return локализированное описание
     */
    String value() default "";
}
