package com.renault.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.renault.entities.Garage;

public interface GarageRepository  extends JpaRepository<Garage, Long>{
	@Query("SELECT DISTINCT g FROM Garage g " +
		       "JOIN g.vehicules v " +
		       "JOIN v.accessoires a " +
		       "WHERE a.nom = :accessoireName")
		List<Garage> findGaragesByAccessoire(@Param("accessoireName") String accessoireName);

	Garage findByName(String name);
}
