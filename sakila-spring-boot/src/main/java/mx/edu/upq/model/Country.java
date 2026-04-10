package mx.edu.upq.model;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the country database table.
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "country")
@NamedQuery(name = "Country.findAll", query = "SELECT c FROM Country c")
public class Country implements Serializable {

	@Serial
	private static final long serialVersionUID = 2611698490624657700L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "country_id", unique = true, nullable = false)
	private Short countryId;

	@Column(nullable = false, length = 50)
	private String country;

	@Column(name = "last_update", nullable = false)
	private Timestamp lastUpdate;

	// bi-directional one-to-many association to City
	@OneToMany(mappedBy = "country")
	private List<City> cities;

	// Métodos de conveniencia (se mantienen)
	public City addCity(City city) {
		this.cities.add(city);
		city.setCountry(this);
		return city;
	}

	public City removeCity(City city) {
		this.cities.remove(city);
		city.setCountry(null);
		return city;
	}

}
