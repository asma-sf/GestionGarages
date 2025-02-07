package com.renault.services;

import java.util.List;

import com.renault.dtos.VehicleDto;
import com.renault.entities.Vehicle;



public interface VehicleService {
	
	VehicleDto addVehicleToGarage(Long garageId,VehicleDto vehicleDto);
	VehicleDto updateVehicle(Long vehiculeId ,VehicleDto vehicleDto);
	void deleteVehicle(Long vehicleId);
	List<VehicleDto> getVehicleByGarage(Long garageId);
	List<VehicleDto> getVehicleByModel(String model);
	

}
