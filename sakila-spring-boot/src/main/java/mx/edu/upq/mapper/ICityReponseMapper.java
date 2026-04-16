package mx.edu.upq.mapper;

import mx.edu.upq.model.City;
import mx.edu.upq.response.CityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICityReponseMapper {

	@Mapping(source = "cityId", target = "cityId")
	@Mapping(source = "city", target = "city")
	@Mapping(source = "lastUpdate", target = "lastUpdate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
	@Mapping(source = "country.countryId", target = "countryId")
	@Mapping(source = "country.country", target = "countryName")
	CityResponse toResponse(City city);

}
