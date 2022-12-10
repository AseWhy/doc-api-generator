package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.github.asewhy.apidoc.descriptor.info.ApiSecurity;
import io.github.asewhy.apidoc.formats.openapi.schema.JsonSchema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiComponents {
    @JsonPropertyOrder(alphabetic = true)
    private Map<String, JsonSchema> schemas;
    @JsonPropertyOrder(alphabetic = true)
    private Map<String, ApiSecurity> securitySchemes;
}
