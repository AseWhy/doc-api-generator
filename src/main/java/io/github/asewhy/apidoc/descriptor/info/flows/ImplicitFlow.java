package io.github.asewhy.apidoc.descriptor.info.flows;

import lombok.Builder;
import lombok.ToString;

import java.util.Map;

@ToString
public class ImplicitFlow extends Flow {
    private final String authorizationUrl;

    @Builder
    public ImplicitFlow(String refreshUrl, Map<String, String> scopes, String authorizationUrl) {
        super(refreshUrl, scopes);

        this.authorizationUrl = authorizationUrl;
    }
}
