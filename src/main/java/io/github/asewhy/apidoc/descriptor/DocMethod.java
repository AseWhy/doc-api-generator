package io.github.asewhy.apidoc.descriptor;

import io.github.asewhy.apidoc.annotations.documentation.Response;
import io.github.asewhy.apidoc.annotations.documentation.Security;
import io.github.asewhy.apidoc.descriptor.enums.DocMethodType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class DocMethod {
    private String name;
    private String description;
    private Boolean deprecated;
    private Set<Security> securities;
    private Set<Response> responses;
    private List<String> path;
    private List<RequestMethod> methods;
    private List<DocVariable> variables;
    private DocController controller;
    private DocMethodType type;
    private String example;
    private Method original;
    private DocDTO body;
    private DocDTO response;

    public String getDescription() {
        return description != null ? description.trim() : null;
    }
}
