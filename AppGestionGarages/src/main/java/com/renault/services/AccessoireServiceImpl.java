package com.renault.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.renault.entities.Accessoire;
import com.renault.entities.Vehicule;
import com.renault.exceptions.AccessoireNotFoundException;
import com.renault.exceptions.VehiculeNotFoundException;
import com.renault.repositories.AccessoireRepository;
import com.renault.repositories.VehiculeRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@AllArgsConstructor
@Slf4j
public class AccessoireServiceImpl implements AccessoireService{
	
	private AccessoireRepository accessoireRepository;
	private VehiculeRepository vehiculeRepository;
	




	@Override
	public Accessoire addAccessoireToVehicule(Long vehiculeId, Accessoire accessoire) {
		log.info("add accessoire to vehicule");
		if(accessoire== null){
			throw new IllegalArgumentException("L\'accessoire ne peut pas être null");
		}
		Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
				.orElseThrow(() -> new VehiculeNotFoundException(vehiculeId));

		accessoire.setVehicule(vehicule);
		if (vehicule.getAccessoires() == null) {
			vehicule.setAccessoires(new ArrayList<>());
			
		}
		vehicule.getAccessoires().add(accessoire); // ajouter l'accessoire au vehicule
		accessoireRepository.save(accessoire); // sauvegrader l'accessoire

		return accessoire;
	}

	@Override
	public Accessoire updateAccessoire(Long accesoireId, Accessoire accessoireBody) {
		log.info("update accessoire with ID {} ",accesoireId);
		Accessoire accessoire = accessoireRepository.findById(accesoireId)
				.orElseThrow(() -> new AccessoireNotFoundException(accesoireId));
		accessoire.setName(accessoireBody.getName());
		accessoire.setDescription(accessoireBody.getDescription());
		accessoire.setPrice(accessoireBody.getPrice());
		accessoire.setType(accessoireBody.getType());

		accessoireRepository.save(accessoire);
		return accessoire;
	}

	@Override
	public void deleteAccesoire(Long accesoireId) {
		log.info("delete accessoire with ID {} ",accesoireId);
		Accessoire accessoire = accessoireRepository.findById(accesoireId)
	            .orElseThrow(() -> new AccessoireNotFoundException(accesoireId));
	            
	        // Détacher l'accessoire du véhicule s'il y en a un
	        if (accessoire.getVehicule() != null) {
	            accessoire.getVehicule().getAccessoires().remove(accessoire);
	            accessoire.setVehicule(null);
	        }
	        
	        accessoireRepository.deleteById(accesoireId);
	        
	        
	}

	@Override
	public List<Accessoire> getAccessoiresByVehicule(Long vehiculeId) {
		log.info("get accessoires by vehicule with ID{} ",vehiculeId);
		Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
				.orElseThrow(() ->  new VehiculeNotFoundException(vehiculeId));
		return vehicule.getAccessoires();
	}

}
