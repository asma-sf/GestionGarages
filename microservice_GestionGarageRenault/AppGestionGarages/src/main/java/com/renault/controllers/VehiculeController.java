package com.renault.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renault.entities.Accessoire;
import com.renault.entities.Vehicule;
import com.renault.services.AccessoireService;
import com.renault.services.VehiculeService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("api/vehicules")
public class VehiculeController {
	

	private VehiculeService vehiculeService;
	private AccessoireService accessoireService;
	
	

	public VehiculeController(VehiculeService vehiculeService, AccessoireService accessoireService) {
		this.vehiculeService = vehiculeService;
		this.accessoireService = accessoireService;
	}
	
	@PutMapping("/{vehiculeId}")
	public Vehicule updateVehicule(@PathVariable Long vehiculeId ,@RequestBody Vehicule vehicule) {
		return vehiculeService.updateVehicule(vehiculeId, vehicule);
		
	}
	@DeleteMapping("/{vehiculeId}")
	public void deleteVehicule(@PathVariable Long vehiculeId) {
		vehiculeService.deleteVehicule(vehiculeId);
	}
	@GetMapping("/garage/{garageId}")
	public List<Vehicule> getVehiculeByGarage(@PathVariable Long garageId){
		return vehiculeService.getVehiculeByGarage(garageId);
		
	}
	@GetMapping("/byModel/{model}")
	public List<Vehicule> getVehiculeByModel(@PathVariable String model){
		return vehiculeService.getVehiculeByModel(model);
	}
	
	@PostMapping("/{vehiculeId}/accessoires")
	public Accessoire addAccessoireToVehicule(@PathVariable Long vehiculeId,@RequestBody Accessoire accessoire) {
		
		return accessoireService.addAccessoireToVehicule(vehiculeId, accessoire);
		
	}

}
