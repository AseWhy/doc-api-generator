package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiTag {
    private String name;
    private String description;
    private OpenApiExternalDocs externalDocs;
}
