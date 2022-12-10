package io.github.asewhy.apidoc.descriptor.info.flows;

import lombok.Builder;
import lombok.ToString;

import java.util.Map;

@ToString
public class ClientCredentialsFlow extends Flow {
    private final String tokenUrl;

    @Builder
    public ClientCredentialsFlow(String refreshUrl, Map<String, String> scopes, String tokenUrl) {
        super(refreshUrl, scopes);

        this.tokenUrl = tokenUrl;
    }
}
