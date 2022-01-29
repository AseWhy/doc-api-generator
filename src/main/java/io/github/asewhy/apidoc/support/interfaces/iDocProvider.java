package io.github.asewhy.apidoc.support.interfaces;

import io.github.asewhy.apidoc.support.bag.FormatterContext;

public interface iDocProvider {
    void pushHtmlDocumentation(StringBuilder builder, FormatterContext context);

    default String getHtmlDocumentation(FormatterContext context) {
        var builder = new StringBuilder();

        pushHtmlDocumentation(builder, context);

        return builder.toString();
    }
}
