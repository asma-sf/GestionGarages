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

import com.renault.dtos.GarageDto;
import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;
import com.renault.entities.Vehicle;
import com.renault.enums.TypeCarburant;
import com.renault.exceptions.GarageNotFoundException;
import com.renault.mappers.GarageMapper;
import com.renault.repositories.GarageOpeningTimeRepository;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehicleRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;



import jakarta.transaction.Transactional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class GarageServiceImpl implements GarageService {

	private GarageRepository garageRepository;
	private VehicleRepository vehiculeRepository;
	private GarageMapper garageMapper;
	private GarageOpeningTimeRepository garageOpeningTimeRepository;


	@Override
	public GarageDto addGarage(GarageDto garageBodyDto) {
	    log.info("Adding new garage: {}", garageBodyDto.name());

	    Garage garage =garageMapper.toGarage(garageBodyDto);
	    if (garage.getOpeningTimes() != null) {
	        // Parcourir chaque GarageOpeningTime
	        for (Map.Entry<DayOfWeek, GarageOpeningTime> entry : garage.getOpeningTimes().entrySet()) {
	            GarageOpeningTime garageOpeningTime = entry.getValue();
	            // Associer le garage à chaque GarageOpeningTime
	            garageOpeningTime.setDayOfWeek(entry.getKey());


	            if (garageOpeningTime.getOpeningTimes() != null) {
	                for (OpeningTime openingTime : garageOpeningTime.getOpeningTimes()) {
	                    // Associer le groupe d'ouverture au créneau horaire
	                    openingTime.setGarageOpeningTime(garageOpeningTime);
	                    openingTime.setDayOfWeek(entry.getKey());
	                }
	            }
	        }
	    }
	    garageRepository.save(garage);
	    return garageMapper.toGarageDto(garage);
	}


	@Override
	public GarageDto updateGarage(Long garageId, GarageDto garageDto) {
	    log.info("Mise à jour du garage avec l'ID {}", garageId);

	    // Récupérer le garage existant
	    Garage garageToUpdate = garageRepository.findById(garageId)
	            .orElseThrow(() -> new GarageNotFoundException(garageId));

	    Garage garageBody = garageMapper.toGarage(garageDto);
	    // Mettre à jour les informations de base du garage
	    garageToUpdate.setName(garageBody.getName());
	    garageToUpdate.setEmail(garageBody.getEmail());
	    garageToUpdate.setAddress(garageBody.getAddress());
	    garageToUpdate.setPhone(garageBody.getPhone());

	    // Mettre à jour les horaires d'ouverture
	    mettreAJourHorairesOuverture(garageToUpdate, garageBody);
	    
	    garageRepository.save(garageToUpdate);
	    return garageMapper.toGarageDto(garageToUpdate);
	}

	private void mettreAJourHorairesOuverture(Garage garageToUpdate, Garage garageBody) {
	    if (garageBody.getOpeningTimes() == null) {
	        return;
	    }

	    Map<DayOfWeek, GarageOpeningTime> horairesExistants = garageToUpdate.getOpeningTimes();
	    Map<DayOfWeek, GarageOpeningTime> nouveauxHoraires = garageBody.getOpeningTimes();

	    // Gérer l'ajout et la mise à jour des horaires
	    nouveauxHoraires.forEach((jour, nouvelHoraire) -> {
	        GarageOpeningTime horaireExistant = horairesExistants.get(jour);

	        if (horaireExistant == null) {
	            // Créer un nouvel horaire pour un jour inexistant
	            GarageOpeningTime nouvelHoraireJour = new GarageOpeningTime();
	            nouvelHoraireJour.setDayOfWeek(jour);

	            nouvelHoraire.getOpeningTimes().forEach(horaire -> {
	                horaire.setGarageOpeningTime(nouvelHoraireJour);
	                horaire.setDayOfWeek(jour);
	                nouvelHoraireJour.getOpeningTimes().add(horaire);
	            });

	            horairesExistants.put(jour, nouvelHoraireJour);
	        } else {

	            // Mettre à jour les horaires existants
	            horaireExistant.getOpeningTimes().clear();
	            nouvelHoraire.getOpeningTimes().forEach(horaire -> {
	                horaire.setGarageOpeningTime(horaireExistant);
	                horaire.setDayOfWeek(horaireExistant.getDayOfWeek());
	                horaireExistant.getOpeningTimes().add(horaire);
	            });
	        }
	    });

	    // Supprimer les jours qui ne sont plus dans les nouveaux horaires
	    horairesExistants.keySet().removeIf(jour -> !nouveauxHoraires.containsKey(jour));

	    garageToUpdate.setOpeningTimes(horairesExistants);
	}

	private void mettreAJourHorairesJour(GarageOpeningTime horaireExistant, GarageOpeningTime nouvelHoraire) {
	  
	    
	    // Supprime les anciens horaires 
	    horaireExistant.getOpeningTimes().clear();
	   

	    // Vérifie que 'nouvelHoraire' est bien persistant avant d'ajouter les horaires
	    if (nouvelHoraire.getId() == null) {
	        garageOpeningTimeRepository.save(nouvelHoraire);
	    }

	    // Ajouter les nouveaux horaires en leur associant la journée et la référence du groupe
	    nouvelHoraire.getOpeningTimes().forEach(horaireOuverture -> {
	        horaireOuverture.setGarageOpeningTime(horaireExistant);
	        horaireOuverture.setDayOfWeek(horaireExistant.getDayOfWeek());
	        horaireExistant.getOpeningTimes().add(horaireOuverture);
	    });


	   

	    // Forcer la mise à jour dans la base de données
	    garageOpeningTimeRepository.save(horaireExistant);
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
	public GarageDto getGarageById(Long garageId) {
		log.info("get garage with ID {}", garageId);
		Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));
		//return garageMapperImpl.fromGarage(garage);
		return garageMapper.toGarageDto(garage);
	}

	@Override
	public Page<GarageDto> listGarages(Pageable pageable) {
		log.info("get paginated list of garages");
		Page<Garage> garages = garageRepository.findAll(pageable);
		
		return garages.map(garageMapper::toGarageDto);
	}

	@Override
	public List<GarageDto> searchGaragesByVehicleType(String vehiculeType) {
		log.info("search garages by vehicule type {}: ", vehiculeType);
		try {
			TypeCarburant type = TypeCarburant.valueOf(vehiculeType.toUpperCase());
			
			List<Vehicle> vehicules = vehiculeRepository.findByTypeCarburant(type);
			
			if (vehicules.isEmpty()) {
				return Collections.emptyList();
			}
			// Suppression des doublons avec un Set
			Set<Garage> garages = vehicules.stream().map(Vehicle::getGarage).filter(Objects::nonNull) // Évite les NullPointerException																										
					.collect(Collectors.toSet());
			

			return new ArrayList<>(garages.stream().map(garage -> garageMapper.toGarageDto(garage)).toList());

		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Type de vehicule invalide : " + vehiculeType, e);
		}
	

	}

	@Override
	public List<GarageDto> searchGaragesByAccessory(String accessoryName) {
		log.info("search garages by accessoire  {}: ", accessoryName);
		List<Garage> garages = garageRepository.findGaragesByAccessory(accessoryName);

		return garages.stream().map(garage -> garageMapper.toGarageDto(garage)).toList();
	}



}
