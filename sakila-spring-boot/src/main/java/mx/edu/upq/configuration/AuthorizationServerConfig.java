package mx.edu.upq.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.cors.CorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationServerConfig {

	// ========== 1. Cadena de filtros del Authorization Server ==========
	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
	                                                                  CorsConfigurationSource corsConfigurationSource) throws Exception {
		OAuth2AuthorizationServerConfigurer authServerConfigurer = new OAuth2AuthorizationServerConfigurer();

		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource)) // CAMBIO: CORS para endpoints /oauth2/**
				.securityMatcher("/oauth2/**", "/.well-known/**", "/.well-known/openid-configuration") // CAMBIO: incluir rutas de descubrimiento
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/.well-known/**").permitAll()   // PERMITIR acceso público a la configuración OpenID
						.anyRequest().authenticated()                    // el resto (oauth2/**) requiere autenticación
				)
				.csrf(csrf -> csrf.ignoringRequestMatchers(authServerConfigurer.getEndpointsMatcher()))
				.with(authServerConfigurer, Customizer.withDefaults())
				.exceptionHandling(exceptions ->
						exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
				);

		return http.build();
	}

	// ========== 2. Repositorio de clientes registrados (CLIENTE PÚBLICO para SPA) ==========
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		// CAMBIO: Cliente público sin secreto, PKCE obligatorio, redirect al frontend React
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("clientapp")
				// .clientSecret(passwordEncoder.encode("123456"))  ELIMINADO: cliente público no usa secreto
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)            // Cliente público
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				// ELIMINADO: AuthorizationGrantType.CLIENT_CREDENTIALS (no aplica sin secreto)
				.redirectUri("http://localhost:3000/callback")                          // CAMBIO: URL del frontend React
				.scope("read_profile_info")
				.clientSettings(ClientSettings.builder()
						.requireProofKey(true)                                          // CAMBIO: PKCE obligatorio
						.requireAuthorizationConsent(false)
						.build())
				.tokenSettings(TokenSettings.builder()
						.accessTokenTimeToLive(Duration.ofHours(1))
						.refreshTokenTimeToLive(Duration.ofHours(7))
						.build())
				.build();

		return new InMemoryRegisteredClientRepository(registeredClient);
	}

	// ========== 3. Fuente de claves JWK ==========
	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	private static KeyPair generateRsaKey() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	// ========== 4. Configuración de rutas ==========
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	// ========== 5. Personalizador de tokens (¡LA CLAVE PARA LOS ROLES!) ==========
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
		return context -> {
			if (context.getPrincipal() instanceof UsernamePasswordAuthenticationToken auth) {
				var roles = auth.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList());
				// CAMBIO: se mantiene el claim "roles" sin prefijo, igual que tu UserDetailsService
				context.getClaims().claim("roles", roles);
			}
		};
	}
}