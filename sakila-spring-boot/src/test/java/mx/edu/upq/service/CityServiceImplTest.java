package mx.edu.upq.service;

import mx.edu.upq.exception.InternalException;
import mx.edu.upq.mapper.ICityReponseMapper;
import mx.edu.upq.mapper.ICityRequestMapper;
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

/**
 * Prueba unitaria de la lógica de negocio de CityServiceImpl.
 * Utiliza Mockito para simular repositorios y mapeadores.
 */
@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

	@Mock
	private ICityRepository cityRepository;
	@Mock
	private ICountryRepository countryRepository;
	@Mock
	private ICityReponseMapper cityReponseMapper;
	@Mock
	private ICityRequestMapper cityRequestMapper;

	@InjectMocks  // Inyecta los mocks anteriores en esta instancia del servicio
	private CityServiceImpl cityService;

	// Objetos de prueba reutilizables
	private City city;
	private CityResponse cityResponse;
	private CityRequest cityRequest;

	/**
	 * Inicializa los objetos de prueba antes de cada test.
	 */
	@BeforeEach
	void setUp() {
		// País asociado a la ciudad
		Country country = new Country();
		country.setCountryId((short) 1);
		country.setCountry("Mexico");

		// Entidad City
		city = new City();
		city.setCityId((short) 1);
		city.setCity("Guadalajara");
		city.setCountry(country);
		city.setLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));

		// DTO de respuesta
		cityResponse = new CityResponse(1, "Guadalajara", LocalDateTime.now(), 1, "Mexico");

		// DTO de petición
		cityRequest = new CityRequest();
		cityRequest.setCityId((short) 1);
		cityRequest.setCity("Guadalajara");
		cityRequest.setCountryId((short) 1);
		cityRequest.setLastUpdate(LocalDateTime.now());
	}

	/**
	 * Prueba que findAll() retorne la lista de ciudades proveniente del repositorio paginado.
	 */
	@Test
	void findAll_ShouldReturnList() {
		Page<CityResponse> page = new PageImpl<>(List.of(cityResponse));
		when(cityRepository.findAllPagintaion(any(Pageable.class))).thenReturn(page);

		List<CityResponse> result = cityService.findAll();
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getCity()).isEqualTo("Guadalajara");
	}

	/**
	 * Prueba que findById retorne la ciudad cuando existe en el repositorio.
	 */
	@Test
	void findById_ShouldReturnCityResponse() {
		when(cityRepository.findByCityId((short) 1)).thenReturn(Optional.of(cityResponse));

		CityResponse result = cityService.findById((short) 1);
		assertThat(result).isNotNull();
		assertThat(result.getCityId()).isEqualTo(1);
	}

	/**
	 * Prueba que findById lance InternalException cuando no se encuentra la ciudad.
	 */
	@Test
	void findById_NotFound_ThrowsException() {
		when(cityRepository.findByCityId((short) 99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> cityService.findById((short) 99))
				.isInstanceOf(InternalException.class)
				.hasMessageContaining("Ciudad no encontrada");
	}

	/**
	 * Prueba la creación de una ciudad: mapea request a entidad, obtiene país, guarda y retorna respuesta.
	 */
	@Test
	void create_ShouldReturnSavedCity() {
		when(cityRequestMapper.toEntity(any(CityRequest.class))).thenReturn(city);
		when(countryRepository.getReferenceById((short) 1)).thenReturn(city.getCountry());
		when(cityRepository.save(any(City.class))).thenReturn(city);
		when(cityReponseMapper.toResponse(any(City.class))).thenReturn(cityResponse);

		CityResponse result = cityService.create(cityRequest);
		assertThat(result).isNotNull();
		verify(cityRepository).save(any(City.class)); // Verifica que realmente se guardó
	}

	/**
	 * Prueba la actualización: busca existente, aplica cambios, guarda y retorna respuesta.
	 */
	@Test
	void update_ShouldReturnUpdatedCity() {
		when(cityRepository.findById((short) 1)).thenReturn(Optional.of(city));
		doNothing().when(cityRequestMapper).updateEntity(any(CityRequest.class), any(City.class));
		when(cityRepository.save(any(City.class))).thenReturn(city);
		when(cityReponseMapper.toResponse(any(City.class))).thenReturn(cityResponse);

		CityResponse result = cityService.update(cityRequest);
		assertThat(result).isNotNull();
		verify(cityRepository).save(any(City.class));
	}

	/**
	 * Prueba que delete elimine correctamente cuando la ciudad existe.
	 */
	@Test
	void delete_ShouldDeleteCity() {
		when(cityRepository.existsById((short) 1)).thenReturn(true);
		doNothing().when(cityRepository).deleteById((short) 1);

		cityService.delete((short) 1);
		verify(cityRepository).deleteById((short) 1);
	}

	/**
	 * Prueba que delete lance excepción si la ciudad no existe.
	 */
	@Test
	void delete_NotFound_ThrowsException() {
		when(cityRepository.existsById((short) 99)).thenReturn(false);

		assertThatThrownBy(() -> cityService.delete((short) 99))
				.isInstanceOf(InternalException.class);
	}

	/**
	 * Prueba la generación de Excel a partir de los datos de ciudades.
	 */
	@Test
	void generateExcel_ShouldReturnBase64String() {
		when(cityRepository.findAllPagintaion(any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(cityResponse)));

		String base64 = cityService.generateExcel();
		assertThat(base64).isNotNull().isNotEmpty();
	}

}
