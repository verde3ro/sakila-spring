package mx.edu.upq.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CityRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = -3056633812864899244L;

	private Short cityId;

	@NotNull(message = "El nombre de la ciudad no puede ser nulo")
	@NotEmpty(message = "El nombre de la ciudad no puede ser vacío")
	@Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
	private String city;

	@NotNull(message = "La fecha de actualización no puede ser nula")
	@PastOrPresent(message = "La fecha debe ser presente o pasada")
	private LocalDateTime lastUpdate;

	@NotNull(message = "El país es obligatorio")
	private Short countryId;

	private String countryName;

}
