package mx.edu.upq.response;

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
public class PageResponse<T extends Serializable> implements Serializable {

	@Serial
	private static final long serialVersionUID = -2322897591808151854L;
	private List<T> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	public PageResponse(Page<T> pageData) {
		this.content = pageData.getContent();
		this.page = pageData.getNumber();
		this.size = pageData.getSize();
		this.totalElements = pageData.getTotalElements();
		this.totalPages = pageData.getTotalPages();
	}

}
