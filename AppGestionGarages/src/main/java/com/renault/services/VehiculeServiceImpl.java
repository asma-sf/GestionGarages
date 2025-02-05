package com.renault.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.renault.entities.Accessoire;
import com.renault.entities.Garage;
import com.renault.entities.Vehicule;
import com.renault.enums.TypeCarburant;
import com.renault.exceptions.GarageLimitVehiculeException;
import com.renault.exceptions.GarageNotFoundException;
import com.renault.exceptions.VehiculeNotFoundException;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehiculeRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class VehiculeServiceImpl implements VehiculeService {
	private static final int MAX_VEHICULES = 50;

	private VehiculeRepository vehiculeRepository;
	private GarageRepository garageRepository;


	

	@Override
	public Vehicule addVehiculeToGarage(Long garageId, Vehicule vehicule) {

		if(vehicule ==null) {
			throw new IllegalArgumentException("Le vehicule ne peut pas être null");
		}
		// Récupérer le garage
		Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));

		if (garage.getVehicules() == null) {
		    garage.setVehicules(new ArrayList<>());
		}
		
		// Vérifier la limite de 50 véhicules
		if (garage.getVehicules().size() >= MAX_VEHICULES) {
			throw new GarageLimitVehiculeException(MAX_VEHICULES);
		}
		// Convertir le type du véhicule en majuscules
		if (vehicule.getTypeCarburant() != null) {
			vehicule.setTypeCarburant(TypeCarburant.valueOf(vehicule.getTypeCarburant().name().toUpperCase()));
		}
		vehicule.setGarage(garage);

		if (vehicule.getAccessoires() != null) {
			for (Accessoire acc : vehicule.getAccessoires()) {
				acc.setVehicule(vehicule); // Associer chaque accessoire au véhicule
			}
		}

		Vehicule savedVehicule = vehiculeRepository.save(vehicule);
		// Ajouter le véhicule à la liste du garage 
		garage.getVehicules().add(savedVehicule);

		//garageRepository.save(garage);

		return savedVehicule;
	}


	public Vehicule updateVehicule(Long vehiculeId, Vehicule vehicule) {
		log.info("Update vehicule with ID {}", vehiculeId);

		// Récupérer le véhicule existant ou lever une exception s'il n'existe pas
		Vehicule vehiculeToUpdate = vehiculeRepository.findById(vehiculeId)
				.orElseThrow(() -> new VehiculeNotFoundException(vehiculeId));

		// Mise à jour des champs seulement si les nouvelles valeurs ne sont pas nulles

		vehiculeToUpdate.setAnneeFabrication(vehicule.getAnneeFabrication());

		if (vehicule.getBrand() != null) {
			vehiculeToUpdate.setBrand(vehicule.getBrand());
		}
		if (vehicule.getTypeCarburant() != null) {
			vehiculeToUpdate.setTypeCarburant(vehicule.getTypeCarburant());
		}
		if (vehicule.getModel() != null) {
			vehiculeToUpdate.setModel(vehicule.getModel());
		}

		// si on veut modifier less accessoires , il faut passer par la méthode updateAccessoire  ou addAccesoireTo Vehicule 

		// Ne pas modifier le garage, si souhaité , il suffit juste d'enlever ce commentaire
		// Mise à jour du garage si présent
		/*
		 * if (vehicule.getGarage() != null) {
		 * vehiculeToUpdate.setGarage(vehicule.getGarage()); }
		 */

		// Sauvegarde de l'entité mise à jour
		vehiculeRepository.save(vehiculeToUpdate);

		return vehiculeToUpdate;
	}

	@Override
	public void deleteVehicule(Long vehiculeId) {
		log.info("delete vehicule  with ID {}", vehiculeId);
         Vehicule  vehicule = vehiculeRepository.findById(vehiculeId).orElseThrow(() -> new VehiculeNotFoundException(vehiculeId));
      // Détacher le vehicule du garage s'il y en a un
         if(vehicule.getGarage()!= null) {
        	 vehicule.getGarage().getVehicules().remove(vehicule);
        	 vehicule.setGarage(null);
         }
       vehiculeRepository.deleteById(vehiculeId);
	}

	@Override
	public List<Vehicule> getVehiculeByGarage(Long garageId) {
		log.info("get vehicules for garage with ID {}", garageId);
		Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));

		List<Vehicule> vehicules = garage.getVehicules();

		return vehicules;
	}

	@Override
	public List<Vehicule> getVehiculeByModel(String model) {
		log.info("get vehicules by model {}", model);
		List<Vehicule> vehicules = vehiculeRepository.findByModel(model);

		return vehicules;
	}

}
