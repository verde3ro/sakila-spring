package mx.edu.upq.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Cadena de filtros para el login por formulario y otras páginas (Swagger, etc.).
	 * Orden 2 para aplicarse después del Resource Server y Authorization Server.
	 */
	@Bean
	@Order(2)
	public SecurityFilterChain formLoginSecurityFilterChain(
			HttpSecurity http,
			CorsConfigurationSource corsConfigurationSource
	) throws Exception {
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.securityMatcher("/**")
				// ═══════════════ CABECERAS DE SEGURIDAD ═══════════════
				.headers(headers -> headers
						.httpStrictTransportSecurity(hsts -> hsts
								.includeSubDomains(true)
								.maxAgeInSeconds(31536000))
						.referrerPolicy(referrer -> referrer
								.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
						.xssProtection(xss -> xss
								.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
						.crossOriginOpenerPolicy(coop -> coop
								.policy(CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.SAME_ORIGIN))
						.crossOriginResourcePolicy(corp -> corp
								.policy(CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_ORIGIN))
						.crossOriginEmbedderPolicy(coep ->
								coep.policy(CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.REQUIRE_CORP))
						.contentTypeOptions(config -> {
						})
						.contentSecurityPolicy(csp ->
								csp.policyDirectives("default-src 'self'"))
						.addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
								"geolocation=(), microphone=(), camera=(), fullscreen=(), payment=()"))
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/oauth/authorize**", "/login**", "/error**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(withDefaults()); // Sesión con estado (no se especifica STATELESS)

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
