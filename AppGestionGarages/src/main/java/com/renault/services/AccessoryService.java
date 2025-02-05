package com.renault.services;

import java.util.List;

import com.renault.entities.Accessory;



public interface AccessoryService {

	Accessory addAccessoryToVehicle(Long vehicleId,Accessory Accessory);
	Accessory updateAccessory(Long accessoryId, Accessory Accessory);
	void deleteAccessory(Long accessoryId);
	List<Accessory> getAccessoriesByVehicule(Long vehicleId);
	
	
}
