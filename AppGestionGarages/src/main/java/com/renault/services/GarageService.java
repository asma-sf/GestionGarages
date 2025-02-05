package com.renault.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.renault.entities.Garage;



public interface GarageService {

	Garage addGarage(Garage Garage);
	Garage updateGarage(Long garageId,Garage Garage);
	void deleteGarage(Long garageId);
	Garage getGarageById(Long garageId);
	Page<Garage> listGarages(Pageable pageable);
	
	List<Garage> searchGaragesByVehicleType(String vehicleType);
	List<Garage> searchGaragesByAccessory(String accessoryName);
	
	
}
