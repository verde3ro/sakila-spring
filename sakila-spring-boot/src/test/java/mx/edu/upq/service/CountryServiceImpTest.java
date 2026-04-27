package mx.edu.upq.service;

import mx.edu.upq.mapper.ICountryResponseMapper;
import mx.edu.upq.model.Country;
import mx.edu.upq.repository.ICountryRepository;
import mx.edu.upq.response.CountryResponse;
import mx.edu.upq.service.impl.CountryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Prueba unitaria de CountryServiceImpl (nota: el nombre de la clase contiene "Imp").
 * Verifica la operación findAll.
 */
@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {   // nombre original corregido (la clase se llama CountryServiceImplTest)

	@Mock
	private ICountryRepository countryRepository;
	@Mock
	private ICountryResponseMapper countryResponseMapper;

	@InjectMocks
	private CountryServiceImpl countryService;

	/**
	 * Prueba que findAll retorne una lista de CountryResponse mapeada desde las entidades.
	 */
	@Test
	void findAll_ShouldReturnList() {
		// Entidad simulada
		Country country = new Country();
		country.setCountryId((short) 1);
		country.setCountry("Mexico");

		// Respuesta esperada después del mapeo
		CountryResponse response = new CountryResponse(1, "Mexico", LocalDateTime.now());

		// Simula el repositorio y el mapper
		when(countryRepository.findAll()).thenReturn(List.of(country));
		when(countryResponseMapper.toResponseList(List.of(country))).thenReturn(List.of(response));

		List<CountryResponse> result = countryService.findAll();
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getCountry()).isEqualTo("Mexico");
	}

}
