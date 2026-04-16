package mx.edu.upq.mapper;

import mx.edu.upq.model.Country;
import mx.edu.upq.response.CountryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICountryResponseMapper {

	@Mapping(source = "countryId", target = "countryId")
	@Mapping(source = "country", target = "country")
	@Mapping(source = "lastUpdate", target = "lastUpdate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
	CountryResponse toResponse(Country country);

	List<CountryResponse> toResponseList(List<Country> countries);

}
