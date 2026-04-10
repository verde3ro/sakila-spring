package mx.edu.upq.repository;

import mx.edu.upq.model.City;
import mx.edu.upq.response.CityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface ICityRepository extends JpaRepository<City, Short> {

	@Query("select new mx.edu.upq.response.CityResponse(c.cityId, c.city, c.lastUpdate, c.country.countryId, c.country.country) from City c where c.cityId = :cityId")
	Optional<CityResponse> findByCityId(@Param("cityId") short cityId);

	@Query("SELECT new mx.edu.upq.response.CityResponse(c.cityId, c.city, c.lastUpdate, co.countryId, co.country) FROM City c JOIN c.country co")
	Page<CityResponse> findAllPagintaion(Pageable pageable);

}
