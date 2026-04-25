package mx.edu.upq.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos para crear o actualizar una ciudad")
public class CityRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = -3056633812864899244L;

	@Schema(description = "ID de la ciudad (solo para actualización)", example = "1")
	private Short cityId;

	@NotNull(message = "El nombre de la ciudad no puede ser nulo")
	@NotEmpty(message = "El nombre de la ciudad no puede ser vacío")
	@Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
	@Schema(description = "Nombre de la ciudad", example = "Guadalajara", minLength = 1, maxLength = 50)
	private String city;

	@NotNull(message = "La fecha de actualización no puede ser nula")
	@PastOrPresent(message = "La fecha debe ser presente o pasada")
	@Schema(description = "Fecha y hora de la última actualización", example = "2025-12-31T23:59:59")
	private LocalDateTime lastUpdate;

	@NotNull(message = "El país es obligatorio")
	@Schema(description = "ID del país al que pertenece", example = "10")
	private Short countryId;

	@Schema(description = "Nombre del país (solo lectura)", accessMode = Schema.AccessMode.READ_ONLY)
	private String countryName;

}
