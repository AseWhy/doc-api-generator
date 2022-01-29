package io.github.asewhy.apidoc.components;

import io.github.asewhy.apidoc.support.interfaces.iDocumentedApi;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class TemplateDataProvider {
    private final String template;

    /**
     * Поставщик шаблонов api со стандартным шаблоном template.html
     */
    @Autowired
    public TemplateDataProvider() throws IOException {
        this("template.html");
    }

    /**
     * Поставщик шаблонов api с кастомным шаблоном
     *
     * @param path путь до шаблона
     */
    public TemplateDataProvider(String path) throws IOException {
        var resolves = Path.of(path).normalize().toString();
        var root = new ClassPathResource("./" + resolves);

        if(!root.exists()) {
            throw new FileNotFoundException(path);
        }

        this.template = new String(root.getInputStream().readAllBytes());
    }

    /**
     * Получить форматированный вывод готовой html страницы
     *
     * @param api документация api
     * @return html представление api
     * @throws IOException если произошла ошибка при чтении файла шаблона
     */
    public String getFormattedTemplate(@NotNull iDocumentedApi api) throws IOException {
        var start = System.currentTimeMillis();
        var name = api.getName();
        var content = api.getDocumentation();

        return String.format(template, name, content, System.currentTimeMillis() - start);
    }
}
