package mx.edu.upq.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representación de un país")
public class CountryResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = 5250059480612831999L;

	@Schema(description = "ID del país", example = "10")
	private int countryId;

	@Schema(description = "Nombre del país", example = "México")
	private String country;

	@Schema(description = "Fecha de última actualización", example = "2025-06-15T10:30:00")
	private LocalDateTime lastUpdate;

}
