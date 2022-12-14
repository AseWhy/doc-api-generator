package io.github.asewhy.apidoc.config;

import io.github.asewhy.apidoc.ApiDocumentationConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@ConditionalOnBean(ApiDocumentationConfiguration.class)
public class DocumentationSpringAutoconfigure implements WebMvcConfigurer {
    @Autowired
    protected ApiDocumentationConfiguration configuration;

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        var path = configuration.docsPath();

        while(path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        var chunks = path.split("/");

        if(chunks.length > 1) {
            path = String.join("/", Arrays.copyOf(chunks, chunks.length - 1));
        } else {
            path = "/";
        }

        registry
            .addResourceHandler(path + "/**/*")
            .addResourceLocations("classpath:/META-INF/resources/")
            .addResourceLocations("classpath:/META-INF/resources/static/")
            .addResourceLocations("classpath:/META-INF/static/")
        .addResourceLocations("classpath:static/");
    }
}
