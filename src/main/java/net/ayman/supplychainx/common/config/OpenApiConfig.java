package net.ayman.supplychainx.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI supplyChainXOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SupplyChainX API")
                        .description("API documentation for the Supply Chain Management system")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ayman")
                                .email("ayman@example.com")
                                .url("https://github.com/AymanElh")));
    }
}
