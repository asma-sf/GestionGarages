package com.renault.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.renault.dtos.VehicleDto;
import com.renault.entities.Accessory;
import com.renault.entities.Garage;
import com.renault.entities.Vehicle;
import com.renault.enums.TypeCarburant;
import com.renault.exceptions.GarageLimitVehiculeException;
import com.renault.exceptions.GarageNotFoundException;
import com.renault.exceptions.VehicleNotFoundException;
import com.renault.mappers.VehicleMapper;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehicleRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class VehicleServiceImpl implements VehicleService {
	private static final int MAX_VEHICLES = 50;

	private VehicleRepository vehicleRepository;
	private GarageRepository garageRepository;
	private VehicleMapper vehicleMapper;


	

	@Override
	public VehicleDto addVehicleToGarage(Long garageId, VehicleDto vehicleDto) {

		if(vehicleDto ==null) {
			throw new IllegalArgumentException("Le vehicule ne peut pas être null");
		}
		// Récupérer le garage
		Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));

		if (garage.getVehicles() == null) {
		    garage.setVehicles(new ArrayList<>());
		}
		
		// Vérifier la limite de 50 véhicules
		if (garage.getVehicles().size() >= MAX_VEHICLES) {
			throw new GarageLimitVehiculeException(MAX_VEHICLES);
		}
		Vehicle vehicle =vehicleMapper.toVehicleEntity(vehicleDto);
		// Convertir le type du véhicule en majuscules
		if (vehicle.getTypeCarburant() != null) {
			vehicle.setTypeCarburant(TypeCarburant.valueOf(vehicle.getTypeCarburant().name().toUpperCase()));
		}
		vehicle.setGarage(garage);

		if (vehicle.getAccessories() != null) {
			for (Accessory acc : vehicle.getAccessories()) {
				acc.setVehicule(vehicle); // Associer chaque accessoire au véhicule
			}
		}

		Vehicle savedVehicule = vehicleRepository.save(vehicle);
		// Ajouter le véhicule à la liste du garage 
		garage.getVehicles().add(savedVehicule);

		//garageRepository.save(garage);

		return vehicleMapper.toVehicleDto(savedVehicule);
	}


	public VehicleDto updateVehicle(Long vehiculeId, VehicleDto vehicleDto) {
		log.info("Update vehicule with ID {}", vehiculeId);

		// Récupérer le véhicule existant ou lever une exception s'il n'existe pas
		Vehicle vehicleToUpdate = vehicleRepository.findById(vehiculeId)
				.orElseThrow(() -> new VehicleNotFoundException(vehiculeId));

		// Mise à jour des champs seulement si les nouvelles valeurs ne sont pas nulles

		Vehicle vehicle = vehicleMapper.toVehicleEntity(vehicleDto);
		
		vehicleToUpdate.setManufacturingYear((vehicle.getManufacturingYear()));
		if (vehicle.getBrand() != null) {
			vehicleToUpdate.setBrand(vehicle.getBrand());
		}
		if (vehicle.getTypeCarburant() != null) {
			vehicleToUpdate.setTypeCarburant(vehicle.getTypeCarburant());
		}
		if (vehicle.getModel() != null) {
			vehicleToUpdate.setModel(vehicle.getModel());
		}

		// si on veut modifier less accessoires , il faut passer par la méthode updateAccessoire  ou addAccesoireTo Vehicule 

		// Ne pas modifier le garage, si souhaité , il suffit juste d'enlever ce commentaire
		// Mise à jour du garage si présent
		/*
		 * if (vehicle.getGarage() != null) {
		 * vehicleToUpdate.setGarage(vehicle.getGarage()); }
		 */

		// Sauvegarde de l'entité mise à jour
		vehicleRepository.save(vehicleToUpdate);

		return vehicleMapper.toVehicleDto(vehicleToUpdate);
	}

	@Override
	public void deleteVehicle(Long vehicleId) {
		log.info("delete vehicle  with ID {}", vehicleId);
         Vehicle  vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new VehicleNotFoundException(vehicleId));
      // Détacher le vehicule du garage s'il y en a un
         if(vehicle.getGarage()!= null) {
        	 vehicle.getGarage().getVehicles().remove(vehicle);
        	 vehicle.setGarage(null);
         }
       vehicleRepository.deleteById(vehicleId);
	}

	@Override
	public List<VehicleDto> getVehicleByGarage(Long garageId) {
		log.info("get vehicles for garage with ID {}", garageId);
		Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));

		List<Vehicle> vehicles = garage.getVehicles();

		return vehicles.stream().map(vehicule -> vehicleMapper.toVehicleDto(vehicule)).toList();
	}

	@Override
	public List<VehicleDto> getVehicleByModel(String model) {
		log.info("get vehicles by model {}", model);
		List<Vehicle> vehicles = vehicleRepository.findByModel(model);

		return vehicles.stream().map(vehicule -> vehicleMapper.toVehicleDto(vehicule)).toList();
	}

}
