package io.github.asewhy.apidoc.support;

import io.github.asewhy.apidoc.annotations.Description;
import io.github.asewhy.apidoc.components.StoreShakeService;
import io.github.asewhy.apidoc.support.bag.FormatterContext;
import io.github.asewhy.apidoc.support.enums.DocDTOType;
import io.github.asewhy.apidoc.support.interfaces.iDocProvider;
import io.github.asewhy.apidoc.support.interfaces.iDocumentedApi;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class DocDTO implements iDocProvider {
    private String name;
    private String mapping;
    private String description;
    private DocDTOType type;
    private Boolean isRaw;
    private iDocumentedApi documentation;
    private Class<?> base;
    private Class<?> from;
    private List<DocDTOField> fields = new ArrayList<>();

    public DocDTO(@NotNull Class<?> from, iDocumentedApi parent) {
        var description = from.getAnnotation(Description.class);

        if(description != null) {
            this.description = description.value();
        }

        if(Object.class == from) {
            this.name = "?" + from.getSimpleName();
        } else {
            this.name = from.getSimpleName();
        }

        this.from = from;
        this.documentation = parent;
    }

    public void addField(Field found, StoreShakeService shakeService) {
        this.fields.add(new DocDTOField(found, this, shakeService));
    }

    @Override
    public void pushHtmlDocumentation(@NotNull StringBuilder builder, @NotNull FormatterContext context) {
        var getTabState = context.getTabState();

        builder.append(context.makeStartDetailsBlock("", true));

        builder.append(context.makeHeaderDTOSummary(name));

        if(description != null) {
            builder.append(context.makeParagraph(description));
        }

        builder.append(context.makeStartListBlock());

        builder.append(context.getTabState()).append(context.makeListItem("Тип: ".concat(type == DocDTOType.response ? "Сущность ответа" : type == DocDTOType.composite ? "Сущность ответа и запроса" : isRaw ? "Неизвестно" : "Сущность запроса")));
        builder.append(context.getTabState()).append(context.makeListItem("Название: ".concat(name)));
        builder.append(context.getTabState()).append(context.makeListItem("Конвертируемая: ".concat(isRaw ? "Нет" : "Да")));

        if(type == DocDTOType.response && mapping != null) {
            builder.append(context.getTabState()).append(context.makeListItem("Маппинг: ".concat(mapping)));
        }

        builder.append(context.makeEndListBlock());

        builder.append(context.makeStartHighlight("typescript"));

        builder
            .append(getTabState)
            .append("/**\n")
            .append(getTabState)
            .append(" * DTO ")
            .append(name)
        .append("\n");

        if(description != null) {
            builder.append(getTabState)
                .append(" *\n")
                .append(getTabState)
                .append(" * ")
                .append(context.processDescriptionComment(description))
            .append("\n");
        }

        builder
            .append(getTabState)
            .append(" */\n")
            .append(getTabState)
            .append("interface ")
            .append(name)
        .append(" {\n");

        if(Object.class == from) {
            builder.append(context.addTab().getTabState()).append("[key: string]: [value: any];\n");
        } else if(Map.class == from) {
            builder.append(context.addTab().getTabState()).append("[key: any]: [value: any];\n");
        } else {
            boolean newline = false;

            for (var field : fields) {
                if (newline) {
                    builder.append("\n");
                }

                field.pushHtmlDocumentation(builder, context.addTab());

                builder.append("\n");

                newline = true;
            }
        }

        builder
            .append(getTabState)
            .append("}")
        .append(context.makeEndHighlight());

        builder.append(context.makeEndDetailsBlock());
    }
}
