package io.github.asewhy.apidoc.components;

import io.github.asewhy.apidoc.iApiDocumentationFactory;
import io.github.asewhy.apidoc.support.interfaces.iDocumentedApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class ApiController {
    @Autowired
    protected iDocumentedApi api;
    @Autowired
    protected RequestMappingHandlerMapping handlerMapping;
    @Autowired
    protected TemplateDataProvider templateDataProvider;
    @Autowired
    protected iApiDocumentationFactory apiDocumentationFactory;

    @PostConstruct
    private void initMappings() throws NoSuchMethodException {
        handlerMapping.registerMapping(
            RequestMappingInfo.paths(apiDocumentationFactory.provideDocApiPath())
                .methods(RequestMethod.GET)
                .produces(MediaType.TEXT_HTML_VALUE)
            .build(),
            this,
            this.getClass().getMethod("getDocumentation")
        );
    }

    //
    // Api methods
    //

    public ResponseEntity<String> getDocumentation() throws IOException {
        return new ResponseEntity<>(templateDataProvider.getFormattedTemplate(api), HttpStatus.OK);
    }
}
