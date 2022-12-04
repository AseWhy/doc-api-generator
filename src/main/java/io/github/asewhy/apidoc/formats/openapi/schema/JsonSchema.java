package io.github.asewhy.apidoc.formats.openapi.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JsonSchema {
    private String description;
}
