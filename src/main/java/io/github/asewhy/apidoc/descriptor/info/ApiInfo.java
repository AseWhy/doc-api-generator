package io.github.asewhy.apidoc.descriptor.info;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
public class ApiInfo {
    private final String name;
    private String description;
    private String termsOfService;
    private String contactEmail;
    private String contactWebsite;
    private String contactName;
    private String licenseName;
    private String licenseUrl;
    private String version;

    public ApiInfo(String name) {
        this.name = name;
        this.version = "0.0.1";
    }

    public ApiInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }
}
