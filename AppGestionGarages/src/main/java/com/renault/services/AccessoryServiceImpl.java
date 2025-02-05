package com.renault.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.renault.entities.Accessory;
import com.renault.entities.Vehicle;
import com.renault.exceptions.AccessoryNotFoundException;
import com.renault.exceptions.VehicleNotFoundException;
import com.renault.repositories.AccessoryRepository;
import com.renault.repositories.VehicleRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@AllArgsConstructor
@Slf4j
public class AccessoryServiceImpl implements AccessoryService{
	
	private AccessoryRepository accessoireRepository;
	private VehicleRepository vehiculeRepository;
	




	@Override
	public Accessory addAccessoryToVehicle(Long vehicleId, Accessory accessory) {
		log.info("add accessoire to vehicule");
		if(accessory== null){
			throw new IllegalArgumentException("L\'accessoire ne peut pas être null");
		}
		Vehicle vehicule = vehiculeRepository.findById(vehicleId)
				.orElseThrow(() -> new VehicleNotFoundException(vehicleId));

		accessory.setVehicule(vehicule);
		if (vehicule.getAccessories()== null) {
			vehicule.setAccessories(new ArrayList<>());
			
		}
		vehicule.getAccessories().add(accessory); // ajouter l'accessoire au vehicule
		accessoireRepository.save(accessory); // sauvegrader l'accessoire

		return accessory;
	}

	@Override
	public Accessory updateAccessory(Long accesoireId, Accessory accessoryBody) {
		log.info("update accessoire with ID {} ",accesoireId);
		Accessory accessoire = accessoireRepository.findById(accesoireId)
				.orElseThrow(() -> new AccessoryNotFoundException(accesoireId));
		accessoire.setName(accessoryBody.getName());
		accessoire.setDescription(accessoryBody.getDescription());
		accessoire.setPrice(accessoryBody.getPrice());
		accessoire.setType(accessoryBody.getType());

		accessoireRepository.save(accessoire);
		return accessoire;
	}

	@Override
	public void deleteAccessory(Long accessoryId) {
		log.info("delete accessoire with ID {} ",accessoryId);
		Accessory accessory = accessoireRepository.findById(accessoryId)
	            .orElseThrow(() -> new AccessoryNotFoundException(accessoryId));
	            
	        // Détacher l'accessoire du véhicule s'il y en a un
	        if (accessory.getVehicule() != null) {
	            accessory.getVehicule().getAccessories().remove(accessory);
	            accessory.setVehicule(null);
	        }
	        
	        accessoireRepository.deleteById(accessoryId);
	        
	        
	}

	@Override
	public List<Accessory> getAccessoriesByVehicule(Long vehicleId) {
		log.info("get accessoires by vehicule with ID{} ",vehicleId);
		Vehicle vehicule = vehiculeRepository.findById(vehicleId)
				.orElseThrow(() ->  new VehicleNotFoundException(vehicleId));
		return vehicule.getAccessories();
	}

}
