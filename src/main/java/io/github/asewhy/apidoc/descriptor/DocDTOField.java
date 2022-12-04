package io.github.asewhy.apidoc.descriptor;

import io.github.asewhy.ReflectionUtils;
import io.github.asewhy.apidoc.annotations.documentation.Description;
import io.github.asewhy.apidoc.service.StoreShakeService;
import io.github.asewhy.conversions.ConversionMutator;
import io.github.asewhy.conversions.ConversionResponse;
import io.github.asewhy.conversions.support.annotations.MutatorExcludes;
import io.github.asewhy.conversions.support.iBound;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
@Setter
@ToString
public class DocDTOField {
    protected final static Set<Class<?>> SKIP_ANNOTATIONS = Set.of(Description.class, MutatorExcludes.class);

    private String name;
    private String description;
    private DocDTO parent;
    private DocDTO reference;
    private iBound bound;

    public String getDescription() {
        return description != null ? description.trim() : null;
    }

    public DocDTOField(@NotNull iBound bound, DocDTO parent, StoreShakeService store) {
        this.parent = parent;
        this.bound = bound;
        this.name = bound.getPureName();

        var type = bound.getType();
        var description = bound.getAnnotation(Description.class);

        if(description != null) {
            this.description = description.value();
        }

        if(ConversionMutator.class.isAssignableFrom(type)) {
            this.reference = store.shakeRequestDto(type);
        }

        if(ConversionResponse.class.isAssignableFrom(type)) {
            var pureType = ReflectionUtils.findXGeneric(type, 0);

            if(pureType != null) {
                this.reference = store.shakeResponseDto(pureType, parent.getMapping());
            }
        }
    }
}
