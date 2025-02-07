package com.renault.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.renault.dtos.AccessoryDto;
import com.renault.entities.Accessory;
import com.renault.entities.Vehicle;
import com.renault.exceptions.AccessoryNotFoundException;
import com.renault.exceptions.VehicleNotFoundException;
import com.renault.mappers.AccessoryMapper;
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
	private AccessoryMapper accessoryMapper;
	




	@Override
	public AccessoryDto addAccessoryToVehicle(Long vehicleId, AccessoryDto accessoryDto) {
		log.info("add accessoire to vehicule");
		if(accessoryDto== null){
			throw new IllegalArgumentException("L\'accessoire ne peut pas être null");
		}
		Vehicle vehicle = vehiculeRepository.findById(vehicleId)
				.orElseThrow(() -> new VehicleNotFoundException(vehicleId));

		Accessory accessoryEntity = accessoryMapper.toAccessoryEntity(accessoryDto);
		accessoryEntity.setVehicule(vehicle);
		if (vehicle.getAccessories()== null) {
			vehicle.setAccessories(new ArrayList<>());
			
		}
		vehicle.getAccessories().add(accessoryEntity); // ajouter l'accessoire au vehicule
		accessoireRepository.save(accessoryEntity); // sauvegrader l'accessoire

		return accessoryMapper.toAccessoryDto(accessoryEntity);
	}

	@Override
	public AccessoryDto updateAccessory(Long accesoireId, AccessoryDto accessoryBody) {
		log.info("update accessoire with ID {} ",accesoireId);
		Accessory accessory = accessoireRepository.findById(accesoireId)
				.orElseThrow(() -> new AccessoryNotFoundException(accesoireId));
		Accessory AccessoryEntityBody=accessoryMapper.toAccessoryEntity(accessoryBody);
		accessory.setName(AccessoryEntityBody.getName());
		accessory.setDescription(AccessoryEntityBody.getDescription());
		accessory.setPrice(AccessoryEntityBody.getPrice());
		accessory.setType(AccessoryEntityBody.getType());

		accessoireRepository.save(accessory);
		return accessoryMapper.toAccessoryDto(accessory);
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
	public List<AccessoryDto> getAccessoriesByVehicule(Long vehicleId) {
		log.info("get accessoires by vehicule with ID{} ",vehicleId);
		Vehicle vehicule = vehiculeRepository.findById(vehicleId)
				.orElseThrow(() ->  new VehicleNotFoundException(vehicleId));
		return vehicule.getAccessories().stream().map(accessory -> accessoryMapper.toAccessoryDto(accessory)).toList();
	}

}
