package mx.edu.upq.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representación de una ciudad en las respuestas")
public class CityResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = -5928423473278888769L;

	@Schema(description = "ID de la ciudad", example = "1")
	private int cityId;

	@Schema(description = "Nombre de la ciudad", example = "Guadalajara")
	private String city;

	@Schema(description = "Fecha de última actualización", example = "2025-06-15T10:30:00")
	private LocalDateTime lastUpdate;

	@Schema(description = "ID del país", example = "10")
	private int countryId;

	@Schema(description = "Nombre del país", example = "México")
	private String countryName;

	public CityResponse(int cityId, String city, Timestamp lastUpdate, int countryId, String countryName) {
		this.cityId = cityId;
		this.city = city;
		this.lastUpdate = lastUpdate.toLocalDateTime();
		this.countryId = countryId;
		this.countryName = countryName;
	}

}
