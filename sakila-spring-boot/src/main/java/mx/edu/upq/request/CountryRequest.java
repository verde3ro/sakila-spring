package mx.edu.upq.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


public class CountryRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = -4467720690647787874L;

	@Schema(description = "ID del país (solo para actualización)", example = "1")
	private Short id;

	@NotNull(message = "El nombre del país no puede ser nulo")
	@Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
	@Schema(description = "Nombre del país", example = "México", minLength = 1, maxLength = 50)
	private String country;

	@NotNull(message = "La fecha de actualización no puede ser nula")
	@PastOrPresent(message = "La fecha debe ser presente o pasada")
	@Schema(description = "Fecha y hora de la última actualización", example = "2025-12-31T23:59:59")
	private LocalDateTime lastUpdate;

	public CountryRequest() {
	}

	public CountryRequest(Short id, String country, LocalDateTime lastUpdate) {
		this.id = id;
		this.country = country;
		this.lastUpdate = lastUpdate;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
