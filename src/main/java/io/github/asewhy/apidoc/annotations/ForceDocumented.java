package io.github.asewhy.apidoc.annotations;

import java.lang.annotation.*;

/**
 * Говорит мапперу силой добавить метод в документацию
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForceDocumented {

}
