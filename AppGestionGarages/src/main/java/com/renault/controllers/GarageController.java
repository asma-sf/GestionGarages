package com.renault.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renault.entities.Garage;
import com.renault.entities.Vehicule;
import com.renault.services.GarageService;
import com.renault.services.VehiculeService;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/api/garages")
public class GarageController {
	

	private GarageService garageService;	
	private VehiculeService vehiculeService;
	



	@PostMapping
	public Garage addGarage(@RequestBody Garage garage) {	
		return garageService.addGarage(garage);	
	}
	
	@PutMapping("/{garageId}")
	public Garage updateGarage(@PathVariable Long garageId,@RequestBody Garage garage) {
		return garageService.updateGarage(garageId, garage);
		
	}
	@DeleteMapping("/{garageId}")
	public void deleteGarage(@PathVariable Long garageId) {
		garageService.deleteGarage(garageId);
	}

	@GetMapping("/{garageId}")
	public Garage getGarageById(@PathVariable Long garageId) {
		return garageService.getGarageById(garageId);	
	}
	@GetMapping
	public Page<Garage> listGarages(Pageable pageable){
		return garageService.listGarages(pageable);
		
	}
	@PostMapping("/{garageId}/vehicules")
	public Vehicule addVehiculeToGarage(@PathVariable Long garageId,@RequestBody Vehicule vehicule) {

		return vehiculeService.addVehiculeToGarage(garageId, vehicule);	
	}
	@GetMapping("/byVehiculeType/{vehiculeType}")
	public List<Garage> searchGaragesByVehiculeType(@PathVariable String vehiculeType) {
		return garageService.searchGaragesByVehiculeType(vehiculeType);
		
	}
	@GetMapping("/byAccessoire/{accessoireName}")
	public List<Garage> accessoireName(@PathVariable String accessoireName){
		return garageService.searchGaragesByAccessoire(accessoireName);
		
	}
}
