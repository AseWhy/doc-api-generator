package io.github.asewhy.apidoc.descriptor.info;

import io.github.asewhy.apidoc.descriptor.info.support.ApiKeySecurityIn;
import io.github.asewhy.apidoc.descriptor.info.support.ApiSecurityType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiKeySecurity extends ApiSecurity {
    private final ApiKeySecurityIn in;
    private final String inName;

    @Builder
    public ApiKeySecurity(String name, ApiKeySecurityIn in, String inName, String description) {
        super(ApiSecurityType.http, name, description);

        this.inName = inName;
        this.in = in;
    }
}
