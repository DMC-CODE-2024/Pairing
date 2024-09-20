package com.eirs.pairs.config;

import com.eirs.pairs.constants.DBType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
public class AppConfig {

    @Value("${eirs.notification.url}")
    private String notificationUrl;

    @Value("${module-name}")
    private String moduleName;

    @Value("${dependent.module-name}")
    private String dependentModuleName;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    public DBType getDbType() {
        return driverClassName.startsWith("com.mysql") ? DBType.MYSQL : driverClassName.startsWith("oracle") ? DBType.ORACLE :
                DBType.NONE;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
