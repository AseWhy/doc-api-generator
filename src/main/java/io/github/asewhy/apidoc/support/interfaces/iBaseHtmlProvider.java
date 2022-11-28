package io.github.asewhy.apidoc.support.interfaces;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface iBaseHtmlProvider {
    @Contract(pure = true)
    default @NotNull String makeHeaderSummary(Integer headTabIndex, String content) {
        return "<summary><h" + headTabIndex + ">" + content + "</h" + headTabIndex + "></summary>";
    }

    @Contract(pure = true)
    default @NotNull String makeHeader(Integer headTabIndex, String content) {
        return "<h" + headTabIndex + ">" + content + "</h" + headTabIndex + ">";
    }

    @Contract(pure = true)
    default @NotNull String makeHeaderDTO(Integer headTabIndex, String dtoName) {
        return "<h" + headTabIndex + " id=\"DTO-PURE-" + dtoName + "\">" + dtoName + "</h" + headTabIndex + ">";
    }

    @Contract(pure = true)
    default @NotNull String makeHeaderDTOSummary(Integer headTabIndex, String dtoName) {
        return "<summary><h" + headTabIndex + " id=\"DTO-PURE-" + dtoName + "\">" + dtoName + "</h" + headTabIndex + "></summary>";
    }

    @Contract(pure = true)
    default @NotNull String makeParagraph(String content) {
        return "<p>" + content + "</p>";
    }

    @Contract(pure = true)
    default @NotNull String makeListItem(String content) {
        return "<li>" + content + "</li>";
    }

    @Contract(pure = true)
    default @NotNull String makeListDtoLink(String prefix, String dtoName) {
        return "<li>" + prefix + " " + makeDtoLink(dtoName) + "</li>";
    }

    @Contract(pure = true)
    default @NotNull String makeDtoLink(String dtoName) {
        return "<a href=\"#DTO-PURE-" + dtoName + "\">ДТО " + dtoName + "</a>";
    }

    @Contract(pure = true)
    default @NotNull String makeBreak() {
        return "</br>";
    }

    @Contract(pure = true)
    default @NotNull String makeStartLogicalBlock(String clazz) {
        return "<div class='logical-block " + clazz + "'>";
    }

    @Contract(pure = true)
    default @NotNull String makeStartDetailsBlock(String clazz, boolean open) {
        return "<details class='logical-block " + clazz + "' " + (open ? "open" : "") + ">";
    }

    @Contract(pure = true)
    default @NotNull String makeStartList() {
        return "<li>";
    }

    @Contract(pure = true)
    default @NotNull String makeEndList() {
        return "</li>";
    }

    @Contract(pure = true)
    default @NotNull String makeStartListBlock() {
        return "<ul>";
    }

    @Contract(pure = true)
    default @NotNull String makeEndListBlock() {
        return "</ul>";
    }

    @Contract(pure = true)
    default @NotNull String makeEndLogicalBlock() {
        return makeEndDiv();
    }

    @Contract(pure = true)
    default @NotNull String makeEndDetailsBlock() {
        return "</details>";
    }

    @Contract(pure = true)
    default @NotNull String makeEndDiv() {
        return "</div>";
    }

    @Contract(pure = true)
    default @NotNull String makeStartHighlight(String lang) {
        return "<pre><code class=\"language-" + lang + "\">";
    }

    @Contract(pure = true)
    default @NotNull String makeEndHighlight() {
        return "</code></pre>";
    }
}
