package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.asewhy.apidoc.formats.openapi.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class OpenApiSchema {
    private JsonSchema schema;
}
