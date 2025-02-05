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
import com.renault.repositories.AccessoireRepository;
import com.renault.repositories.VehiculeRepository;

import jakarta.transaction.Transactional;


public class AccessoireServiceTest {

	@Mock
	private AccessoireRepository accessoireRepository;
	
	@Mock
    private VehiculeRepository vehiculeRepository;
	
	@InjectMocks	
	private AccessoireServiceImpl accessoireService;
	
	@BeforeEach
	void step() {
		MockitoAnnotations.openMocks(this);
	}
	
	private Accessory createTestAccessoire() {
		return Accessory.builder()
				.id(1L)
    			.name("Caméra")
    			.description("Caméra de recul")
    			.price(2000)
    			.type("Sécurité")
    			.build();
		
	}
	private Vehicle createTestVehciule() {
		return Vehicle.builder()
				.id(1L)
    			.brand("Renault")
    			.anneeFabrication(2021)
    			.model("Captur")
    			.typeCarburant(TypeCarburant.ESSENCE)
    			.accessoires(new ArrayList<>())
    			.build();
		
	}
	
	@Test
	void testAddAccessoireToVehicule() {
	    // Création des objets de test
	    Accessory accessoire = createTestAccessoire();
	    Vehicle vehicule = createTestVehciule();
	    
	    // Vérifier que la liste est bien initialisée
	    assertNotNull(vehicule.getAccessoires());

	    // Mock des repositories
	    when(vehiculeRepository.findById(vehicule.getId())).thenReturn(Optional.of(vehicule));
	    when(accessoireRepository.save(any(Accessory.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    when(vehiculeRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    // Appel à la méthode à tester
	    Accessory accessoireResult = accessoireService.addAccessoireToVehicule(vehicule.getId(), accessoire);

	    // Vérifications
	    assertNotNull(accessoireResult);
	    assertEquals("Caméra", accessoireResult.getName());
	    assertEquals(2000, accessoireResult.getPrice());
	    assertNotNull(vehicule.getAccessoires());
	    assertEquals(1, vehicule.getAccessoires().size());
	    assertTrue(vehicule.getAccessoires().contains(accessoireResult));

	    // Vérifications des appels aux mocks

	    verify(vehiculeRepository, times(1)).findById(vehicule.getId());
	    verify(accessoireRepository, times(1)).save(accessoire);
	}
	
	@Test
	void testUpdateAccessoire() {
		// création de l'objet accessoire
		Accessory accessoire = createTestAccessoire();
		 // Simuler la récupération de l('accessoire par ID
		
		when(accessoireRepository.findById(1L)).thenReturn(java.util.Optional.of(accessoire));
		accessoire.setName("Caméra de recul");
		accessoire.setPrice(2300);
		
		// appel de la méthode à tester
		Accessory accessoireUpdated = accessoireService.updateAccessoire(1L, accessoire);
		 // Vérification du garage
		assertNotNull(accessoireUpdated);
		assertEquals("Caméra de recul", accessoireUpdated.getName());
		assertEquals(2300, accessoireUpdated.getPrice());
		
		verify(accessoireRepository, times(1)).save(accessoireUpdated); // Vérifier que save a bien été appelé
		verify(accessoireRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
	}
	 @Test
	    void testDeleteAccesoire() {
		// Création de l'accessoire
	        Accessory accessoire = createTestAccessoire();
	        // Simuler la récupération de l'accesoire par ID
	        when(accessoireRepository.findById(1L)).thenReturn(java.util.Optional.of(accessoire)); 
	     // Simuler la suppression de l'accessoire
	        doNothing().when(accessoireRepository).delete(accessoire);
	     // appel de la méthode à tester
	        accessoireService.deleteAccesoire(1L);
	        // Vérifier que la méthode delete() a bien été appelée une fois
	        verify(accessoireRepository, times(1)).deleteById(1L);
	 }
	 
	 @Test
	 void testGetAccessoiresByVehicule() {
		   // Création des accessoires
		 Accessory accessoire1 = createTestAccessoire();
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
     // Création d'une liste d'accessoires qui sera ajoutée au véhicule
		 List<Accessory> accessoires = Arrays.asList(accessoire1,accessoire2);

		  // Création du véhicule et association des accessoires à ce véhicule
		 Vehicle vehicule1 = createTestVehciule();
		 vehicule1.setAccessoires(accessoires);
		 
		 // Simulation de la récupération du véhicule par ID depuis le repository
		 when(vehiculeRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicule1));
		 
		 // appel à la méthode à tester
		 List<Accessory> accessoiresResult = accessoireService.getAccessoiresByVehicule(1L);
		 // Vérifications des résultats
 		assertNotNull(accessoiresResult);
 		assertEquals(2, accessoiresResult.size());
 		assertTrue(accessoiresResult.contains(accessoire1));
 		assertTrue(accessoiresResult.contains(accessoire2));
 		assertFalse(accessoiresResult.contains(accessoire3));
 		
		verify(vehiculeRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
	 }

}
