package io.github.asewhy.apidoc.descriptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unchecked")
public class DocAnnotation<T> {
    private final Class<T> annotation;
    private final Function<T, String> description;

    /**
     * Получить описание аннотации из самой аннотации
     *
     * @param annotation объект аннотации
     * @return описание аннотации
     */
    public String descriptor(Object annotation) {
        if(description != null) {
            return description.apply((T) annotation);
        } else {
            return null;
        }
    }
}
