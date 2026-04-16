package mx.edu.upq.mapper;

import mx.edu.upq.model.City;
import mx.edu.upq.request.CityRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ICityRequestMapper {

	@Mapping(target = "cityId", ignore = true)
	@Mapping(target = "country", ignore = true)
	@Mapping(target = "lastUpdate", source = "lastUpdate")
	City toEntity(CityRequest request);

	@Mapping(target = "country", ignore = true)
	void updateEntity(CityRequest request, @MappingTarget City city);

	default Timestamp map(LocalDateTime value) {
		return value == null ? null : Timestamp.valueOf(value);
	}

}
