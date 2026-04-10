package mx.edu.upq.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import mx.edu.upq.response.PageResponse;
import mx.edu.upq.service.ICityService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CityController {

	private final ICityService cityService;

	@GetMapping("/")
	public ResponseEntity<List<CityResponse>> getCities() {
		return ResponseEntity.ok(cityService.findAll());
	}

	@GetMapping("/pagination")
	public ResponseEntity<PageResponse<CityResponse>> getCities(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "cityId") String sortField,
			@RequestParam(defaultValue = "asc") String sortOrder) {

		Page<CityResponse> citiesPage =
				cityService.findAllPagintaion(page, size, sortField, sortOrder);

		return ResponseEntity.ok(new PageResponse<>(citiesPage));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CityResponse> getCity(@PathVariable short id) {
		return ResponseEntity.ok(cityService.findById(id));
	}

	@PostMapping
	public ResponseEntity<CityResponse> create(@Valid @RequestBody CityRequest request) {
		return ResponseEntity.ok(cityService.create(request));
	}

	@PutMapping
	public ResponseEntity<CityResponse> update(@Valid @RequestBody CityRequest request) {
		return ResponseEntity.ok(cityService.update(request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable short id) {
		cityService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/excel")
	public ResponseEntity<Map<String, String>> exportExcel() {
		String base64 = cityService.generateExcel();

		return ResponseEntity.ok(Map.of("base64", base64));
	}

}
