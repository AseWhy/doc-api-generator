package io.github.asewhy.apidoc.support;

import io.github.asewhy.apidoc.support.bag.FormatterContext;
import io.github.asewhy.apidoc.support.interfaces.iDocProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class DocController implements iDocProvider {
    private String description;
    private String name;
    private String auth;
    private List<String> path;
    private List<DocMethod> methods = new ArrayList<>();

    public void addMethod(DocMethod method) {
        methods.add(method);
    }

    public List<String> getAbsolutePath(@NotNull List<String> path, FormatterContext context) {
        var result = new ArrayList<String>();

        for(var current: this.path) {
            if(path.size() != 0) {
                for (var subpart : path) {
                    result.add(context.formatPath(current) + context.formatPath(subpart));
                }
            } else {
                result.add(context.formatPath(current));
            }
        }

        return result;
    }

    public List<String> getPath(List<String> path, FormatterContext context) {
        var result = new ArrayList<String>();

        for(var current: path) {
            result.add(context.formatPath(current));
        }

        return result;
    }

    @Override
    public void pushHtmlDocumentation(@NotNull StringBuilder builder, @NotNull FormatterContext context) {
        builder.append(context.makeStartDetailsBlock(""));

        builder.append(context.makeHeaderSummary(name));

        if(description != null) {
            builder.append(context.makeParagraph(context.processDescription(description)));
        }

        builder.append(context.makeStartListBlock());

        builder.append(context.makeListItem("Название: ".concat(name)));

        if(auth != null) {
            builder.append(context.makeListItem("Авторизация: ".concat(auth)));
        }

        if(path != null) {
            builder.append(context.makeListItem("Путь: ".concat(String.join(", ", getPath(path, context)))));
        }

        builder.append(context.makeEndListBlock());

        if(methods != null && methods.size() != 0) {
            context = context.addHeader();

            for(var method: methods) {
                method.pushHtmlDocumentation(builder, context.addHeader());

                builder.append("\n");
            }
        }

        builder.append(context.makeEndDetailsBlock());
    }
}
