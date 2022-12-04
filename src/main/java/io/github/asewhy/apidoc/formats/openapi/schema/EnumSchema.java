package io.github.asewhy.apidoc.formats.openapi.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumSchema extends JsonSchema {
    private final String type = "string";
    @JsonProperty("enum")
    private Set<String> value;
}
