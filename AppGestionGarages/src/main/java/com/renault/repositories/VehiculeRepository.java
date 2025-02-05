package com.renault.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renault.enums.TypeCarburant;
import com.renault.entities.Vehicle;

public interface VehiculeRepository extends JpaRepository<Vehicle, Long>{

		List<Vehicle> findByModel(String model);
		List<Vehicle> findByTypeCarburant(TypeCarburant typeCarburant);
}
