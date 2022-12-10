package io.github.asewhy.apidoc.descriptor.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.asewhy.apidoc.descriptor.info.support.ApiSecurityType;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ApiSecurity {
    private final ApiSecurityType type;
    @JsonIgnore
    private final String name;
    private String description;
}
