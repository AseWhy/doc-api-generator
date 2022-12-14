package io.github.asewhy.apidoc.descriptor.interfaces;

import java.util.function.Function;

public interface AnnotationRegistration {
    <T> void registerAnnotation(Class<T> clazz, Function<T, String> description);
}
