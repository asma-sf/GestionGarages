package com.renault.services;

import java.util.List;

import com.renault.dtos.AccessoryDto;
import com.renault.entities.Accessory;



public interface AccessoryService {

	AccessoryDto addAccessoryToVehicle(Long vehicleId,AccessoryDto accessoryDto);
	AccessoryDto updateAccessory(Long accessoryId, AccessoryDto accessoryDto);
	void deleteAccessory(Long accessoryId);
	List<AccessoryDto> getAccessoriesByVehicule(Long vehicleId);
	
	
}
