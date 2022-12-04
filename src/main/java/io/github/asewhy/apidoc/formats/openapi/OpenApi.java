package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApi {
    private String openapi;
    private OpenApiInfo info;
    private List<OpenApiTag> tags;
    private Map<String, OpenApiPath> paths;
    private OpenApiComponents components;
}
