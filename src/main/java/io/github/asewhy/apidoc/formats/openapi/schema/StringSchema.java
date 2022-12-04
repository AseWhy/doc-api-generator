package io.github.asewhy.apidoc.formats.openapi.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StringSchema extends JsonSchema {
    private final String type = "string";
    private final String format;
}
