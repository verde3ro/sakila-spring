package mx.edu.upq.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import mx.edu.upq.service.ICityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba del controlador CityController centrada en la capa web.
 * Solo se carga el controlador y la configuración de Spring MVC/Security.
 * El servicio real (ICityService) se reemplaza por un mock.
 */
@WebMvcTest(CityRestController.class)
class CityRestControllerTest {

	@Autowired
	private MockMvc mockMvc;           // Cliente para simular peticiones HTTP

	@MockitoBean                        // Crea un mock del servicio y lo inyecta en el contexto
	private ICityService cityService;

	@Autowired
	private ObjectMapper objectMapper;  // Para serializar objetos a JSON

	/**
	 * Prueba GET /api/cities/ con rol USER.
	 * Verifica que se obtenga una lista de ciudades y que el primer elemento tenga el nombre esperado.
	 */
	@Test
	@WithMockUser(authorities = "USER")
	void getCities_ShouldReturnList() throws Exception {
		// Datos de prueba
		CityResponse response = new CityResponse(1, "Guadalajara", LocalDateTime.now(), 1, "Mexico");
		// Comportamiento simulado del servicio
		when(cityService.findAll()).thenReturn(List.of(response));

		// Ejecuta la petición y realiza aserciones sobre el resultado
		mockMvc.perform(get("/api/cities/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].city").value("Guadalajara"));
	}

	/**
	 * Prueba GET /api/cities/1 con rol ADMIN.
	 * Verifica que se retorne una ciudad con el ID solicitado.
	 */
	@Test
	@WithMockUser(authorities = "ADMIN")
	void getCityById_ShouldReturnCity() throws Exception {
		CityResponse response = new CityResponse(1, "Guadalajara", LocalDateTime.now(), 1, "Mexico");
		when(cityService.findById((short) 1)).thenReturn(response);

		mockMvc.perform(get("/api/cities/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cityId").value(1));
	}

	/**
	 * Prueba POST /api/cities con rol ADMIN.
	 * Envía una petición JSON con un token CSRF y comprueba que el controlador retorne la ciudad creada.
	 */
	@Test
	@WithMockUser(authorities = "ADMIN")
	void createCity_ShouldReturnCreated() throws Exception {
		// Request de entrada
		CityRequest request = new CityRequest();
		request.setCity("Nueva Ciudad");
		request.setCountryId((short) 1);
		request.setLastUpdate(LocalDateTime.now());

		// Respuesta simulada del servicio
		CityResponse response = new CityResponse(2, "Nueva Ciudad", LocalDateTime.now(), 1, "Mexico");
		when(cityService.create(any(CityRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/cities")
						.with(csrf())  // CSRF necesario para peticiones POST por la configuración de seguridad
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.city").value("Nueva Ciudad"));
	}

	/**
	 * Prueba DELETE /api/cities/1 con rol ADMIN.
	 * Verifica que la respuesta sea 204 (No Content) cuando se elimina correctamente.
	 */
	@Test
	@WithMockUser(authorities = "ADMIN")
	void deleteCity_ShouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/api/cities/1").with(csrf()))  // CSRF también necesario para DELETE
				.andExpect(status().isNoContent());
	}

	/**
	 * Prueba GET /api/cities/pagination?page=0&size=10 con rol USER.
	 * Simula una respuesta paginada y comprueba la estructura del JSON (propiedad "content" y primer elemento).
	 */
	@Test
	@WithMockUser(authorities = "USER")
	void getCitiesPagination_ShouldReturnPage() throws Exception {
		CityResponse city = new CityResponse(1, "Test City", LocalDateTime.now(), 1, "Test Country");
		Page<CityResponse> page = new PageImpl<>(List.of(city));
		when(cityService.findAllPagintaion(anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

		mockMvc.perform(get("/api/cities/pagination")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())   // La paginación de Spring incluye "content"
				.andExpect(jsonPath("$.content[0].city").value("Test City"));
	}

}
