package com.renault.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.renault.entities.Garage;

public interface GarageRepository  extends JpaRepository<Garage, Long>{
	@Query("SELECT DISTINCT g FROM Garage g " +
		       "JOIN g.vehicles v " +
		       "JOIN v.accessories a " +
		       "WHERE a.name = :accessoryName")
		List<Garage> findGaragesByAccessory(@Param("accessoryName") String accessoryName);

	Garage findByName(String name);
}
