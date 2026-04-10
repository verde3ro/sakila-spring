package mx.edu.upq.service;

import mx.edu.upq.response.CountryResponse;

import java.util.List;

public interface ICountryService {

	List<CountryResponse> findAll();

}
