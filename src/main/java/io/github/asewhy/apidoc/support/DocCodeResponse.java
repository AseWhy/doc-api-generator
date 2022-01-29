package io.github.asewhy.apidoc.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DocCodeResponse {
    private Integer code;
    private String description;
}
