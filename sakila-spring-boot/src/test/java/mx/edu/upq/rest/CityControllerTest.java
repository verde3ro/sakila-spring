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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CityController.class)
class CityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ICityService cityService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser(authorities = "USER")
	void getCities_ShouldReturnList() throws Exception {
		CityResponse response = new CityResponse(1, "Guadalajara", LocalDateTime.now(), 1, "Mexico");
		when(cityService.findAll()).thenReturn(List.of(response));

		mockMvc.perform(get("/api/cities/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].city").value("Guadalajara"));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	void getCityById_ShouldReturnCity() throws Exception {
		CityResponse response = new CityResponse(1, "Guadalajara", LocalDateTime.now(), 1, "Mexico");
		when(cityService.findById((short) 1)).thenReturn(response);

		mockMvc.perform(get("/api/cities/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cityId").value(1));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	void createCity_ShouldReturnCreated() throws Exception {
		CityRequest request = new CityRequest();
		request.setCity("Nueva Ciudad");
		request.setCountryId((short) 1);
		request.setLastUpdate(LocalDateTime.now());

		CityResponse response = new CityResponse(2, "Nueva Ciudad", LocalDateTime.now(), 1, "Mexico");
		when(cityService.create(any(CityRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/cities")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.city").value("Nueva Ciudad"));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	void deleteCity_ShouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/api/cities/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(authorities = "USER")
	void getCitiesPagination_ShouldReturnPage() throws Exception {
		Page<CityResponse> page = new PageImpl<>(List.of(new CityResponse()));
		when(cityService.findAllPagintaion(anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

		mockMvc.perform(get("/api/cities/pagination")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray());
	}

}
