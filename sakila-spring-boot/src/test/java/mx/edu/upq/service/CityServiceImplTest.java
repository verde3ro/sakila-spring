package mx.edu.upq.service;

import mx.edu.upq.exception.InternalException;
import mx.edu.upq.mapper.ICityReponseMapper;
import mx.edu.upq.model.City;
import mx.edu.upq.model.Country;
import mx.edu.upq.repository.ICityRepository;
import mx.edu.upq.repository.ICountryRepository;
import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import mx.edu.upq.service.impl.CityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

	@Mock
	private ICityRepository cityRepository;
	@Mock
	private ICountryRepository countryRepository;
	@Mock
	private ICityReponseMapper cityReponseMapper;
	@InjectMocks
	private CityServiceImpl cityService;
	private City city;
	private CityResponse cityResponse;
	private CityRequest cityRequest;

	@BeforeEach
	void setUp() {
		Country country = new Country();
		country.setCountryId((short) 1);
		country.setCountry("Mexico");

		city = new City();
		city.setCityId((short) 1);
		city.setCity("Guadalajara");
		city.setCountry(country);
		city.setLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));

		cityResponse = new CityResponse(1, "Guadalajara", LocalDateTime.now(), 1, "Mexico");

		cityRequest = new CityRequest();
		cityRequest.setCityId((short) 1);
		cityRequest.setCity("Guadalajara");
		cityRequest.setCountryId((short) 1);
		cityRequest.setLastUpdate(LocalDateTime.now());
	}

	@Test
	void findAll_ShouldReturnList() {
		Page<CityResponse> page = new PageImpl<>(List.of(cityResponse));
		when(cityRepository.findAllPagintaion(any(Pageable.class))).thenReturn(page);

		List<CityResponse> result = cityService.findAll();
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getCity()).isEqualTo("Guadalajara");
	}

	@Test
	void findById_ShouldReturnCityResponse() {
		when(cityRepository.findByCityId((short) 1)).thenReturn(Optional.of(cityResponse));

		CityResponse result = cityService.findById((short) 1);
		assertThat(result).isNotNull();
		assertThat(result.getCityId()).isEqualTo(1);
	}

	@Test
	void findById_NotFound_ThrowsException() {
		when(cityRepository.findByCityId((short) 99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> cityService.findById((short) 99))
				.isInstanceOf(InternalException.class)
				.hasMessageContaining("Ciudad no encontrada");
	}

	@Test
	void create_ShouldReturnSavedCity() {
		when(countryRepository.getReferenceById((short) 1)).thenReturn(new Country());
		when(cityRepository.save(any(City.class))).thenReturn(city);
		when(cityReponseMapper.toResponse(any(City.class))).thenReturn(cityResponse);

		CityResponse result = cityService.create(cityRequest);
		assertThat(result).isNotNull();
		verify(cityRepository).save(any(City.class));
	}

	@Test
	void update_ShouldReturnUpdatedCity() {
		when(cityRepository.findById((short) 1)).thenReturn(Optional.of(city));
		when(countryRepository.getReferenceById((short) 1)).thenReturn(new Country());
		when(cityRepository.save(any(City.class))).thenReturn(city);
		when(cityReponseMapper.toResponse(any(City.class))).thenReturn(cityResponse);

		CityResponse result = cityService.update(cityRequest);
		assertThat(result).isNotNull();
		verify(cityRepository).save(any(City.class));
	}

	@Test
	void delete_ShouldDeleteCity() {
		when(cityRepository.existsById((short) 1)).thenReturn(true);
		doNothing().when(cityRepository).deleteById((short) 1);

		cityService.delete((short) 1);
		verify(cityRepository).deleteById((short) 1);
	}

	@Test
	void delete_NotFound_ThrowsException() {
		when(cityRepository.existsById((short) 99)).thenReturn(false);

		assertThatThrownBy(() -> cityService.delete((short) 99))
				.isInstanceOf(InternalException.class);
	}

	@Test
	void generateExcel_ShouldReturnBase64String() {
		when(cityRepository.findAllPagintaion(any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(cityResponse)));

		String base64 = cityService.generateExcel();
		assertThat(base64).isNotNull().isNotEmpty();
	}

}
