package mx.edu.upq.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		final String securitySchemeName = "bearerAuth";
		return new OpenAPI()
				.info(new Info()
						.title("Sakila API")
						.version("1.0")
						.description("Documentación de la API de Sakila"))
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(new Components()
						.addSecuritySchemes(securitySchemeName,
								new SecurityScheme()
										.name(securitySchemeName)
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")));
	}

	/**
	 * Sobrescribe el bean 'conventionErrorViewResolver' que entra en conflicto con Springdoc en el perfil 'dev'.
	 * Al devolver null se mantiene el comportamiento por defecto de Spring Boot.
	 */
	@Bean
	public ErrorViewResolver conventionErrorViewResolver() {
		return (request, status, model) -> {
			// Devolver null mantiene el comportamiento estándar de Spring Boot
			return null;
		};
	}

}
