package mx.edu.upq.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import mx.edu.upq.response.CountryResponse;
import mx.edu.upq.service.ICountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

	private final ICountryService countryService;

	@Operation(summary = "Obtener todos los países", description = "Solo ADMIN")
	@ApiResponse(responseCode = "200", description = "Lista de países")
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/")
	public ResponseEntity<List<CountryResponse>> getCountries() {
		return ResponseEntity.ok(countryService.findAll());
	}

}
