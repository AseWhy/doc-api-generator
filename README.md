# doc-api-generator

Небольшой модуль для генерации документации api, модуль работает в паке с модулем конверсий и без него работать не может.

## Базовая конфигурация

Для начала работы с модулем необходимо создать бин конфигурации, минимальный пример показан ниже:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.asewhy.apidoc.ApiDocumentationConfiguration;
import io.github.asewhy.apidoc.IApiDocumentationConfiguration;
import io.github.asewhy.apidoc.annotations.EnableApiDocGeneration;
import io.github.asewhy.apidoc.descriptor.info.ApiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableApiDocGeneration
@Profile({"dev", "beta", "test"})
public class DocumentationConfig implements ApiDocumentationConfiguration {
    @Value("${spring.application.name}")
    protected String appName;
    @Autowired
    protected ObjectMapper objectMapper;

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Override
    public ApiInfo api() {
        return new ApiInfo(appName, "2.2.8");
    }
}
```

Конфигурация выше будет активировать документацию только в профилях dev, beta и test. После запуска документацию swagger
можно будет увидеть по адресу api/docs/swagger, документация openapi будет доступна по адресу /api/docs/json/. На маршрутах
с документацией можно отключить spring security:

```java
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class StorageResource extends ResourceServerConfigurerAdapter {
    public void configure(@NotNull HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .authorizeRequests()
                .antMatchers("/api/docs/**")
                .permitAll()
            .anyRequest()
        .authenticated();
    }
}
```

Адрес документации можно поменять, реализовав в конфигурации методы `apiPath` и `docsPath`.

### Безопасность

Для описания способа доступа к апи можно указать так-же используемый способ авторизации. Ниже приведен пример как это можно сделать
в конфигурации документации просто реализовав метод интерфейса:

```java

import io.github.asewhy.apidoc.ApiDocumentationConfiguration;
import io.github.asewhy.apidoc.IApiDocumentationConfiguration;
import io.github.asewhy.apidoc.annotations.EnableApiDocGeneration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableApiDocGeneration
@Profile({"dev", "beta", "test"})
public class DocumentationConfig implements ApiDocumentationConfiguration {
    // ...

    @Override
    public ApiSecurityInfo security() {
        var info = new ApiSecurityInfo();

        info.addSecurity(
            ApiHttpSecurity
                .builder()
                .scheme(ApiHttpSecurityScheme.bearer)
                .name("HttpBearerSecurity")
            .build()
        );

        return info;
    }

    // ...
}
```

Код выше позволит указывать bearer токен авторизации для тестирования апи прямо на странице. Для авторизации так-же доступны
и другие способы, смотри пакет `io.github.asewhy.apidoc.descriptor.info`.

### Аннотации

Пакет предоставляет свои аннотации для настройки того как будет отображаться документация:

| Аннотация   | Описание                                                                                                                                                                                                                                                                            | Пример                                                                                                                                                                                                          |
|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Description | Описание Поля/Метода/Контроллера/ДТО. Аннотация позволяет указать локализованное описание аннотируемой сущности                                                                                                                                                                     | @Description("Hello world")                                                                                                                                                                                     |
| Example     | Пример для ДТО. Можно указать только над классом, предоставляет информацию о том какой пример отображать. Если аннотация отсутствует то swagger то генерирует пример автоматически. Пример должен быть предоставлен в том же формате что может обработать поставляемый ObjectMapper | @Example("""<br>&nbsp;&nbsp;&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"foo": "bar",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"baz": "bar"<br>&nbsp;&nbsp;&nbsp;&nbsp;}<br>""") |