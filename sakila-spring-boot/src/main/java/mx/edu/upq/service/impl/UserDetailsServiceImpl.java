package mx.edu.upq.service.impl;

import lombok.RequiredArgsConstructor;
import mx.edu.upq.model.User;
import mx.edu.upq.model.UserRole;
import mx.edu.upq.repository.IUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	private final IUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("Usuario no encontrado: %s", username)));

		List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
				.map(UserRole::getRole)
				.map(role -> new SimpleGrantedAuthority(role.getName()))
				.toList();

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				user.isEnabled(),
				true, true, true,
				authorities
		);
	}

}
