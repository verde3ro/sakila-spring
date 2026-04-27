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

/**
 * Prueba de integración del repositorio de ciudades.
 * Utiliza H2 en memoria y carga datos de prueba desde data.sql (se espera que exista en src/test/resources).
 */
@DataJpaTest
class CityRepositoryTest {

	@Autowired
	private ICityRepository cityRepository;

	/**
	 * Verifica que la consulta findByCityId devuelva los datos correctos para el ID 1,
	 * que corresponden al registro insertado por data.sql.
	 */
	@Test
	void findByCityId_ShouldReturnCityResponse() {
		// El script data.sql debe contener: city_id=1, city='A Coruña (La Coruña)', country='Spain'
		Optional<CityResponse> result = cityRepository.findByCityId((short) 1);

		assertThat(result).isPresent();
		CityResponse response = result.get();
		assertThat(response.getCityId()).isEqualTo(1);
		assertThat(response.getCity()).isEqualTo("A Coruña (La Coruña)");
		assertThat(response.getCountryName()).isEqualTo("Spain");
	}

	/**
	 * Prueba la consulta paginada findAllPagintaion, verificando que devuelva
	 * una página con contenido y que el total de elementos coincida con los insertados (600).
	 */
	@Test
	void findAllPagination_ShouldReturnPage() {
		PageRequest pageable = PageRequest.of(0, 10, Sort.by("c.cityId"));
		Page<CityResponse> page = cityRepository.findAllPagintaion(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getTotalElements()).isEqualTo(600); // 600 ciudades en data.sql
	}

}
