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

@WebMvcTest(CountryController.class)
class CountryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ICountryService countryService;

	@Test
	@WithMockUser(authorities = "ADMIN")
	void getCountries_ShouldReturnList() throws Exception {
		CountryResponse response = new CountryResponse(1, "Mexico", LocalDateTime.now());
		when(countryService.findAll()).thenReturn(List.of(response));

		mockMvc.perform(get("/api/coutries/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].country").value("Mexico"));
	}

}
