package mx.edu.upq.repository;

import mx.edu.upq.response.CityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CityRepositoryTest {

	@Autowired
	private ICityRepository cityRepository;

	@Test
	void findByCityId_ShouldReturnCityResponse() {
		Optional<CityResponse> result = cityRepository.findByCityId((short) 1);

		assertThat(result).isPresent();
		assertThat(result.get().getCityId()).isEqualTo(1);
	}

	@Test
	void findAllPagination_ShouldReturnPage() {
		PageRequest pageable = PageRequest.of(0, 5, Sort.by("cityId"));
		Page<CityResponse> page = cityRepository.findAllPagintaion(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotEmpty();
	}

}
