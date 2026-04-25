package mx.edu.upq.configuration;

import mx.edu.upq.exception.JsonAccessDeniedHandler;
import mx.edu.upq.exception.JsonAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class ResourceServerConfig {

	@Bean
	@Order(0)
	public SecurityFilterChain resourceServerSecurityFilterChain(
			HttpSecurity http,
			CorsConfigurationSource corsConfigurationSource,
			JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint,   // inyectado
			JsonAccessDeniedHandler jsonAccessDeniedHandler             // inyectado
	) throws Exception {
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.securityMatcher("/api/**")
				.authorizeHttpRequests(auth -> auth
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt
								.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
						)
						// El entry point de autenticación se define AQUÍ dentro del resource server
						.authenticationEntryPoint(jsonAuthenticationEntryPoint)
				)
				// El accessDeniedHandler se puede dejar en exceptionHandling (también funciona)
				.exceptionHandling(exceptions -> exceptions
						.accessDeniedHandler(jsonAccessDeniedHandler)
				);

		return http.build();
	}

}
