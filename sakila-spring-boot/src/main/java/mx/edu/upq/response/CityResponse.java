package mx.edu.upq.response;

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
public class CityResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = -5928423473278888769L;

	private int cityId;

	private String city;

	private LocalDateTime lastUpdate;

	private int countryId;

	private String countryName;
	public CityResponse(int cityId, String city, Timestamp lastUpdate, int countryId, String countryName) {
		this.cityId = cityId;
		this.city = city;
		this.lastUpdate = lastUpdate.toLocalDateTime();
		this.countryId = countryId;
		this.countryName = countryName;
	}

}
