package io.github.asewhy.apidoc.support;

import io.github.asewhy.apidoc.support.bag.FormatterContext;
import io.github.asewhy.apidoc.support.enums.DocMethodType;
import io.github.asewhy.apidoc.support.interfaces.iDocProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.List;

@Getter
@Setter
@ToString
public class DocMethod implements iDocProvider {
    private String name;
    private String description;
    private String auth;
    private List<String> path;
    private List<RequestMethod> methods;
    private List<DocVariable> variables;
    private DocController controller;
    private DocMethodType type;
    private Method original;
    private DocDTO body;
    private DocDTO response;

    @Override
    public void pushHtmlDocumentation(@NotNull StringBuilder builder, @NotNull FormatterContext context) {
        builder.append(context.makeStartDetailsBlock(""));

        builder.append(context.makeHeaderSummary(name));

        if(description != null) {
            builder.append(context.makeParagraph(context.processDescription(description)));
        }

        builder.append(context.makeStartListBlock());

        builder.append(context.makeListItem("Название: ".concat(name)));

        if(original != null) {
            var clazz = original.getDeclaringClass();

            if(clazz.isInterface()) {
                builder.append(context.makeListItem("Тип: Метод интерфейса " + clazz.getSimpleName()));
            } else {
                builder.append(context.makeListItem("Тип: Фактический"));
            }
        } else {
            builder.append(context.makeListItem("Тип: Фактический"));
        }

        if(methods != null) {
            builder.append(context.makeListItem("Методы: ".concat(String.join(", ", methods.stream().map(Enum::toString).toList()))));
        }

        if(path != null) {
            builder.append(context.makeListItem("Путь в контроллере: ".concat(context.formatPath(String.join(", ", controller.getPath(path, context))))));
        }

        if(path != null) {
            builder.append(context.makeListItem("Полный путь: ".concat(context.formatPath(String.join(", ", controller.getAbsolutePath(path, context))))));
        }

        if(auth != null) {
            builder.append(context.makeListItem("Авторизация: ".concat(auth)));
        }

        if(body != null) {
            builder.append(context.makeListDtoLink("Тело запроса:", body.getName()));
        }

        if(response != null) {
            builder.append(context.makeListDtoLink("Тело ответа:", response.getName()));
        } else {
            builder.append(context.makeListItem("Тело ответа: Неизвестно (Невозможно распознать или Void)"));
        }

        builder.append(context.makeEndListBlock());

        if(variables != null && variables.size() != 0) {
            builder.append(context.addHeader().makeHeader("Переменные пути"));
            builder.append(context.makeStartListBlock());

            for (var variable : variables) {
                builder.append(context.makeStartList());
                variable.pushHtmlDocumentation(builder, context.addHeader());
                builder.append(context.makeEndList());
            }

            builder.append(context.makeEndListBlock());
        }

        builder.append(context.makeEndDetailsBlock());
    }
}
