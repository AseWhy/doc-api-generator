package io.github.asewhy.apidoc.descriptor;

import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.descriptor.info.ApiInfo;
import io.github.asewhy.apidoc.descriptor.interfaces.IAnnotationRegistration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class DocumentedApi implements IAnnotationRegistration {
    private final ApiInfo info;

    private Map<Class<?>, DocController> controllers = new HashMap<>();
    private Map<Class<?>, DocDTO> dataTransferObjects = new HashMap<>();
    private Map<Class<?>, DocAnnotation<?>> annotations = new HashMap<>();

    public DocDTO getDto(Class<?> clazz) {
        return ReflectionUtils.findOnClassMap(dataTransferObjects, clazz);
    }
    public DocController getController(Class<?> clazz) {
        return ReflectionUtils.findOnClassMap(controllers, clazz);
    }

    public DocDTO makeDTO(Class<?> clazz) {
        var dto = new DocDTO(clazz, this);
        this.dataTransferObjects.put(clazz, dto);
        return dto;
    }

    public DocController makeController(Class<?> clazz) {
        var dto = new DocController();
        this.controllers.put(clazz, dto);
        return dto;
    }

    public <T> DocAnnotation<T> getAnnotation(Class<T> clazz) {
        return (DocAnnotation<T>) ReflectionUtils.findOnClassMap(annotations, clazz);
    }

    @Override
    public <T> void registerAnnotation(Class<T> clazz, Function<T, String> description) {
        var dto = new DocAnnotation<>(clazz, description);
        this.annotations.put(clazz, dto);
    }
}
