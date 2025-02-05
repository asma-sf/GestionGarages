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

import com.renault.entities.Accessory;
import com.renault.entities.Vehicle;
import com.renault.services.AccessoryService;
import com.renault.services.VehicleService;

import org.springframework.web.bind.annotation.RequestBody;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/api/vehicles")
public class VehicleController {
	

	private VehicleService vehicleService;
	private AccessoryService accessoryService;
	
	

	
	@PutMapping("/{vehicleId}")
	public Vehicle updateVehicle(@PathVariable Long vehicleId ,@RequestBody Vehicle vehicle) {
		return vehicleService.updateVehicle(vehicleId, vehicle);
		
	}
	@DeleteMapping("/{vehicleId}")
	public void deleteVehicle(@PathVariable Long vehicleId) {
		vehicleService.deleteVehicle(vehicleId);
	}
	@GetMapping("/garage/{garageId}")
	public List<Vehicle> getVehicleByGarage(@PathVariable Long garageId){
		return vehicleService.getVehicleByGarage(garageId);
		
	}
	@GetMapping("/byModel/{model}")
	public List<Vehicle> getVehicleByModel(@PathVariable String model){
		return vehicleService.getVehicleByModel(model);
	}
	
	@PostMapping("/{vehicleId}/accessories")
	public Accessory addAccessoryToVehicle(@PathVariable Long vehicleId,@RequestBody Accessory accessory) {
		
		return accessoryService.addAccessoryToVehicle(vehicleId, accessory);
		
	}

}
