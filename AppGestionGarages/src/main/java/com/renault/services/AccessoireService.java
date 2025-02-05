package com.renault.services;

import java.util.List;

import com.renault.entities.Accessoire;



public interface AccessoireService {

	Accessoire addAccessoireToVehicule(Long vehiculeId,Accessoire Accessoire);
	Accessoire updateAccessoire(Long accesoireId, Accessoire Accessoire);
	void deleteAccesoire(Long accesoireId);
	List<Accessoire> getAccessoiresByVehicule(Long vehiculeId);
	
	
}
