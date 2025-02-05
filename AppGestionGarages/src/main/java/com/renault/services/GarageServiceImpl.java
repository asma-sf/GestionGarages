package com.renault.services;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;
import com.renault.entities.Vehicule;
import com.renault.enums.TypeCarburant;
import com.renault.exceptions.GarageNotFoundException;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehiculeRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;



import jakarta.transaction.Transactional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class GarageServiceImpl implements GarageService {

	private GarageRepository garageRepository;
	private VehiculeRepository vehiculeRepository;
	


	@Override
	public Garage addGarage(Garage garage) {
	    log.info("Adding new garage: {}", garage.getName());

	    if (garage.getGarageOpeningTimes() != null) {
	        // Parcourir chaque GarageOpeningTime
	        for (Map.Entry<DayOfWeek, GarageOpeningTime> entry : garage.getGarageOpeningTimes().entrySet()) {
	            GarageOpeningTime garageOpeningTime = entry.getValue();
	            // Associer le garage à chaque GarageOpeningTime
	            garageOpeningTime.setDayOfWeek(entry.getKey());


	            if (garageOpeningTime.getOpeningTimes() != null) {
	                for (OpeningTime openingTime : garageOpeningTime.getOpeningTimes()) {
	                    // Associer le groupe d'ouverture au créneau horaire
	                    openingTime.setGroup(garageOpeningTime);
	                    openingTime.setDayOfWeek(entry.getKey());
	                }
	            }
	        }
	    }

	    return garageRepository.save(garage);
	}


	@Override
	public Garage updateGarage(Long garageId, Garage garageBody) {
	    log.info("Mise à jour du garage avec l'ID {}", garageId);

	    // Récupérer le garage existant
	    Garage garageToUpdate = garageRepository.findById(garageId)
	            .orElseThrow(() -> new GarageNotFoundException(garageId));

	    // Mettre à jour les informations de base du garage
	    garageToUpdate.setName(garageBody.getName());
	    garageToUpdate.setEmail(garageBody.getEmail());
	    garageToUpdate.setAddress(garageBody.getAddress());
	    garageToUpdate.setTelephone(garageBody.getTelephone());

	    // Mettre à jour les horaires d'ouverture
	    mettreAJourHorairesOuverture(garageToUpdate, garageBody);
	    
	    garageRepository.save(garageToUpdate);
	    return garageToUpdate;
	}

	private void mettreAJourHorairesOuverture(Garage garageToUpdate, Garage garageBody) {
	    // Ignorer si aucun horaire n'est fourni
	    if (garageBody.getGarageOpeningTimes() == null) {
	        return;
	    }
	  
	    Map<DayOfWeek, GarageOpeningTime> horairesExistants = garageToUpdate.getGarageOpeningTimes();
	    Map<DayOfWeek, GarageOpeningTime> nouveauxHoraires = garageBody.getGarageOpeningTimes();

	    // Mettre à jour ou ajouter de nouveaux horaires
	    nouveauxHoraires.forEach((jour, nouvelHoraire) -> {
	        GarageOpeningTime horaireExistant = horairesExistants.get(jour);

	        if (horaireExistant == null) {
	        	
	            // Ajouter les horaires d'un nouveau jour
	            horairesExistants.put(jour, nouvelHoraire);
	        } else {
	        	
	            // Mettre à jour les horaires du jour existant
	            mettreAJourHorairesJour(horaireExistant, nouvelHoraire);
	        }
	    });
	   
	    // Supprimer les jours qui ne sont plus dans les nouveaux horaires
	    horairesExistants.keySet().removeIf(jour -> !nouveauxHoraires.containsKey(jour));
	    
	    garageToUpdate.setGarageOpeningTimes(horairesExistants);
	}

	private void mettreAJourHorairesJour(GarageOpeningTime horaireExistant, GarageOpeningTime nouvelHoraire) {
	    // Effacer les horaires existants et ajouter les nouveaux
		
	   horaireExistant.getOpeningTimes().clear();
	   
	    // Ajouter les nouveaux horaires et définir la référence du groupe
	    nouvelHoraire.getOpeningTimes().forEach(horaireOuverture -> {
	        horaireOuverture.setGroup(horaireExistant);
	        horaireOuverture.setDayOfWeek(horaireExistant.getDayOfWeek());
	        horaireExistant.getOpeningTimes().add(horaireOuverture);
	    });
	    
	}



	@Override
	public void deleteGarage(Long garageId) {
		log.info("delete garage with ID {}", garageId);
		Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));
		if (garage != null) {
			garageRepository.deleteById(garageId);
		}

	}

	@Override
	public Garage getGarageById(Long garageId) {
		log.info("get garage with ID {}", garageId);
		Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));
		//return garageMapperImpl.fromGarage(garage);
		return garage;
	}

	@Override
	public Page<Garage> listGarages(Pageable pageable) {
		log.info("get paginated list of garages");
		Page<Garage> garages = garageRepository.findAll(pageable);
		
		return garages;
	}

	@Override
	public List<Garage> searchGaragesByVehiculeType(String vehiculeType) {
		log.info("search garages by vehicule type {}: ", vehiculeType);
		try {
			TypeCarburant type = TypeCarburant.valueOf(vehiculeType.toUpperCase());
			
			List<Vehicule> vehicules = vehiculeRepository.findByTypeCarburant(type);
			
			if (vehicules.isEmpty()) {
				return Collections.emptyList();
			}
			// Suppression des doublons avec un Set
			Set<Garage> garages = vehicules.stream().map(Vehicule::getGarage).filter(Objects::nonNull) // Évite les NullPointerException																										
					.collect(Collectors.toSet());
			

			return new ArrayList<>(garages);

		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Type de vehicule invalide : " + vehiculeType, e);
		}
	

	}

	@Override
	public List<Garage> searchGaragesByAccessoire(String accessoireName) {
		log.info("search garages by accessoire  {}: ", accessoireName);
		List<Garage> garages = garageRepository.findGaragesByAccessoire(accessoireName);

		return garages;
	}

}
