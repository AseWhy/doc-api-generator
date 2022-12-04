package io.github.asewhy.apidoc.annotations;

import io.github.asewhy.apidoc.ApiDocumentation;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ApiDocumentation.class})
public @interface EnableApiDocGeneration {

}
