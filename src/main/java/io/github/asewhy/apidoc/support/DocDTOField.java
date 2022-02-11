package io.github.asewhy.apidoc.support;

import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.DocumentationUtils;
import io.github.asewhy.apidoc.components.StoreShakeService;
import io.github.asewhy.apidoc.annotations.Description;
import io.github.asewhy.apidoc.support.bag.FormatterContext;
import io.github.asewhy.apidoc.support.interfaces.iDocProvider;
import io.github.asewhy.conversions.ConversionMutator;
import io.github.asewhy.conversions.ConversionResponse;
import io.github.asewhy.conversions.support.annotations.MutatorExcludes;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class DocDTOField implements iDocProvider {
    protected static Set<Class<?>> SKIP_ANNOTATIONS = Set.of(Description.class, MutatorExcludes.class);

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
            var pureType = ReflectionUtils.findXGeneric(type, 0);

            if(pureType != null) {
                this.reference = store.shakeResponseDto(pureType, parent.getMapping());
            }
        }
    }

    /**
     * Преобразовать строку в TS тип
     *
     * @param context контекст форматирования, с инструментами форматирования
     */
    public void pushHtmlDocumentation(@NotNull StringBuilder builder, @NotNull FormatterContext context) {
        var info = DocumentationUtils.javaFieldToTypescriptField(javaField);
        var documentation = this.parent.getDocumentation();

        builder
            .append(context.getTabState())
            .append("/**\n")
            .append(context.getTabState())
            .append(" * ")
            .append(name)
        .append("\n");

        if(description != null) {
            builder.append(context.getTabState())
                .append(" *\n")
                .append(context.getTabState())
                .append(" * ")
                .append(context.processDescriptionComment(description))
            .append("\n");
        }

        if(info.getComment() != null) {
            builder.append(context.getTabState())
                .append(" *\n")
                .append(context.getTabState())
                .append(" * ")
                .append(info.getComment())
            .append("\n");
        }

        var annotations = Arrays.stream(javaField.getAnnotations()).filter(type -> !SKIP_ANNOTATIONS.contains(type.annotationType())).collect(Collectors.toList());

        if(annotations.size() > 0) {
            builder.append(context.getTabState()).append(" * \n");

            for (var annotation : annotations) {
                var type = annotation.annotationType();
                var description = documentation.getAnnotation(type);

                builder.append(context.getTabState()).append(" * ");

                if (description != null) {
                    builder.append(
                        Objects.requireNonNullElseGet(
                            description.descriptor(annotation),
                            () -> DocumentationUtils.formatAnnotation(annotation.toString())
                        )
                    );
                } else {
                    builder.append(DocumentationUtils.formatAnnotation(annotation.toString()));
                }

                builder.append("\n");
            }
        }

        builder
            .append(context.getTabState())
            .append(" */\n")
            .append(context.getTabState())
            .append(context.format(name, javaField.getDeclaringClass()))
            .append(": ")
            .append(info.getTypeDec())
        .append(";");
    }
}
