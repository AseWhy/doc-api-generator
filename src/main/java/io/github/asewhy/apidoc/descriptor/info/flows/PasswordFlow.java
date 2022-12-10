package io.github.asewhy.apidoc.descriptor.info.flows;

import lombok.Builder;
import lombok.ToString;

import java.util.Map;

@ToString
public class PasswordFlow extends Flow {
    private final String tokenUrl;

    @Builder
    public PasswordFlow(String refreshUrl, Map<String, String> scopes, String tokenUrl) {
        super(refreshUrl, scopes);

        this.tokenUrl = tokenUrl;
    }
}
