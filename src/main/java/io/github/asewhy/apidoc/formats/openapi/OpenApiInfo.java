package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OpenApiInfo {
    private String title;
    private String description;
    private String termsOfService;
    private String version;
    private OpenApiContacts contacts;
    private OpenApiLicense license;
}
