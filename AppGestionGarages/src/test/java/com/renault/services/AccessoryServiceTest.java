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

import com.renault.entities.Accessory;
import com.renault.entities.Vehicle;
import com.renault.enums.TypeCarburant;
import com.renault.repositories.AccessoryRepository;
import com.renault.repositories.VehicleRepository;

import jakarta.transaction.Transactional;


public class AccessoryServiceTest {

	@Mock
	private AccessoryRepository accessoryRepository;
	
	@Mock
    private VehicleRepository vehicleRepository;
	
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
	
	@Test
	void testAddAccessorieToVehicule() {
	    // Création des objets de test
	    Accessory accessory = createTestAccessory();
	    Vehicle vehicule = createTestVehicle();
	    
	    // Vérifier que la liste est bien initialisée
	    assertNotNull(vehicule.getAccessories());

	    // Mock des repositories
	    when(vehicleRepository.findById(vehicule.getId())).thenReturn(Optional.of(vehicule));
	    when(accessoryRepository.save(any(Accessory.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    // Appel à la méthode à tester
	    Accessory accessoireResult = accessoryService.addAccessoryToVehicle(vehicule.getId(), accessory);

	    // Vérifications
	    assertNotNull(accessoireResult);
	    assertEquals("Caméra", accessoireResult.getName());
	    assertEquals(2000, accessoireResult.getPrice());
	    assertNotNull(vehicule.getAccessories());
	    assertEquals(1, vehicule.getAccessories().size());
	    assertTrue(vehicule.getAccessories().contains(accessoireResult));

	    // Vérifications des appels aux mocks

	    verify(vehicleRepository, times(1)).findById(vehicule.getId());
	    verify(accessoryRepository, times(1)).save(accessory);
	}
	
	@Test
	void testUpdateAccessorie() {
		// création de l'objet accessoire
		Accessory accessoire = createTestAccessory();
		 // Simuler la récupération de l('accessoire par ID
		
		when(accessoryRepository.findById(1L)).thenReturn(java.util.Optional.of(accessoire));
		accessoire.setName("Caméra de recul");
		accessoire.setPrice(2300);
		
		// appel de la méthode à tester
		Accessory accessoireUpdated = accessoryService.updateAccessory(1L, accessoire);
		 // Vérification du garage
		assertNotNull(accessoireUpdated);
		assertEquals("Caméra de recul", accessoireUpdated.getName());
		assertEquals(2300, accessoireUpdated.getPrice());
		
		verify(accessoryRepository, times(1)).save(accessoireUpdated); // Vérifier que save a bien été appelé
		verify(accessoryRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
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
	 void testGetaccessoriesByVehicule() {
		   // Création des accessories
		 Accessory accessoire1 = createTestAccessory();
		 Accessory accessoire2 = Accessory.builder()
					.id(1L)
	    			.name("GPS")
	    			.description("Système de navigation intégré")
	    			.price(1500)
	    			.type("Electonique")
	    			.build();
     	Accessory accessoire3 = Accessory.builder()
    			.name("Ecran")
    			.description("Ecran tactil")
    			.price(1500)
    			.type("Technologie")
    			.build();
     // Création d'une liste d'accessories qui sera ajoutée au véhicule
		 List<Accessory> accessories = Arrays.asList(accessoire1,accessoire2);

		  // Création du véhicule et association des accessories à ce véhicule
		 Vehicle vehicule1 = createTestVehicle();
		 vehicule1.setAccessories(accessories);
		 
		 // Simulation de la récupération du véhicule par ID depuis le repository
		 when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicule1));
		 
		 // appel à la méthode à tester
		 List<Accessory> accessoriesResult = accessoryService.getAccessoriesByVehicule(1L);
		 // Vérifications des résultats
 		assertNotNull(accessoriesResult);
 		assertEquals(2, accessoriesResult.size());
 		assertTrue(accessoriesResult.contains(accessoire1));
 		assertTrue(accessoriesResult.contains(accessoire2));
 		assertFalse(accessoriesResult.contains(accessoire3));
 		
		verify(vehicleRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
	 }

}
