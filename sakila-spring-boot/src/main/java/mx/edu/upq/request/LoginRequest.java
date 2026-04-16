package mx.edu.upq.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class LoginRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 3588633224228216427L;

	@NotNull(message = "El usuario no puede ser nulo")
	@Size(min = 1, max = 50, message = "El usuario debe tener entre 1 y 50 caracteres")
	private String username;
	@NotNull(message = "El password no puede ser nulo")
	@Size(min = 1, max = 50, message = "El password debe tener entre 1 y 50 caracteres")
	private String password;

}
