package mx.edu.upq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class ResourceServerConfig {

	@Bean
	@Order(0)   // Prioridad máxima, antes que el Authorization Server y el login
	public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http,
	                                                             CorsConfigurationSource corsConfigurationSource) throws Exception {
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource)) // CAMBIO: CORS para API
				.securityMatcher("/api/**")            // Solo se aplica a endpoints de la API
				.authorizeHttpRequests(auth -> auth
						.anyRequest().authenticated()      // Cualquier petición bajo /api/ requiere autenticación
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt
								.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())  // Usa el convertidor personalizado
						)
				);

		return http.build();
	}

}