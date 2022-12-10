package io.github.asewhy.apidoc.descriptor.info;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ApiSecurityInfo {
    private ApiSecurity def;
    private final Map<String, ApiSecurity> securities;

    /**
     * Информация об авторизации приложения
     */
    public ApiSecurityInfo() {
        this.securities = new HashMap<>();
    }

    /**
     * Добавить информацию о безопасности приложения
     *
     * @param security информация о безопасности
     */
    public void addSecurity(ApiSecurity security) {
        if(def == null) {
            def = security;
        }

        securities.put(security.getName(), security);
    }

    /**
     * Установить информацию об безопасности по умолчанию
     *
     * @param def информацию о безопасности по умолчанию
     */
    public void setDefault(ApiSecurity def) {
        if(def == null) {
            return;
        }

        this.def = def;
    }

    /**
     * Получить информацию о способе авторизации
     *
     * @param name название способа
     * @return информация о способе авторизации
     */
    public ApiSecurity getSecurity(String name) {
        return securities.get(name);
    }
}
