package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiPath {
    private String summary;
    private String description;
    private OpenApiOperation get;
    private OpenApiOperation put;
    private OpenApiOperation post;
    private OpenApiOperation delete;
    private OpenApiOperation options;
    private OpenApiOperation head;
    private OpenApiOperation path;
    private OpenApiOperation trace;
    private Set<OpenApiParameter> parameters;
}
