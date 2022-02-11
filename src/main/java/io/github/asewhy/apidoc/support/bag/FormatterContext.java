package io.github.asewhy.apidoc.support.bag;

import io.github.asewhy.apidoc.DocumentationUtils;
import io.github.asewhy.apidoc.support.interfaces.iBaseHtmlProvider;
import io.github.asewhy.conversions.ConversionFactoryInternal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class FormatterContext implements iBaseHtmlProvider {
    private final String tabState;
    private final Integer headTabIndex;
    private final ConversionFactoryInternal factory;

    public String format(String name, Class<?> clazz) {
        return factory.getCallbackNameStrategy().convert(name, clazz);
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

    public @NotNull String processDescriptionComment(@NotNull String description) {
        return DocumentationUtils.REFERENCES_NEWLINE_PATTERN
            .matcher(processDescription(description))
        .replaceAll("$1" + this.tabState + " * $2");
    }

    public @NotNull String processDescription(@NotNull String description) {
        return DocumentationUtils.REFERENCES_PATTERN
            .matcher(description.trim())
        .replaceAll(this.makeDtoLink("$1"));
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
