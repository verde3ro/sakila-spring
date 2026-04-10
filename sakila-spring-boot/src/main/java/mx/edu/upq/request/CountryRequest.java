package mx.edu.upq.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


public class CountryRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = -4467720690647787874L;

	private Short id;

	@NotNull(message = "El nombre del país no puede ser nulo")
	@Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
	private String country;

	@NotNull(message = "La fecha de actualización no puede ser nula")
	@PastOrPresent(message = "La fecha debe ser presente o pasada")
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
