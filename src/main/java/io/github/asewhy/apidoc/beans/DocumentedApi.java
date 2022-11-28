package io.github.asewhy.apidoc.beans;

import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.support.DocAnnotation;
import io.github.asewhy.apidoc.support.DocController;
import io.github.asewhy.apidoc.support.DocDTO;
import io.github.asewhy.apidoc.support.bag.FormatterContext;
import io.github.asewhy.apidoc.support.interfaces.iDocumentedApi;
import io.github.asewhy.conversions.ConversionConfigurationInternal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class DocumentedApi implements iDocumentedApi {
    private final String name;

    private Map<Class<?>, DocController> controllers = new HashMap<>();
    private Map<Class<?>, DocDTO> dataTransferObjects = new HashMap<>();
    private Map<Class<?>, DocAnnotation<?>> annotations = new HashMap<>();

    @Autowired
    protected ConversionConfigurationInternal config;

    @Override
    public DocDTO getDto(Class<?> clazz) {
        return ReflectionUtils.findOnClassMap(dataTransferObjects, clazz);
    }

    @Override
    public DocDTO makeDTO(Class<?> clazz) {
        var dto = new DocDTO(clazz, this);
        this.dataTransferObjects.put(clazz, dto);
        return dto;
    }

    @Override
    public DocController getController(Class<?> clazz) {
        return ReflectionUtils.findOnClassMap(controllers, clazz);
    }

    @Override
    public DocController makeController(Class<?> clazz) {
        var dto = new DocController();
        this.controllers.put(clazz, dto);
        return dto;
    }

    @Override
    public <T> DocAnnotation<T> getAnnotation(Class<T> clazz) {
        return (DocAnnotation<T>) ReflectionUtils.findOnClassMap(annotations, clazz);
    }

    @Override
    public <T> DocAnnotation<T> makeAnnotation(Class<T> clazz, Function<T, String> description) {
        var dto = new DocAnnotation<>(clazz, description);
        this.annotations.put(clazz, dto);
        return dto;
    }

    @Override
    public void pushHtmlDocumentation(@NotNull StringBuilder builder, @NotNull FormatterContext context) {
        builder.append(context.makeHeader("Api ".concat(name)));

        builder.append(context.makeStartLogicalBlock("api no-bordered"));

        context = context.addHeader();

        builder.append(context.makeHeader("Контроллеры"));

        builder.append(context.makeStartLogicalBlock("controllers no-bordered"));

        for(var controller: controllers.values()) {
            controller.pushHtmlDocumentation(builder, context.addHeader());

            builder.append("\n");
        }

        builder.append(context.makeEndLogicalBlock());

        builder.append(context.makeHeader("ДТО"));

        builder.append(context.makeStartLogicalBlock("dtos no-bordered"));

        for(var object: dataTransferObjects.values()) {
            object.pushHtmlDocumentation(builder, context.addHeader());
        }

        builder.append(context.makeEndLogicalBlock());

        builder.append(context.makeEndLogicalBlock());
    }

    @Override
    public String getDocumentation() {
        return getHtmlDocumentation(new FormatterContext("", 1, config));
    }
}
