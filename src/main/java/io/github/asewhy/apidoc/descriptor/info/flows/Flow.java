package io.github.asewhy.apidoc.descriptor.info.flows;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Map;

@ToString
@AllArgsConstructor
public abstract class Flow {
    private String refreshUrl;
    private Map<String, String> scopes;
}
