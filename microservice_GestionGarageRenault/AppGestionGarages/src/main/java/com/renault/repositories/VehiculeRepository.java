package com.renault.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renault.enums.TypeVehicule;
import com.renault.entities.Vehicule;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long>{

		List<Vehicule> findByModel(String model);
		List<Vehicule> findByTypeVehicule(TypeVehicule typeVehicule);
}
