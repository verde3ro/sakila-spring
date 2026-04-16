package mx.edu.upq.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {
	@Serial
	private static final long serialVersionUID = 3612454161556594545L;
	private String token;
	private String type = "Bearer";
	private String username;
	private String role;

	public LoginResponse(String token, String username, String role) {
		this.token = token;
		this.username = username;
		this.role = role;
	}

}
