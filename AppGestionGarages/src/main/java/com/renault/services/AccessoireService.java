package com.renault.services;

import java.util.List;

import com.renault.entities.Accessory;



public interface AccessoireService {

	Accessory addAccessoireToVehicule(Long vehiculeId,Accessory Accessoire);
	Accessory updateAccessoire(Long accesoireId, Accessory Accessoire);
	void deleteAccesoire(Long accesoireId);
	List<Accessory> getAccessoiresByVehicule(Long vehiculeId);
	
	
}
