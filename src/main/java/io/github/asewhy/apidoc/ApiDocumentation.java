package io.github.asewhy.apidoc;

import io.github.asewhy.apidoc.descriptor.DocumentedApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(IApiDocumentationConfiguration.class)
@ComponentScan("io.github.asewhy.apidoc")
public class ApiDocumentation {
    @Autowired
    protected IApiDocumentationConfiguration config;

    @Bean
    @ConditionalOnMissingBean(DocumentedApi.class)
    public DocumentedApi provideDocApi() {
        return new DocumentedApi(config.docApi());
    }
}
