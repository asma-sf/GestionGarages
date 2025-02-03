package com.renault.services;

import java.util.List;

import com.renault.entities.Vehicule;



public interface VehiculeService {
	
	Vehicule addVehiculeToGarage(Long garageId,Vehicule Vehicule);
	Vehicule updateVehicule(Long vehiculeId ,Vehicule Vehicule);
	void deleteVehicule(Long vehiculeId);
	List<Vehicule> getVehiculeByGarage(Long garageId);
	List<Vehicule> getVehiculeByModel(String model);
	

}
