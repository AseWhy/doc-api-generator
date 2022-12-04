package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiOperation {
    private Set<String> tags;
    private String summary;
    private String description;
    private OpenApiExternalDocs externalDocs;
    private Set<OpenApiParameter> parameters;
    private OpenApiBodyInfo requestBody;
    private Map<String, OpenApiBodyInfo> responses;
    private Boolean deprecated;
    private Map<String, List<String>> security;
}
