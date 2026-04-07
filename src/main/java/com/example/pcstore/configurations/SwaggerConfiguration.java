package com.example.pcstore.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    public static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PC Store API")
                        .description("""
                                REST API cho hệ thống bán PC và linh kiện máy tính.

                                **Xác thực:** Các endpoint yêu cầu đăng nhập cần gửi kèm header:
                                ```
                                Authorization: Bearer <access_token>
                                ```
                                Token được lấy từ API `/api/authentication/login`.

                                **Phân quyền:**
                                - `USER` — khách hàng đã đăng nhập
                                - `ADMIN` — quản trị viên
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PC Store Team")
                                .email("nguyentunglam230203@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:9999").description("Local Development")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Nhập JWT access token lấy từ API đăng nhập")));
    }
}
