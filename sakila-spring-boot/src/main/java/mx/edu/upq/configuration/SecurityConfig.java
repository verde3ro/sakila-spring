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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfigurationSource;

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
						.contentSecurityPolicy(csp -> csp.policyDirectives(
								"default-src 'self'; " +
										"style-src 'self' 'unsafe-inline' https://unpkg.com; " +
										"script-src 'self' 'unsafe-inline' https://unpkg.com; " +
										"font-src 'self' https://unpkg.com data:; " +
										"img-src 'self' data:;"
						))
						.addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
								"geolocation=(), microphone=(), camera=(), fullscreen=(), payment=()"))
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login.html", "/login.css", "/login.js", "/oauth2/authorize**", "/error**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/login.html")          // Página personalizada
						.loginProcessingUrl("/login")      // URL que procesa el POST (no es una ruta estática)
						.permitAll()
				)
				.logout(logout -> logout
						.logoutSuccessUrl("/login.html?logout")
						.permitAll()
				).csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.ignoringRequestMatchers("/api/**", "/login")
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
