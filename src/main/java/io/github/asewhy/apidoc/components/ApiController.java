package io.github.asewhy.apidoc.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.asewhy.apidoc.IApiDocumentationConfiguration;
import io.github.asewhy.apidoc.descriptor.DocumentedApi;
import io.github.asewhy.apidoc.formats.ConversionServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;

@Component
public class ApiController {
    @Autowired
    protected DocumentedApi api;
    @Autowired
    protected RequestMappingHandlerMapping handlerMapping;
    @Autowired
    protected IApiDocumentationConfiguration configuration;
    @Autowired
    protected ConversionServiceProvider convertService;

    @PostConstruct
    private void initMappings() throws NoSuchMethodException {
        handlerMapping.registerMapping(
            RequestMappingInfo.paths(configuration.docsPath())
                .methods(RequestMethod.GET)
            .build(),
            this,
            this.getClass().getMethod("getDocumentationPage")
        );

        handlerMapping.registerMapping(
            RequestMappingInfo.paths(configuration.apiPath())
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_VALUE)
            .build(),
            this,
            this.getClass().getMethod("getDocumentation", String.class)
        );
    }

    //
    // Documentation endpoint
    //

    public ResponseEntity<String> getDocumentation(@PathVariable("type") String type) throws JsonProcessingException {
        var service = convertService.provide(type);

        if(service == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
            configuration.objectMapper().writeValueAsString(service.convert(api)),
            HttpStatus.OK
        );
    }

    public ResponseEntity<String> getDocumentationPage() {
        return new ResponseEntity<>(
            "<!DOCTYPE html>\n" +
            "<html lang=\"ru\">\n" +
            "  <head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>" + configuration.api().getName() + "</title>\n" +
            "    <link rel=\"stylesheet\" type=\"text/css\" href=\"swagger-ui.css\" />\n" +
            "    <link rel=\"stylesheet\" type=\"text/css\" href=\"swagger.css\" />\n" +
            "    <link rel=\"icon\" type=\"image/png\" href=\"favicon-32x32.png\" sizes=\"32x32\" />\n" +
            "    <link rel=\"icon\" type=\"image/png\" href=\"favicon-16x16.png\" sizes=\"16x16\" />\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"swagger-ui\"></div>\n" +
            "    <script src=\"swagger-ui-bundle.js\" charset=\"UTF-8\"> </script>\n" +
            "    <script src=\"swagger-ui-standalone-preset.js\" charset=\"UTF-8\"> </script>\n" +
            "    <script type=\"text/javascript\">\n" +
            "      window.onload = function() {\n" +
            "        window.ui = SwaggerUIBundle({\n" +
            "          url: \"" + configuration.apiPath().replace("{type}", "openapi") + "\",\n" +
            "          dom_id: '#swagger-ui',\n" +
            "          deepLinking: true,\n" +
            "          presets: [\n" +
            "            SwaggerUIBundle.presets.apis,\n" +
            "            SwaggerUIStandalonePreset\n" +
            "          ],\n" +
            "          plugins: [\n" +
            "            SwaggerUIBundle.plugins.DownloadUrl\n" +
            "          ],\n" +
            "          layout: \"StandaloneLayout\"\n" +
            "        });\n" +
            "      };\n" +
            "    </script>\n" +
            "  </body>\n" +
            "</html>\n",
            HttpStatus.OK
        );
    }
}
