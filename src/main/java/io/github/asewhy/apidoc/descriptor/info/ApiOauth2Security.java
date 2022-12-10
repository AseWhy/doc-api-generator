package io.github.asewhy.apidoc.descriptor.info;

import io.github.asewhy.apidoc.descriptor.info.flows.*;
import io.github.asewhy.apidoc.descriptor.info.support.ApiSecurityType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@ToString
public class ApiOauth2Security extends ApiSecurity {
    private static final Set<String> CANNOT_FLOW = Set.of("implicit", "authorizationCode", "password", "clientCredentials");

    private final Map<String, Flow> flows;

    @Builder
    public ApiOauth2Security(String name, String description) {
        super(ApiSecurityType.oauth2, name, description);

        this.flows = new HashMap<>();
    }

    public void addImplicitFlow(ImplicitFlow flow) {
        flows.put("implicit", flow);
    }

    public void addAuthorizationCodeFlow(AuthorizationCodeFlow flow) {
        flows.put("authorizationCode", flow);
    }

    public void addPasswordFlow(PasswordFlow flow) {
        flows.put("password", flow);
    }

    public void addClientCredentialsFlow(ClientCredentialsFlow flow) {
        flows.put("clientCredentials", flow);
    }

    public void addCustomFlow(String name, Flow flow) {
        if(CANNOT_FLOW.contains(name)) {
            throw new RuntimeException("Cannot add flow with name like " + name);
        }

        flows.put(name, flow);
    }

    public Map<String, Flow> getFlows() {
        return Collections.unmodifiableMap(flows);
    }
}
