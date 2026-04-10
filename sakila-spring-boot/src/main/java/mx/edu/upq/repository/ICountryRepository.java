package mx.edu.upq.repository;

import mx.edu.upq.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICountryRepository extends JpaRepository<Country, Short> {

}
