package io.github.asewhy.apidoc.support;

import io.github.asewhy.apidoc.support.bag.FormatterContext;
import io.github.asewhy.apidoc.support.interfaces.iDocProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;

@Getter
@Setter
@ToString
public class DocVariable implements iDocProvider {
    private String name;
    private String description;
    private Boolean required;
    private Object annotation;
    private Parameter parameter;

    @Override
    public void pushHtmlDocumentation(@NotNull StringBuilder builder, @NotNull FormatterContext context) {
        builder.append(name);

        if(description != null) {
            builder.append(" - ").append(description);
        }

        if(required) {
            builder.append(" (обязательно)");
        }
    }
}
