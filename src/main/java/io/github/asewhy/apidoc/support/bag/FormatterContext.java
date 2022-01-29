package io.github.asewhy.apidoc.support.bag;

import io.github.asewhy.apidoc.support.interfaces.iBaseHtmlProvider;
import io.github.asewhy.conversions.support.iConversionFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record FormatterContext(
    String tabState,
    Integer headTabIndex,
    iConversionFactory factory
) implements iBaseHtmlProvider {
    public String format(String name) {
        return factory.convertFieldName(name);
    }

    public @NotNull String makeHeader(String content) {
        return iBaseHtmlProvider.super.makeHeader(headTabIndex, content);
    }

    public @NotNull String makeHeaderDTO(String name) {
        return iBaseHtmlProvider.super.makeHeaderDTO(headTabIndex, name);
    }

    public @NotNull String makeHeaderDTOSummary(String name) {
        return iBaseHtmlProvider.super.makeHeaderDTOSummary(headTabIndex, name);
    }

    public @NotNull String makeHeaderSummary(String content) {
        return iBaseHtmlProvider.super.makeHeaderSummary(headTabIndex, content);
    }

    public @NotNull String formatPath(@NotNull String path) {
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

    @Contract(" -> new")
    public @NotNull FormatterContext addTab() {
        return new FormatterContext(
            tabState + "\t",
            headTabIndex,
            factory
        );
    }

    @Contract(" -> new")
    public @NotNull FormatterContext addHeader() {
        return new FormatterContext(
            tabState,
            headTabIndex >= 6 ? headTabIndex : headTabIndex + 1,
            factory
        );
    }
}
