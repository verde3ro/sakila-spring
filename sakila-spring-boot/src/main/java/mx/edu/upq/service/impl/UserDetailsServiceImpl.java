package mx.edu.upq.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final Map<String, UserDetails> users = Map.of(
			"user", new User("user", "user0123", List.of(new SimpleGrantedAuthority("USER"))),
			"admin", new User("admin", "admin123", List.of(new SimpleGrantedAuthority("ADMIN")))
	);

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails user = users.get(username);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("Usuario no encontrado: %s", username));
		}

		return user;
	}

}
