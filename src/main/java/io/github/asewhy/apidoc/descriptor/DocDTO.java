package io.github.asewhy.apidoc.descriptor;

import io.github.asewhy.apidoc.annotations.documentation.Description;
import io.github.asewhy.apidoc.service.StoreShakeService;
import io.github.asewhy.apidoc.descriptor.enums.DocDTOType;
import io.github.asewhy.conversions.support.Bound;
import io.github.asewhy.conversions.support.Bound;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class DocDTO {
    private String name;
    private String mapping;
    private String description;
    private DocDTOType type;
    private Boolean isRaw;
    private String example;
    private DocumentedApi documentation;
    private Class<?> base;
    private Class<?> from;
    private List<DocDTOField> fields = new ArrayList<>();

    public DocDTO(@NotNull Class<?> from, DocumentedApi parent) {
        var description = from.getAnnotation(Description.class);

        if(description != null) {
            this.description = description.value();
        }

        if(Object.class == from) {
            this.name = "?" + from.getSimpleName();
        } else {
            this.name = from.getSimpleName();
        }

        this.from = from;
        this.documentation = parent;
    }

    public void addField(Bound bound, StoreShakeService shakeService) {
        this.fields.add(new DocDTOField(bound, this, shakeService));
    }

    public String getDescription() {
        return description != null ? description.trim() : null;
    }
}
