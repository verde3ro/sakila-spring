package mx.edu.upq.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta paginada genérica")
public class PageResponse<T extends Serializable> implements Serializable {

	@Serial
	private static final long serialVersionUID = -2322897591808151854L;
	@Schema(description = "Lista de elementos de la página actual")
	private List<T> content;
	@Schema(description = "Número de página (0‑based)", example = "0")
	private int page;
	@Schema(description = "Tamaño de página", example = "10")
	private int size;
	@Schema(description = "Total de elementos en todas las páginas", example = "150")
	private long totalElements;
	@Schema(description = "Total de páginas", example = "15")
	private int totalPages;

	public PageResponse(Page<T> pageData) {
		this.content = pageData.getContent();
		this.page = pageData.getNumber();
		this.size = pageData.getSize();
		this.totalElements = pageData.getTotalElements();
		this.totalPages = pageData.getTotalPages();
	}

}
