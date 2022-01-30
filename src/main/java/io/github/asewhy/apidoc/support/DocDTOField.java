package io.github.asewhy.apidoc.support;

import io.github.asewhy.apidoc.DocumentationUtils;
import io.github.asewhy.apidoc.components.StoreShakeService;
import io.github.asewhy.apidoc.annotations.Description;
import io.github.asewhy.apidoc.support.bag.FormatterContext;
import io.github.asewhy.apidoc.support.interfaces.iDocProvider;
import io.github.asewhy.conversions.ConversionMutator;
import io.github.asewhy.conversions.ConversionResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@Getter
@Setter
@ToString
public class DocDTOField implements iDocProvider {
    private String name;
    private String description;
    private DocDTO parent;
    private DocDTO reference;
    private Field javaField;

    public DocDTOField(@NotNull Field field, DocDTO parent, StoreShakeService store) {
        this.parent = parent;
        this.javaField = field;
        this.name = field.getName();

        var type = field.getType();
        var description = field.getAnnotation(Description.class);

        if(description != null) {
            this.description = description.value();
        }

        if(ConversionMutator.class.isAssignableFrom(type)) {
            this.reference = store.shakeRequestDto(type);
        }

        if(ConversionResponse.class.isAssignableFrom(type)) {
            this.reference = store.shakeResponseDto(type, parent.getMapping());
        }
    }

    /**
     * Преобразовать строку в TS тип
     *
     * @param context контекст форматирования, с инструментами форматирования
     * @return сформированная строка с типом typescript
     */
    public void pushHtmlDocumentation(@NotNull StringBuilder builder, @NotNull FormatterContext context) {
        var info = DocumentationUtils.javaFieldToTypescriptField(javaField);

        builder
            .append(context.tabState())
            .append("/**\n")
            .append(context.tabState())
            .append(" * ")
            .append(name)
        .append("\n");

        if(description != null) {
            builder.append(context.tabState())
                .append(" *\n")
                .append(context.tabState())
                .append(" * ")
                .append(context.processDescription(description))
            .append("\n");
        }

        if(info.comment() != null) {
            builder.append(context.tabState())
                .append(" *\n")
                .append(context.tabState())
                .append(" * ")
                .append(info.comment())
            .append("\n");
        }

        builder
            .append(context.tabState())
            .append(" */\n")
            .append(context.tabState())
            .append(context.format(name, javaField.getDeclaringClass()))
            .append(": ")
            .append(info.typeDec())
        .append(";");
    }
}
