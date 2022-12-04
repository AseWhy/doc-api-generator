package io.github.asewhy.apidoc;

import org.jetbrains.annotations.NotNull;

public class DocumentationUtils {
    /**
     * Отфрматировать путь
     *
     * @param path путь для форматорования
     * @return отформатированный путь
     */
    public static @NotNull String formatPath(@NotNull String path) {
        if(path.isBlank()) {
            return "/";
        }

        if(path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        if(!path.startsWith("/")) {
            path = "/" + path;
        }

        return path;
    }
}
