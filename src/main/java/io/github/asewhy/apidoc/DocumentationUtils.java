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

public class DocumentationUtils {
    /**
     * Преобразовать значение класса в значение TypeScript типа
     *
     * @param type класс
     * @return информация о TypeScript типе
     */
    public static @NotNull TsTypeInfo javaFieldToTypescriptField(Class<?> type) {
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
            var ofType = javaFieldToTypescriptField(ReflectionUtils.findXGeneric(field));

            return new TsTypeInfo(ofType.typeDec() + "[]", ofType.comment());
        } else {
            return javaFieldToTypescriptField(type);
        }
    }
}
