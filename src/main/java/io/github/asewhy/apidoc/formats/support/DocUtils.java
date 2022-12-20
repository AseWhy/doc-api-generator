package io.github.asewhy.apidoc.formats.support;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Date;

public class DocUtils {
    /**
     * Можно ли обрабатывать тип как просто тип
     *
     * @param type тип
     * @return true если можно
     */
    public static boolean isSimpleType(Class<?> type) {
        return Integer.class.isAssignableFrom(type) ||
            Long.class.isAssignableFrom(type) ||
            Number.class.isAssignableFrom(type) ||
            Boolean.class.isAssignableFrom(type) ||
            Temporal.class.isAssignableFrom(type) ||
            Enum.class.isAssignableFrom(type) ||
            Instant.class.isAssignableFrom(type) ||
            Date.class.isAssignableFrom(type) ||
        String.class.isAssignableFrom(type);
    }
}
