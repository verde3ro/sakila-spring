package mx.edu.upq.model;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the city database table.
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "city")
@NamedQuery(name = "City.findAll", query = "SELECT c FROM City c")
public class City implements Serializable {

	@Serial
	private static final long serialVersionUID = 8363664519736621160L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "city_id", unique = true, nullable = false)
	private Short cityId;

	@Column(nullable = false, length = 50)
	private String city;

	@Column(name = "last_update", nullable = false)
	private Timestamp lastUpdate;

	// bi-directional many-to-one association to Country
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id", nullable = false)
	private Country country;

}
