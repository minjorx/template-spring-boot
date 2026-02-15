package com.minjor.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
        final String securitySchemeName = "bearerAuth"; // 定义安全方案名称

        var openapi = new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .description("输入 JWT 令牌，格式: Bearer <token>")))
                .info(new Info().title("Datam")
                        .description("Datam")
                        .version("v1.0.0"));

        Server server = new Server();
        server.setUrl(apiPrefix);
        openapi.addServersItem(server);
        return openapi;
    }
}
