package io.github.asewhy.apidoc;

import io.github.asewhy.apidoc.support.interfaces.iDocumentedApi;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

public interface iApiDocumentationFactory {
    /**
     * Получить экземпляр документации api
     *
     * @return экземпляр документации api
     */
    iDocumentedApi provideDocApi();

    /**
     * Вызывается после автоматического добавления всех имеющихся в api данных
     *
     * @param api поставляемая документация api
     */
    default void afterApiInitialize(@org.jetbrains.annotations.NotNull iDocumentedApi api) {

    }

    /**
     * Получить эндпоинт, по которому будет доступна документация
     *
     * @return эндпоинт документации
     */
    default String provideDocApiPath() {
        return "/api/docs";
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
    default void beforeApiInitialize(@org.jetbrains.annotations.NotNull iDocumentedApi api) {
        api.makeAnnotation(NotNull.class, annotation -> "Поле должно быть не null");
        api.makeAnnotation(Min.class, annotation -> "Минимальное значение " + annotation.value());
        api.makeAnnotation(Max.class, annotation -> "Максимальное значение " + annotation.value());
        api.makeAnnotation(Length.class, annotation -> "Максимальная длинна " + annotation.max() + "; Минимальная длинна " + annotation.min());
        api.makeAnnotation(Size.class, annotation -> "Максимальный размер " + annotation.max() + "; Минимальный размер " + annotation.min());
        api.makeAnnotation(Email.class, annotation -> "Должно иметь формат RFC-822 Email");
        api.makeAnnotation(Null.class, annotation -> "Поле должно быть null");
        api.makeAnnotation(Pattern.class, annotation -> "Поле должно соответствовать шаблону " + annotation.regexp());
        api.makeAnnotation(NotBlank.class, annotation -> "Поле не должно быть пустым, включая пробелы");
    }
}
