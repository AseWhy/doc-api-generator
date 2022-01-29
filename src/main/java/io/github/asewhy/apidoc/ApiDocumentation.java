package io.github.asewhy.apidoc;

import io.github.asewhy.apidoc.support.interfaces.iDocumentedApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.github.asewhy.apidoc.components")
public class ApiDocumentation {
    @Autowired
    protected iApiDocumentationFactory apiDocumentationFactory;

    @Bean
    @ConditionalOnMissingBean(iDocumentedApi.class)
    public iDocumentedApi provideDocApi() {
        return apiDocumentationFactory.provideDocApi();
    }
}
