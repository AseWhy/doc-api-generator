package io.github.asewhy.apidoc.descriptor.info.flows;

import lombok.Builder;
import lombok.ToString;

import java.util.Map;

@ToString
public class AuthorizationCodeFlow extends Flow {
    private final String tokenUrl;
    private final String authorizationUrl;

    @Builder
    public AuthorizationCodeFlow(String refreshUrl, Map<String, String> scopes, String tokenUrl, String authorizationUrl) {
        super(refreshUrl, scopes);

        this.authorizationUrl = authorizationUrl;
        this.tokenUrl = tokenUrl;
    }
}
