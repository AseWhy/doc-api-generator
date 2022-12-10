package io.github.asewhy.apidoc.descriptor.info;

import io.github.asewhy.apidoc.descriptor.info.support.ApiHttpSecurityScheme;
import io.github.asewhy.apidoc.descriptor.info.support.ApiSecurityType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiHttpSecurity extends ApiSecurity {
    private final ApiHttpSecurityScheme scheme;

    @Builder
    public ApiHttpSecurity(String name, String description, ApiHttpSecurityScheme scheme) {
        super(ApiSecurityType.http, name, description);

        this.scheme = scheme;
    }
}
