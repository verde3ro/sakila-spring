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

@ExtendWith(MockitoExtension.class)
class CountryServiceImpTest {

	@Mock
	private ICountryRepository countryRepository;
	@Mock
	private ICountryResponseMapper countryResponseMapper;
	@InjectMocks
	private CountryServiceImpl countryService;

	@Test
	void findAll_ShouldReturnList() {
		Country country = new Country();
		country.setCountryId((short) 1);
		country.setCountry("Mexico");
		CountryResponse response = new CountryResponse(1, "Mexico", LocalDateTime.now());

		when(countryRepository.findAll()).thenReturn(List.of(country));
		when(countryResponseMapper.toResponseList(List.of(country))).thenReturn(List.of(response));

		List<CountryResponse> result = countryService.findAll();
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getCountry()).isEqualTo("Mexico");
	}

}
