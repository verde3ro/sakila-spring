package mx.edu.upq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Cadena de filtros para el login por formulario.
	 * Orden 2 para aplicarse después del Resource Server y Authorization Server.
	 */
	@Bean
	@Order(2)
	public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource)) // CAMBIO: Habilitar CORS para el login
				.securityMatcher("/**")          // Aplica a cualquier ruta no capturada por cadenas de orden menor
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/oauth/authorize**", "/login**", "/error**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // OpenAPI público
						.anyRequest().authenticated()
				)
				.formLogin(withDefaults());      // Habilita el formulario de login predeterminado

		return http.build();
	}

	/**
	 * Codificador de contraseñas BCrypt, compartido con el Authorization Server.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
