package io.github.asewhy.apidoc.descriptor.info;

import io.github.asewhy.apidoc.descriptor.info.support.ApiSecurityType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiOpenIdSecurity extends ApiSecurity {
    private final String openIdConnectUrl;

    @Builder
    public ApiOpenIdSecurity(String name, String description, String openIdConnectUrl) {
        super(ApiSecurityType.openIdConnect, name, description);

        this.openIdConnectUrl = openIdConnectUrl;
    }
}
