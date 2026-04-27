package mx.edu.upq.repository;

import mx.edu.upq.model.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de integración del repositorio de países.
 * Verifica la operación findAll con los datos cargados desde data.sql.
 */
@DataJpaTest
class CountryRepositoryTest {

	@Autowired
	private ICountryRepository countryRepository;

	/**
	 * Comprueba que findAll devuelva la cantidad esperada de países (109)
	 * y que el primero sea "Afghanistan", según el script de inicialización.
	 */
	@Test
	void findAll_ShouldReturnCountries() {
		List<Country> countries = countryRepository.findAll();
		assertThat(countries).isNotEmpty();
		assertThat(countries).hasSize(109);                  // Cantidad esperada según data.sql
		assertThat(countries.getFirst().getCountry()).isEqualTo("Afghanistan");
	}

}
