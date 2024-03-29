package io.github.asewhy.apidoc.formats;

import io.github.asewhy.apidoc.formats.service.OpenApiDocConvertService;
import io.github.asewhy.apidoc.formats.support.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ConversionServiceProvider {
    @Autowired
    protected OpenApiDocConvertService openApiDocConvertService;

    public ConversionService<?> provide(String type) {
        if(Objects.equals(type, "openapi")) {
            return openApiDocConvertService;
        } else {
            return null;
        }
    }
}
