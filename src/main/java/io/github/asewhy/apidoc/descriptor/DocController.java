package io.github.asewhy.apidoc.descriptor;

import io.github.asewhy.apidoc.DocumentationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class DocController {
    private String description;
    private String name;
    private String auth;
    private Class<?> original;
    private List<String> path;
    private List<DocMethod> methods = new ArrayList<>();

    public void addMethod(DocMethod method) {
        methods.add(method);
    }

    public List<String> getAbsolutePath(@NotNull List<String> path) {
        var result = new ArrayList<String>();

        for(var current: this.path) {
            if(path.size() != 0) {
                for (var subpart : path) {
                    result.add(DocumentationUtils.formatPath(current) + DocumentationUtils.formatPath(subpart));
                }
            } else {
                result.add(DocumentationUtils.formatPath(current));
            }
        }

        return result;
    }

    public List<String> getPath(List<String> path) {
        var result = new ArrayList<String>();

        for(var current: path) {
            result.add(DocumentationUtils.formatPath(current));
        }

        return result;
    }

    public String getDescription() {
        return description != null ? description.trim() : null;
    }
}
