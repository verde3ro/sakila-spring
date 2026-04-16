package mx.edu.upq.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.edu.upq.request.LoginRequest;
import mx.edu.upq.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private JwtUtil jwtUtil;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void login_ShouldReturnToken() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("user");
		loginRequest.setPassword("password");

		UserDetails userDetails = User.withUsername("user").password("password").authorities("USER").build();
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
		when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("fake-jwt-token"))
				.andExpect(jsonPath("$.username").value("user"));
	}

}
