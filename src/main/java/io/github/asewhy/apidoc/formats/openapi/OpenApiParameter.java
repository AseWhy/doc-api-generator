package io.github.asewhy.apidoc.formats.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.asewhy.apidoc.formats.openapi.schema.JsonSchema;
import io.github.asewhy.apidoc.formats.openapi.support.OpenApiDest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiParameter {
    /**
     * Имя параметра. Имена параметров чувствительны к регистру.
     * Если это «путь», поле имени ДОЛЖНО соответствовать выражению шаблона, встречающемуся в поле пути в объекте «Пути». Дополнительную информацию см. в разделе Шаблоны путей.
     * Если это «заголовок», а поле имени — «Принять», «Тип содержимого» или «Авторизация», определение параметра ДОЛЖНО игнорироваться.
     * Во всех остальных случаях имя соответствует имени параметра, используемому свойством in.
     */
    private String name;
    private OpenApiDest in;
    private JsonSchema schema;
    private String description;
    private Boolean required;
    private Boolean deprecated;
    /**
     * Устанавливает возможность передачи пустых параметров. Это справедливо только для параметров запроса и
     * позволяет отправлять параметр с пустым значением. Значение по умолчанию — ложь.
     * Если используется стиль и если поведение n/a (не может быть сериализовано),
     * значение allowEmptyValue ДОЛЖНО ИГНОРИРОВАТЬСЯ. Использование этого свойства НЕ РЕКОМЕНДУЕТСЯ,
     * так как оно может быть удалено в более поздних версиях.
     */
    private Boolean allowEmptyValue;
}
