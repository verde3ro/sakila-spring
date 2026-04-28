package mx.edu.upq.rest;

import mx.edu.upq.response.CountryResponse;
import mx.edu.upq.service.ICountryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba del controlador CountryController.
 * Verifica el endpoint GET /api/countries/ con un mock del servicio ICountryService.
 */
@WebMvcTest(CountryRestController.class)
class CountryRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ICountryService countryService;

	/**
	 * Petición GET /api/countries/ con rol ADMIN.
	 * Simula la respuesta con una lista de países y comprueba que el primer país sea "Mexico".
	 */
	@Test
	@WithMockUser(authorities = "ADMIN")
	void getCountries_ShouldReturnList() throws Exception {
		// Datos de prueba: una respuesta de país
		CountryResponse response = new CountryResponse(1, "Mexico", LocalDateTime.now());
		// Simula que findAll() devuelve esa respuesta
		when(countryService.findAll()).thenReturn(List.of(response));

		mockMvc.perform(get("/api/countries/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].country").value("Mexico"));
	}

}
