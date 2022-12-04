package io.github.asewhy.apidoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.asewhy.apidoc.descriptor.DocumentedApi;
import io.github.asewhy.apidoc.descriptor.info.ApiInfo;
import io.github.asewhy.apidoc.descriptor.interfaces.IAnnotationRegistration;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

public interface IApiDocumentationConfiguration {
    /**
     * Получить экземпляр маппера объектов
     *
     * @return маппер объектов
     */
    ObjectMapper objectMapper();

    /**
     * Получить экземпляр документации api
     *
     * @return экземпляр документации api
     */
    default ApiInfo docApi() {
        return new ApiInfo("Spring application");
    }

    /**
     * Вызывается после автоматического добавления всех имеющихся в api данных
     *
     * @param api поставляемая документация api
     */
    default void afterApiInitialize(@org.jetbrains.annotations.NotNull DocumentedApi api) {

    }

    /**
     * Получить эндпоинт, по которому будет доступна документация
     *
     * @return эндпоинт документации
     */
    default String apiPath() {
        return "/api/docs/json/{type}";
    }

    /**
     * Получить эндпоинт, по которому будет доступна документация
     *
     * @return эндпоинт документации
     */
    default String docsPath() {
        return "/api/docs/swagger";
    }

    /**
     * Нужна ли инициализация api по умолчанию, тоесть если будет возвращен true, то
     * будут добавлены все методы всех контроллеров ShiftController, и все модели имеющиеся на данный момент в сторе
     *
     * @return true если нужна первичная инициализация api
     */
    default boolean requireApiInitialize() {
        return true;
    }

    /**
     * Вызывается перед автоматическим добавлением всех имеющихся в api данных
     *
     * @param api поставляемая документация api
     */
    default void beforeApiInitialize(@org.jetbrains.annotations.NotNull IAnnotationRegistration api) {
        api.registerAnnotation(NotNull.class, annotation -> "Поле должно быть не null");
        api.registerAnnotation(Min.class, annotation -> "Минимальное значение " + annotation.value());
        api.registerAnnotation(Max.class, annotation -> "Максимальное значение " + annotation.value());
        api.registerAnnotation(Length.class, annotation -> "Максимальная длинна " + annotation.max() + "; Минимальная длинна " + annotation.min());
        api.registerAnnotation(Size.class, annotation -> "Максимальный размер " + annotation.max() + "; Минимальный размер " + annotation.min());
        api.registerAnnotation(Email.class, annotation -> "Должно иметь формат RFC-822 Email");
        api.registerAnnotation(Null.class, annotation -> "Поле должно быть null");
        api.registerAnnotation(Pattern.class, annotation -> "Поле должно соответствовать шаблону " + annotation.regexp());
        api.registerAnnotation(NotBlank.class, annotation -> "Поле не должно быть пустым, включая пробелы");
    }
}
