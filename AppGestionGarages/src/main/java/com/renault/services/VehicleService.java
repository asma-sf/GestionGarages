package com.renault.services;

import java.util.List;

import com.renault.entities.Vehicle;



public interface VehicleService {
	
	Vehicle addVehicleToGarage(Long garageId,Vehicle Vehicle);
	Vehicle updateVehicle(Long vehiculeId ,Vehicle Vehicle);
	void deleteVehicle(Long vehicleId);
	List<Vehicle> getVehicleByGarage(Long garageId);
	List<Vehicle> getVehicleByModel(String model);
	

}
