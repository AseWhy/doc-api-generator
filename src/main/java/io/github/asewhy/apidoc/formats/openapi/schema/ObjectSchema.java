package io.github.asewhy.apidoc.formats.openapi.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ObjectSchema extends JsonSchema {
    private final String type = "object";
    private Set<String> required;
    private Map<String, JsonSchema> properties;
    private boolean additionalProperties = true;
}
