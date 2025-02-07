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

import com.renault.dtos.GarageDto;
import com.renault.dtos.VehicleDto;
import com.renault.entities.Garage;
import com.renault.entities.Vehicle;
import com.renault.services.GarageService;
import com.renault.services.VehicleService;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/api/garages")
public class GarageController {
	

	private GarageService garageService;	
	private VehicleService vehicleService;
	



	@PostMapping
	public GarageDto addGarage(@RequestBody GarageDto garageDto) {	
		return garageService.addGarage(garageDto);	
	}
	
	@PutMapping("/{garageId}")
	public GarageDto updateGarage(@PathVariable Long garageId,@RequestBody GarageDto garageDto) {
		return garageService.updateGarage(garageId, garageDto);
		
	}
	@DeleteMapping("/{garageId}")
	public void deleteGarage(@PathVariable Long garageId) {
		garageService.deleteGarage(garageId);
	}

	@GetMapping("/{garageId}")
	public GarageDto getGarageById(@PathVariable Long garageId) {
		return garageService.getGarageById(garageId);	
	}
	@GetMapping
	public Page<GarageDto> listGarages(Pageable pageable){
		return garageService.listGarages(pageable);
		
	}
	@PostMapping("/{garageId}/vehicles")
	public VehicleDto addVehicleToGarage(@PathVariable Long garageId,@RequestBody VehicleDto vehicle) {

		return vehicleService.addVehicleToGarage(garageId, vehicle);	
	}
	@GetMapping("/byVehicleType/{vehicleType}")
	public List<GarageDto> searchGaragesByVehicleType(@PathVariable String vehicleType) {
		return garageService.searchGaragesByVehicleType(vehicleType);
		
	}
	@GetMapping("/byAccessory/{accessoryName}")
	public List<GarageDto> accessoireName(@PathVariable String accessoryName){
		return garageService.searchGaragesByAccessory(accessoryName);
		
	}
}
