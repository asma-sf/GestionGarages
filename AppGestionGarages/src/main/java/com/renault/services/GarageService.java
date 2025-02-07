package com.renault.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.renault.dtos.GarageDto;
import com.renault.entities.Garage;



public interface GarageService {

	GarageDto addGarage(GarageDto garageDto);
	GarageDto updateGarage(Long garageId,GarageDto garageDto);
	void deleteGarage(Long garageId);
	GarageDto getGarageById(Long garageId);
	Page<GarageDto> listGarages(Pageable pageable);
	
	List<GarageDto> searchGaragesByVehicleType(String vehicleType);
	List<GarageDto> searchGaragesByAccessory(String accessoryName);
	
	
}
