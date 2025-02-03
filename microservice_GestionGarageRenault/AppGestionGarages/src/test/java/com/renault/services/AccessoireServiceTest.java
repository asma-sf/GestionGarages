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

import com.renault.entities.Accessoire;
import com.renault.entities.Vehicule;
import com.renault.enums.TypeVehicule;
import com.renault.repositories.AccessoireRepository;
import com.renault.repositories.VehiculeRepository;

import jakarta.transaction.Transactional;

@Transactional
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
	
	private Accessoire createTestAccessoire() {
		Accessoire accessoire = Accessoire.builder()
				.id(1L)
    			.nom("Caméra")
    			.description("Caméra de recul")
    			.prix(2000)
    			.type("Sécurité")
    			.build();
		return accessoire;
	}
	private Vehicule createTestVehciule() {
		Vehicule vehicule =Vehicule.builder()
				.id(1L)
    			.brand("Renault")
    			.anneeFabrication(2021)
    			.model("Captur")
    			.typeCarburant("Diesel")
    			.typeVehicule(TypeVehicule.SUV)
    			.accessoires(new ArrayList<>())
    			.build();
		return vehicule;
	}
	
	@Test
	void testAddAccessoireToVehicule() {
	    // Création des objets de test
	    Accessoire accessoire = createTestAccessoire();
	    Vehicule vehicule = createTestVehciule();
	    
	    // Vérifier que la liste est bien initialisée
	    assertNotNull(vehicule.getAccessoires());

	    // Mock des repositories
	    when(vehiculeRepository.findById(vehicule.getId())).thenReturn(Optional.of(vehicule));
	    when(accessoireRepository.save(any(Accessoire.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    when(vehiculeRepository.save(any(Vehicule.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    // Appel à la méthode à tester
	    Accessoire accessoireResult = accessoireService.addAccessoireToVehicule(vehicule.getId(), accessoire);

	    // Vérifications
	    assertNotNull(accessoireResult);
	    assertEquals("Caméra", accessoireResult.getNom());
	    assertEquals(2000, accessoireResult.getPrix());
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
		Accessoire accessoire = createTestAccessoire();
		 // Simuler la récupération de l('accessoire par ID
		
		when(accessoireRepository.findById(1L)).thenReturn(java.util.Optional.of(accessoire));
		accessoire.setNom("Caméra de recul");
		accessoire.setPrix(2300);
		
		// appel de la méthode à tester
		Accessoire accessoireUpdated = accessoireService.updateAccessoire(1L, accessoire);
		 // Vérification du garage
		assertNotNull(accessoireUpdated);
		assertEquals("Caméra de recul", accessoireUpdated.getNom());
		assertEquals(2300, accessoireUpdated.getPrix());
		
		verify(accessoireRepository, times(1)).save(accessoireUpdated); // Vérifier que save a bien été appelé
		verify(accessoireRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
	}
	 @Test
	    void testDeleteAccesoire() {
		// Création de l'accessoire
	        Accessoire accessoire = createTestAccessoire();
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
		 Accessoire accessoire1 = createTestAccessoire();
		 Accessoire accessoire2 = Accessoire.builder()
					.id(1L)
	    			.nom("GPS")
	    			.description("Système de navigation intégré")
	    			.prix(1500)
	    			.type("Electonique")
	    			.build();
     	Accessoire accessoire3 = Accessoire.builder()
    			.nom("Ecran")
    			.description("Ecran tactil")
    			.prix(1500)
    			.type("Technologie")
    			.build();
     // Création d'une liste d'accessoires qui sera ajoutée au véhicule
		 List<Accessoire> accessoires = Arrays.asList(accessoire1,accessoire2);

		  // Création du véhicule et association des accessoires à ce véhicule
		 Vehicule vehicule1 = createTestVehciule();
		 vehicule1.setAccessoires(accessoires);
		 
		 // Simulation de la récupération du véhicule par ID depuis le repository
		 when(vehiculeRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicule1));
		 
		 // appel à la méthode à tester
		 List<Accessoire> accessoiresResult = accessoireService.getAccessoiresByVehicule(1L);
		 // Vérifications des résultats
 		assertNotNull(accessoiresResult);
 		assertEquals(2, accessoiresResult.size());
 		assertTrue(accessoiresResult.contains(accessoire1));
 		assertTrue(accessoiresResult.contains(accessoire2));
 		assertFalse(accessoiresResult.contains(accessoire3));
 		
		verify(vehiculeRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
	 }

}
