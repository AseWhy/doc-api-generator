# doc-api-generator

Небольшой модуль для генерации документации api, модуль работает в паке с модулем конверсий и без него работать не может.

## Конфигурация

Для начала работы с модулем необходимо создать бин конфигурации, минимальный пример показан ниже:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.asewhy.apidoc.IApiDocumentationConfiguration;
import io.github.asewhy.apidoc.annotations.EnableApiDocGeneration;
import io.github.asewhy.apidoc.descriptor.info.ApiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@EnableApiDocGeneration
@Profile({ "dev", "beta", "test" })
public class DocumentationConfig implements IApiDocumentationConfiguration {
    @Value("${spring.application.name}")
    protected String appName;
    @Autowired
    protected ObjectMapper objectMapper;

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Override
    public ApiInfo docApi() {
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