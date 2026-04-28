package mx.edu.upq.configuration;

import mx.edu.upq.exception.JsonAccessDeniedHandler;
import mx.edu.upq.exception.JsonAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

	@Bean
	@Order(0)
	public SecurityFilterChain resourceServerSecurityFilterChain(
			HttpSecurity http,
			CorsConfigurationSource corsConfigurationSource,
			JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint,
			JsonAccessDeniedHandler jsonAccessDeniedHandler
	) throws Exception {

		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.securityMatcher("/api/**")
				// ---------- sesión sin estado (JWT) ----------
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				// ---------- cabeceras de seguridad ----------
				.headers(headers -> headers
						.httpStrictTransportSecurity(hsts -> hsts
								.includeSubDomains(true)
								.maxAgeInSeconds(31536000)
						)
						.referrerPolicy(referrer -> referrer
								.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
						)
						.xssProtection(xss -> xss
								.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
						)
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
						.crossOriginOpenerPolicy(coop -> coop
								.policy(CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.SAME_ORIGIN)
						)
						.crossOriginResourcePolicy(corp -> corp
								.policy(CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_ORIGIN)
						)
						.crossOriginEmbedderPolicy(coep ->
								coep.policy(CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.REQUIRE_CORP)
						)
						.contentTypeOptions(config -> {
						})
						.contentSecurityPolicy(csp ->
								csp.policyDirectives("default-src 'self'")
						)
						.addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
								"geolocation=(), microphone=(), camera=(), fullscreen=(), payment=()"
						))
				)

				// ---------- Configuración del recurso protegido con JWT ----------
				.authorizeHttpRequests(auth -> auth
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2
								.jwt(jwt -> jwt
												.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
										// Si tuvieras un bean jwtDecoder(), podrías ponerlo aquí:
										// .decoder(jwtDecoder())
								)
								.authenticationEntryPoint(jsonAuthenticationEntryPoint)
						// El accessDeniedHandler se puede mantener aquí o en exceptionHandling.
						// La forma más típica es en exceptionHandling, pero también es válido dentro del resource server:
						// .accessDeniedHandler(jsonAccessDeniedHandler)
				)
				.exceptionHandling(exceptions -> exceptions
						.accessDeniedHandler(jsonAccessDeniedHandler)
				);

		return http.build();
	}

}
