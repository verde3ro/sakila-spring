package mx.edu.upq.rest;

import lombok.RequiredArgsConstructor;
import mx.edu.upq.response.CountryResponse;
import mx.edu.upq.service.ICountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coutries")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CountryController {

	private final ICountryService countryService;

	@GetMapping("/")
	public ResponseEntity<List<CountryResponse>> getCountries() {
		return ResponseEntity.ok(countryService.findAll());
	}

}
