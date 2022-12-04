package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.asewhy.apidoc.formats.openapi.schema.JsonSchema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiMediaType {
    private JsonSchema schema;
    private Object example;
    private String encoding;
}
