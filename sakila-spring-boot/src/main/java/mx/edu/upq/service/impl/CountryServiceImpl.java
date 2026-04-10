package mx.edu.upq.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.upq.mapper.ICountryMapper;
import mx.edu.upq.repository.ICountryRepository;
import mx.edu.upq.response.CountryResponse;
import mx.edu.upq.service.ICountryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CountryServiceImpl implements ICountryService {

	private final ICountryRepository countryRepository;
	private final ICountryMapper countryMapper;

	@Override
	public List<CountryResponse> findAll() {
		return countryMapper.toResponseList(countryRepository.findAll());
	}

}
