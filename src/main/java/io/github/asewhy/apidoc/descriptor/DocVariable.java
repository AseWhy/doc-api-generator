package io.github.asewhy.apidoc.descriptor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Parameter;

@Getter
@Setter
@ToString
public class DocVariable {
    private String name;
    private String description;
    private Boolean required;
    private Object annotation;
    private Parameter parameter;

    public String getDescription() {
        return description != null ? description.trim() : null;
    }
}
