package com.renault.services;

import java.util.List;

import com.renault.entities.Vehicle;



public interface VehiculeService {
	
	Vehicle addVehiculeToGarage(Long garageId,Vehicle Vehicule);
	Vehicle updateVehicule(Long vehiculeId ,Vehicle Vehicule);
	void deleteVehicule(Long vehiculeId);
	List<Vehicle> getVehiculeByGarage(Long garageId);
	List<Vehicle> getVehiculeByModel(String model);
	

}
