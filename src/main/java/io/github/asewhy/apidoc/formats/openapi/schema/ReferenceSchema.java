package io.github.asewhy.apidoc.formats.openapi.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReferenceSchema extends JsonSchema {
    @JsonProperty("$ref")
    private String $ref;

    public ReferenceSchema(String ref) {
        this.$ref = ref;
    }
}
