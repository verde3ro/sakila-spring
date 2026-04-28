package mx.edu.upq.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import mx.edu.upq.response.PageResponse;
import mx.edu.upq.service.ICityService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor
@Tag(name = "Cities", description = "Operaciones relacionadas con ciudades")
@SecurityRequirement(name = "bearerAuth")
public class CityRestController {

	private final ICityService cityService;

	@Operation(summary = "Obtener todas las ciudades", description = "Devuelve lista completa de ciudades. Roles: USER, ADMIN")
	@ApiResponse(responseCode = "200", description = "Lista de ciudades obtenida correctamente")
	@PreAuthorize("hasAnyAuthority('USER','ADMIN')")
	@GetMapping("/")
	public ResponseEntity<List<CityResponse>> getCities() {
		return ResponseEntity.ok(cityService.findAll());
	}

	@Operation(summary = "Obtener ciudades paginadas", description = "Soporta paginación y ordenamiento")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Página de ciudades devuelta"),
			@ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
	})
	@PreAuthorize("hasAnyAuthority('USER','ADMIN')")
	@GetMapping("/pagination")
	public ResponseEntity<PageResponse<CityResponse>> getCities(
			// @RequestParam(defaultValue = "0") int page,
			// @RequestParam(defaultValue = "10") int size,
			// @RequestParam(defaultValue = "cityId") String sortField,
			// @RequestParam(defaultValue = "asc") String sortOrder
			@Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "cityId") String sortField,
			@Parameter(description = "Dirección del orden (asc/desc)") @RequestParam(defaultValue = "asc") String sortOrder
			) {
		Page<CityResponse> citiesPage =
				cityService.findAllPagintaion(page, size, sortField, sortOrder);

		return ResponseEntity.ok(new PageResponse<>(citiesPage));
	}

	@Operation(summary = "Obtener ciudad por ID", description = "Solo ADMIN")
	@ApiResponse(responseCode = "200", description = "Ciudad encontrada")
	@ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<CityResponse> getCity(@PathVariable short id) {
		return ResponseEntity.ok(cityService.findById(id));
	}

	@Operation(summary = "Crear nueva ciudad", description = "Solo ADMIN")
	@ApiResponse(responseCode = "200", description = "Ciudad creada")
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public ResponseEntity<CityResponse> create(@Valid @RequestBody CityRequest request) {
		return ResponseEntity.ok(cityService.create(request));
	}

	@Operation(summary = "Actualizar ciudad existente", description = "Solo ADMIN. Body: CityRequest con ID")
	@ApiResponse(responseCode = "200", description = "Ciudad actualizada")
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping
	public ResponseEntity<CityResponse> update(@Valid @RequestBody CityRequest request) {
		return ResponseEntity.ok(cityService.update(request));
	}

	@Operation(summary = "Eliminar ciudad", description = "Solo ADMIN")
	@ApiResponse(responseCode = "204", description = "Ciudad eliminada")
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable short id) {
		cityService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Exportar ciudades a Excel", description = "Devuelve archivo Excel en base64. Roles: USER, ADMIN")
	@ApiResponse(responseCode = "200", description = "Base64 del archivo Excel")
	@PreAuthorize("hasAnyAuthority('USER','ADMIN')")
	@GetMapping("/excel")
	public ResponseEntity<Map<String, String>> exportExcel() {
		String base64 = cityService.generateExcel();

		return ResponseEntity.ok(Map.of("base64", base64));
	}

}
