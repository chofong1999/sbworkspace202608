package com.example.demo.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot User CRUD")                     // 文件標題
                        .description("Spring Boot JPA 練習專案 API 文檔") // 描述
                        .version("1.0.0")                              // 版本
                        .contact(new Contact()                         // 聯絡人
                                .name("開發者")
                                .email("developer@example.com"))
                        .license(new License()                         // 授權
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
