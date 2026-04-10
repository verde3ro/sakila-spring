package mx.edu.upq.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.upq.exception.InternalException;
import mx.edu.upq.mapper.ICityMapper;
import mx.edu.upq.model.City;
import mx.edu.upq.model.Country;
import mx.edu.upq.repository.ICityRepository;
import mx.edu.upq.repository.ICountryRepository;
import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import mx.edu.upq.service.ICityService;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CityServiceImpl implements ICityService {

	private final ICityRepository cityRepository;
	private final ICountryRepository countryRepository;
	private final ICityMapper cityMapper;

	// Mapa para convertir el nombre del campo de ordenamiento al nombre real en la consulta JPQL
	private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
			"cityId", "c.cityId",
			"city", "c.city",
			"lastUpdate", "c.lastUpdate",
			"countryName", "co.country"
	);

	@Override
	public List<CityResponse> findAll() {
		Sort sort = Sort.by(Sort.Direction.ASC, "c.cityId");

		return cityRepository.findAllPagintaion(PageRequest.of(0, Integer.MAX_VALUE, sort)).getContent();
	}

	@Override
	public Page<CityResponse> findAllPagintaion(int page, int size, String sortField, String sortOrder) {
		String mappedSortField = SORT_FIELD_MAPPING.getOrDefault(sortField, "c.cityId");
		Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, mappedSortField);
		PageRequest pageable = PageRequest.of(page, size, sort);

		return cityRepository.findAllPagintaion(pageable);
	}

	@Override
	public CityResponse findById(short id) {
		return cityRepository.findByCityId(id)
				.orElseThrow(() -> new InternalException(String.format("Ciudad no encontrada con id: %d", id)));
	}

	@Override
	public CityResponse create(CityRequest request) {
		try {
			City city = new City();
			city.setCity(request.getCity());
			city.setLastUpdate(Timestamp.valueOf(request.getLastUpdate()));
			Country country = countryRepository.getReferenceById(request.getCountryId());
			city.setCountry(country);
			City saved = cityRepository.save(city);
			return toResponse(saved);
		} catch (Exception ex) {
			log.error("Error al crear ciudad", ex);
			throw new InternalException("Error al guardar la ciudad");
		}
	}

	@Override
	public CityResponse update(CityRequest request) {
		City city = cityRepository.findById(request.getCityId())
				.orElseThrow(() -> new InternalException("Ciudad no existe para actualizar"));
		try {
			city.setCity(request.getCity());
			city.setLastUpdate(Timestamp.valueOf(request.getLastUpdate()));
			if (!Objects.equals(city.getCountry().getCountryId(), request.getCountryId())) {
				Country country = countryRepository.getReferenceById(request.getCountryId());
				city.setCountry(country);
			}
			City updated = cityRepository.save(city);
			return toResponse(updated);
		} catch (Exception ex) {
			log.error("Error al actualizar ciudad id {}", request.getCityId(), ex);
			throw new InternalException("Error al actualizar la ciudad");
		}
	}

	@Override
	public void delete(short id) {
		if (!cityRepository.existsById(id)) {
			throw new InternalException("Ciudad a eliminar no existe");
		}
		try {
			cityRepository.deleteById(id);
		} catch (Exception ex) {
			log.error("Error al eliminar ciudad id {}", id, ex);
			throw new InternalException("Error al eliminar la ciudad");
		}
	}

	@Override
	public String generateExcel() {
		try {
			String base64 = null;
			String[] encabezado = {"Id", "Ciudad", "Fecha"};
			List<CityResponse> ciudades = this.findAll();

			// Creando libro
			try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				//Crear hoja
				XSSFSheet sheet = workbook.createSheet("Ciudades");
				// Creando estilo de la hoja
				CellStyle style = workbook.createCellStyle();
				// Creando Fuente
				XSSFFont font = workbook.createFont();
				font.setFontHeight(10);
				font.setFontName("Arial");
				font.setColor(IndexedColors.BLACK.getIndex());
				font.setBold(true);
				font.setItalic(false);

				// Se establece color de celda
				style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setFont(font);

				// Creando fila 0 del encabezado
				XSSFRow rowHead = sheet.createRow(0);

				// Escribiendo encabezado
				int i = 0;
				for (String titulo : encabezado) {
					// Creando celda
					XSSFCell cell = rowHead.createCell(i);
					cell.setCellStyle(style);
					cell.setCellValue(titulo);
					i++;
				}

				// Escribiendo Datos
				int rownum = 1;
				for (CityResponse ciudad : ciudades) {
					// Creando fila
					XSSFRow row = sheet.createRow(rownum);

					XSSFCell cell0 = row.createCell(0);
					cell0.setCellValue(ciudad.getCityId());

					XSSFCell cell1 = row.createCell(1);
					cell1.setCellValue(ciudad.getCity().trim());

					XSSFCell cell2 = row.createCell(2);
					cell2.setCellValue(
							ciudad.getLastUpdate().format(
									DateTimeFormatter.ofPattern("dd/MM/yyyy")
							)
					);

					rownum++;
				}

				// Se establece el ancho de columna al máximo para las columnas
				for (int position = 0; position < encabezado.length; position++) {
					sheet.autoSizeColumn(position);
				}

				workbook.write(bos);

				base64 = Base64.getEncoder().encodeToString(bos.toByteArray());
			}


			return base64;
		} catch (Exception ex) {
			log.error("Ocurrio un error al generar el archivo excel", ex);
			throw new InternalException("Ocurrio un error al generar el archivo excel");
		}
	}

	private CityResponse toResponse(City city) {
		return cityMapper.toResponse(city);
	}

}
