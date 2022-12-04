package io.github.asewhy.apidoc.descriptor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DocCodeResponse {
    private Integer code;
    private String description;

    public String getDescription() {
        return description != null ? description.trim() : null;
    }
}
