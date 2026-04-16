package mx.edu.upq.repository;

import mx.edu.upq.model.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CountryRepositoryTest {

	@Autowired
	private ICountryRepository countryRepository;

	@Test
	void findAll_ShouldReturnCountries() {
		List<Country> countries = countryRepository.findAll();

		assertThat(countries).isNotEmpty();
	}

}
