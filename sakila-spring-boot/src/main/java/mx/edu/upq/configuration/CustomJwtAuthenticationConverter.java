package mx.edu.upq.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		// Extrae el claim "roles" como lista de strings
		List<String> roles = jwt.getClaimAsStringList("roles");
		Collection<GrantedAuthority> authorities = (roles != null ? roles : List.<String>of())
				.stream()
				// Crea una autoridad por cada rol. Ej: "ADMIN" -> new SimpleGrantedAuthority("ADMIN")
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new JwtAuthenticationToken(jwt, authorities);
	}

}
