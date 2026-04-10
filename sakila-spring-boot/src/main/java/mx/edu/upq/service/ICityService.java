package mx.edu.upq.service;

import mx.edu.upq.request.CityRequest;
import mx.edu.upq.response.CityResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICityService {
	List<CityResponse> findAll();

	Page<CityResponse> findAllPagintaion(int page, int size, String sortField, String sortOrder);

	CityResponse findById(short id);

	CityResponse create(CityRequest request);

	CityResponse update(CityRequest request);

	void delete(short id);

	String generateExcel();

}
