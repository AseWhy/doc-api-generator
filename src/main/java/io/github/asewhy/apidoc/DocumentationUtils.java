package io.github.asewhy.apidoc;

import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.support.bag.TsTypeInfo;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public class DocumentationUtils {
    public static Pattern REFERENCES_PATTERN = Pattern.compile("\\[([aA-zZ0-9_$]+)]");
    public static Pattern REFERENCES_NEWLINE_PATTERN = Pattern.compile("(\n\r|\n)\s*([^\n]+)");

    /**
     * Преобразовать значение класса в значение TypeScript типа
     *
     * @param type класс
     * @return информация о TypeScript типе
     */
    public static @NotNull TsTypeInfo javaFieldToTypescriptField(Class<?> type) {
        if(type == null) {
            return new TsTypeInfo("<N/A>", null);
        }

        if(Number.class.isAssignableFrom(type)) {
            return new TsTypeInfo("number", null);
        } else if(String.class.isAssignableFrom(type)) {
            return new TsTypeInfo("string", null);
        } else if(Boolean.class.isAssignableFrom(type)) {
            return new TsTypeInfo("boolean", null);
        } else if(Map.class.isAssignableFrom(type)) {
            return new TsTypeInfo("any", null);
        } else if(Enum.class.isAssignableFrom(type)) {
            var builder = new StringBuilder();
            var firstPassed = false;

            for(var current: type.getEnumConstants()) {
                if(firstPassed) {
                    builder.append(" | ");
                }

                builder.append('"').append(current).append('"');

                firstPassed = true;
            }

            return new TsTypeInfo(builder.toString(), "enum " + type.getSimpleName());
        } else if(LocalDate.class.isAssignableFrom(type)) {
            return new TsTypeInfo("string", "Date-Time [yyyy-MM-DD]");
        } else if(LocalDateTime.class.isAssignableFrom(type)) {
            return new TsTypeInfo("string", "Local-Date-Time [yyyy-MM-dd'T'HH:mm:ss.SSSX]");
        } else if(ZonedDateTime.class.isAssignableFrom(type)) {
            return new TsTypeInfo("string", "Zoned-Date-Time [yyyy-MM-dd'T'HH:mm:ss.SSSXZ]. Formats: RFC_1123_DATE_TIME, ISO_OFFSET_DATE_TIME, ISO_ZONED_DATE_TIME. Default zone is " + ZoneId.systemDefault());
        }

        return new TsTypeInfo(type.getSimpleName(), null);
    }

    /**
     * Преобразовать тип поля в значение TypeScript типа
     *
     * @param field после
     * @return информация о TypeScript типе
     */
    public static @NotNull TsTypeInfo javaFieldToTypescriptField(@NotNull Field field) {
        var type = field.getType();

        if(Collection.class.isAssignableFrom(type)) {
            var collectionType = ReflectionUtils.findXGeneric(field);
            var ofType = javaFieldToTypescriptField(collectionType);

            return new TsTypeInfo(ofType.typeDec() + "[]", ofType.comment());
        } else {
            return javaFieldToTypescriptField(type);
        }
    }

    /**
     * Преобразовать аннотацию вида @com.example.Name(first="Duke", middle="of", last="Java") в @Name(first="Duke", middle="of", last="Java")
     *
     * @param annotationDeclaration исходный текст аннотации
     * @return форматированное значение аннотации
     */
    public static @NotNull String formatAnnotation(String annotationDeclaration) {
        return annotationDeclaration.replaceAll("(@)[aA-zZ.]+\\.([^.]+)", "$1$2");
    }
}
