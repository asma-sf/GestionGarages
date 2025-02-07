package com.renault.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.renault.dtos.AccessoryDto;
import com.renault.dtos.VehicleDto;
import com.renault.entities.Accessory;
import com.renault.entities.Vehicle;
import com.renault.enums.TypeCarburant;
import com.renault.mappers.AccessoryMapper;
import com.renault.mappers.VehicleMapper;
import com.renault.repositories.AccessoryRepository;
import com.renault.repositories.VehicleRepository;

import jakarta.transaction.Transactional;


public class AccessoryServiceTest {

	@Mock
	private AccessoryRepository accessoryRepository;
	
	@Mock
    private VehicleRepository vehicleRepository;
	
	@Mock
	private AccessoryMapper accessoryMapper;
	
	@Mock
	private VehicleMapper vehicleMapper;
	
	@InjectMocks	
	private AccessoryServiceImpl accessoryService;
	
	@BeforeEach
	void step() {
		MockitoAnnotations.openMocks(this);
	}
	
	private Accessory createTestAccessory() {
		return Accessory.builder()
				.id(1L)
    			.name("Caméra")
    			.description("Caméra de recul")
    			.price(2000)
    			.type("Sécurité")
    			.build();
		
	}
	private AccessoryDto createTestAccessoryDto() {
		return AccessoryDto.builder()
				.id(1L)
    			.name("Caméra")
    			.description("Caméra de recul")
    			.price(2000)
    			.type("Sécurité")
    			.build();
		
	}
	
	private Vehicle createTestVehicle() {
		return Vehicle.builder()
				.id(1L)
    			.brand("Renault")
    			.manufacturingYear(2021)
    			.model("Captur")
    			.typeCarburant(TypeCarburant.ESSENCE)
    			.accessories(new ArrayList<>())
    			.build();
		
	}
	private VehicleDto createTestVehicleDto() {
		return VehicleDto.builder()
				.id(1L)
    			.brand("Renault")
    			.manufacturingYear(2021)
    			.model("Captur")
    			.typeCarburant(TypeCarburant.ESSENCE)
    			.accessories(new ArrayList<>())
    			.build();
		
	}
	
	@Test
	void testAddAccessorieToVehicule() {
	    // Création des objets de test
	    Accessory accessory = createTestAccessory();
	    AccessoryDto accessoryDto = createTestAccessoryDto();
	    Vehicle vehicle = createTestVehicle();
	    
	    // Mock des mappers
	    when(accessoryMapper.toAccessoryDto(any(Accessory.class))).thenReturn(accessoryDto);
	    when(vehicleMapper.toVehicleDto(any(Vehicle.class))).thenReturn(createTestVehicleDto());

	    // Vérifier que la liste est bien initialisée
	    assertNotNull(vehicle.getAccessories());
	    assertTrue(vehicle.getAccessories().isEmpty()); // Liste vide avant ajout
	    assertNotNull(vehicle.getAccessories());

	    // Mock des repositories
	    when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
	    when(accessoryRepository.save(any(Accessory.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    // Mock des mappers
	    when(accessoryMapper.toAccessoryEntity(any(AccessoryDto.class))).thenReturn(accessory);

	    // Mise à jour de l'objet VehicleDto après ajout de l'accessoire
	    VehicleDto updatedVehicleDto = VehicleDto.builder()
	            .id(1L)
	            .brand("Renault")
	            .model("Captur")
	            .typeCarburant(TypeCarburant.ESSENCE)
	            .manufacturingYear(2021)
	            .garageID(null)
	            .accessories(Arrays.asList(accessoryDto))
	            .build();

	    when(vehicleMapper.toVehicleDto(vehicle)).thenReturn(updatedVehicleDto);

	    // Appel à la méthode à tester
	    AccessoryDto accessoireResultDto = accessoryService.addAccessoryToVehicle(vehicle.getId(), accessoryDto);

	    // Vérifications données
	    assertNotNull(accessoireResultDto);
	    assertEquals("Caméra", accessoireResultDto.name());
	    assertEquals(2000, accessoireResultDto.price());
	    
	    // Vérification que le véhicule contient un accessoire
	    assertNotNull(vehicle.getAccessories());
	    assertEquals(1, vehicle.getAccessories().size());
	    assertTrue(vehicle.getAccessories().contains(accessory)); 

	    assertNotNull(updatedVehicleDto.accessories());
	    assertEquals(1, updatedVehicleDto.accessories().size());
	    assertTrue(updatedVehicleDto.accessories().contains(accessoryDto)); 
	    
	    // Vérifications des appels aux mocks
	    verify(vehicleRepository, times(1)).findById(vehicle.getId());
	    verify(accessoryRepository, times(1)).save(accessory);

	    
	}

	@Test
	void testUpdateAccessorie() {
	    // Création de l'objet accessoire
	    Accessory accessoire = createTestAccessory();
	    // Simuler la récupération de l'accessoire par ID
	    when(accessoryRepository.findById(1L)).thenReturn(java.util.Optional.of(accessoire));
	    
	    // Création de l'AccessoryDto avec les nouvelles données
	    AccessoryDto accessoryDto = AccessoryDto.builder()
	            .id(1L)
	            .name("Caméra de recul")
	            .description("Caméra de recul")
	            .price(2300)
	            .type("Sécurité")
	            .build();
	    
	    // Mock des mappers
	    when(accessoryMapper.toAccessoryDto(any(Accessory.class))).thenReturn(accessoryDto);

	    // Mise à jour de l'accessoire (transformer l'AccessoryDto en Accessory)
	    when(accessoryMapper.toAccessoryEntity(any(AccessoryDto.class))).thenReturn(accessoire);
	    
	    // Appel de la méthode à tester
	    AccessoryDto accessoireUpdated = accessoryService.updateAccessory(1L, accessoryDto);
	    
	    // Vérifications
	    assertNotNull(accessoireUpdated);
	    assertEquals("Caméra de recul", accessoireUpdated.name());
	    assertEquals(2300, accessoireUpdated.price());

	    // Vérification que la méthode save a bien été appelée avec l'entité mise à jour
	    verify(accessoryRepository, times(1)).save(accessoire); 
	    verify(accessoryRepository, times(1)).findById(1L); // Vérifier que findById a bien été appelé
	}

	 @Test
	    void testDeleteAccessorie() {
		// Création de l'accessoire
	        Accessory accessoire = createTestAccessory();
	        // Simuler la récupération de l'accesoire par ID
	        when(accessoryRepository.findById(1L)).thenReturn(java.util.Optional.of(accessoire)); 
	     // Simuler la suppression de l'accessoire
	        doNothing().when(accessoryRepository).delete(accessoire);
	     // appel de la méthode à tester
	        accessoryService.deleteAccessory(1L);
	        // Vérifier que la méthode delete() a bien été appelée une fois
	        verify(accessoryRepository, times(1)).deleteById(1L);
	 }
	 @Test
	 void testGetAccessoriesByVehicle() {
	     // Création des accessoires
	     Accessory accessory1 = createTestAccessory();
	     Accessory accessory2 = Accessory.builder()
	             .name("GPS")
	             .description("Système de navigation intégré")
	             .price(1500)
	             .type("Electonique")
	             .build();
	     Accessory accessory3 = Accessory.builder()
	             .name("Ecran")
	             .description("Ecran tactil")
	             .price(1500)
	             .type("Technologie")
	             .build();
	     
	     AccessoryDto accessoryDto1 = createTestAccessoryDto();
	     AccessoryDto accessoryDto2 = AccessoryDto.builder()
	             .name("GPS")
	             .description("Système de navigation intégré")
	             .price(1500)
	             .type("Electonique")
	             .build();
	     AccessoryDto accessoryDto3 = AccessoryDto.builder()
	             .name("Ecran")
	             .description("Ecran tactil")
	             .price(1500)
	             .type("Technologie")
	             .build();
	     
	     // Simuler les mocks des mappers
	     when(accessoryMapper.toAccessoryDto(accessory1)).thenReturn(accessoryDto1);
	     when(accessoryMapper.toAccessoryDto(accessory2)).thenReturn(accessoryDto2);
	     when(accessoryMapper.toAccessoryDto(accessory3)).thenReturn(accessoryDto3);

	     // Création d'une liste d'accessoires qui sera ajoutée au véhicule
	     List<Accessory> accessories = Arrays.asList(accessory1, accessory2);


	     // Création du véhicule et association des accessoires à ce véhicule
	     Vehicle vehicle1 = createTestVehicle();
	     vehicle1.setAccessories(accessories);

	     // Simulation de la récupération du véhicule par ID depuis le repository
	     when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicle1));

	     // Appel à la méthode à tester
	     List<AccessoryDto> accessoriesResult = accessoryService.getAccessoriesByVehicule(1L);

	     // Vérifications des résultats
	     assertNotNull(accessoriesResult);
	     assertEquals(2, accessoriesResult.size());
	     assertTrue(accessoriesResult.contains(accessoryDto1));
	     assertTrue(accessoriesResult.contains(accessoryDto2));
	     assertFalse(accessoriesResult.contains(accessoryDto3));

	     // Vérification que findById a bien été appelé
	     verify(vehicleRepository, times(1)).findById(1L);
	 }

	

}
