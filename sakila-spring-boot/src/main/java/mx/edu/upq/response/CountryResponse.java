package mx.edu.upq.response;

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
public class CountryResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = 5250059480612831999L;

	private int countryId;

	private String country;

	private LocalDateTime lastUpdate;

}
