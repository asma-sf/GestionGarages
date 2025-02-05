package com.renault.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renault.entities.Accessory;
import com.renault.services.AccessoryService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/api/accessories")
public class AccessoryController {
	
	
	private AccessoryService accessoryService;
	
	
	@PutMapping("/{accessoryId}")
	public Accessory updateAccessoire(@PathVariable Long accessoryId,@RequestBody Accessory accessory) {
		return accessoryService.updateAccessory(accessoryId, accessory);
		
	}
	@DeleteMapping("/{accessoryId}")
	public void deleteAccesoire(@PathVariable Long accessoryId) {
		accessoryService.deleteAccessory(accessoryId);
	}
	@GetMapping("/byVehicle/{vehicleId}")
	public List<Accessory> getAccessoiresByVehicule(@PathVariable Long vehicleId){
		return accessoryService.getAccessoriesByVehicule(vehicleId);
		
	}

}
