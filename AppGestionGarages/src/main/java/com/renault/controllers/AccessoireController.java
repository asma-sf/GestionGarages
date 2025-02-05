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

import com.renault.entities.Accessoire;
import com.renault.services.AccessoireService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/api/accessoires")
public class AccessoireController {
	
	
	private AccessoireService accessoireService;
	
	
	@PutMapping("/{accessoireId}")
	public Accessoire updateAccessoire(@PathVariable Long accessoireId,@RequestBody Accessoire accessoire) {
		return accessoireService.updateAccessoire(accessoireId, accessoire);
		
	}
	@DeleteMapping("/{accessoireId}")
	public void deleteAccesoire(@PathVariable Long accessoireId) {
		accessoireService.deleteAccesoire(accessoireId);
	}
	@GetMapping("/byVehicule/{vehiculeId}")
	public List<Accessoire> getAccessoiresByVehicule(@PathVariable Long vehiculeId){
		return accessoireService.getAccessoiresByVehicule(vehiculeId);
		
	}

}
