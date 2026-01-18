package com.minjor.app.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc API文档相关配置
 */
@Slf4j
@Configuration
public class SpringDocConfig {
    @Value("${springdoc.swagger-ui.api-prefix:/}")
    private String apiPrefix;

    @Bean
    public OpenAPI openApi() {
        var openapi = new OpenAPI()
                .info(new Info().title("app name")
                        .description("description")
                        .version("v1.0.0"));
        Server server = new Server();
        server.setUrl(apiPrefix);
        openapi.addServersItem(server);
        return openapi;
    }
}