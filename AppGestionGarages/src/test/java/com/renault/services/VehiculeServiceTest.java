package com.renault.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;
import com.renault.entities.Vehicule;
import com.renault.enums.TypeCarburant;
import com.renault.exceptions.GarageLimitVehiculeException;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehiculeRepository;

import jakarta.transaction.Transactional;

public class VehiculeServiceTest {

	@Mock
	private VehiculeRepository vehiculeRepository;

	@Mock
	private GarageRepository garageRepository;

	@InjectMocks
	private VehiculeServiceImpl vehiculeService;

	private Garage testGarageMaxVehicule;
	
	private static final int MAX_VEHICULES = 50;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		// Créer un garage avec 50 véhicules
		testGarageMaxVehicule = Garage.builder().id(1L).build();
		
        List<Vehicule> vehicules = new ArrayList<>();
        for (int i = 0; i < MAX_VEHICULES; i++) {
            Vehicule v =Vehicule.builder().id((long) i).build();
            vehicules.add(v);
        }
        testGarageMaxVehicule.setVehicules(vehicules);
	}
	private Vehicule createTestVehicule() {
		Vehicule vehicule =Vehicule.builder()
				.id(1L)
    			.brand("Renault")
    			.anneeFabrication(2021)
    			.model("Captur")
    			.typeCarburant(TypeCarburant.ESSENCE)
    			.accessoires(new ArrayList<>())
    			.build();
		return vehicule;
	}
	private Garage createTestGarage() {
        return Garage.builder()
                .name("garage Casablanca")
                .address("Casablanca")
                .email("gar-casa@renault.com")
                .telephone("0566778899")
                .vehicules(new ArrayList<>())
               /* .garageOpeningTimes(Arrays.asList(
                    GarageOpeningTime.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTimes(Arrays.asList(
                            OpeningTime.builder().startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(12, 0)).build(),
                            OpeningTime.builder().startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(18, 0)).build()))
                        .build()))*/
                .build();
    }
	@Test
	void testAddVehiculeToGarage() {
		 
		 // Création des objets de test
	    Garage garage = createTestGarage();
	    Vehicule vehicule = createTestVehicule();
	 // Vérifier que la liste est bien initialisée
	    assertNotNull(garage.getVehicules());

	    // Mock des repositories
	    when(garageRepository.findById(garage.getId())).thenReturn(Optional.of(garage));
	    when(garageRepository.save(any(Garage.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    when(vehiculeRepository.save(any(Vehicule.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    // Appel à la méthode à tester
	    Vehicule vehiculeResult = vehiculeService.addVehiculeToGarage(garage.getId(), vehicule);

	    // Vérifications
	    assertNotNull(vehiculeResult);
	    assertEquals("Renault", vehiculeResult.getBrand());
	    assertEquals("Captur", vehiculeResult.getModel());
	    assertNotNull(garage.getVehicules());
	    assertEquals(1, garage.getVehicules().size());
	    assertTrue(garage.getVehicules().contains(vehiculeResult));

	    // Vérifications des appels aux mocks

	    verify(garageRepository, times(1)).findById(garage.getId());
	    verify(vehiculeRepository, times(1)).save(vehicule);
	}
	@Test
    void shouldThrowExceptionWhenGarageIsFull() {
        // Simuler la récupération du garage avec 50 véhicules
        when(garageRepository.findById(1L)).thenReturn(Optional.of(testGarageMaxVehicule));

        // Vérifier que l'exception est levée
        Vehicule newVehicule = new Vehicule();
        Exception exception = assertThrows(GarageLimitVehiculeException.class, () -> {
        	vehiculeService.addVehiculeToGarage(1L, newVehicule);
        });

        assertEquals("Le garage ne peut pas depasser "+MAX_VEHICULES+" vehicules", exception.getMessage());
        
        // Vérifier que `vehiculeRepository.save()` n'est jamais appelé
        verify(vehiculeRepository, never()).save(any());
    }
	
	@Test
	void testUpdateVehicule() {
		// création de l'objet vehicule
		Vehicule vehicule = createTestVehicule();
		 // Simuler la récupération du vehciule par ID	
		when(vehiculeRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicule));
		// modifier les données
		vehicule.setBrand("BM");
		vehicule.setTypeCarburant(TypeCarburant.HYBRID);
		

		// appel de la méthode à tester
		Vehicule vehiculeUpdated = vehiculeService.updateVehicule(1L, vehicule);
		 // Vérification du vehicule
		assertNotNull(vehiculeUpdated);
		assertEquals("HYBRID", vehiculeUpdated.getTypeCarburant().toString());
		assertEquals("BM", vehiculeUpdated.getBrand());
		
		verify(vehiculeRepository, times(1)).save(vehiculeUpdated); // Vérifier que save a bien été appelé
		verify(vehiculeRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
	}
	@Test
    void testDeleteVehicule() {
		// création de l'objet vehicule
		Vehicule vehicule = createTestVehicule();
		// Simuler la récupération du vehciule par ID		
		when(vehiculeRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicule));
     // Simuler la suppression du vehicule
        doNothing().when(vehiculeRepository).delete(vehicule);
     // appel de la méthode à tester
        vehiculeService.deleteVehicule(1L);
        // Vérifier que la méthode delete() a bien été appelée une fois
        verify(vehiculeRepository, times(1)).deleteById(1L);
 }
	@Test
	void testGetVehiculeByGarage() {
		// création des objets véhicules
		Vehicule vehicule1= createTestVehicule();
		Vehicule vehicule2 =Vehicule.builder()
    			.brand("Renault")
    			.anneeFabrication(2022)
    			.model("CLIO")
    			.typeCarburant(TypeCarburant.GASOIL)
    			.build();
		Vehicule vehicule3 =Vehicule.builder()
    			.brand("Renault")
    			.anneeFabrication(2022)
    			.model("Megan")
    			.typeCarburant(TypeCarburant.GASOIL)
    			.build();	
    	
		  // Création d'une liste de vehicules qui sera ajoutée aux garages
    	List<Vehicule> vehicules1 = Arrays.asList(vehicule1,vehicule2,vehicule3);
    	List<Vehicule> vehicules2 = Arrays.asList(vehicule1,vehicule2);
    	
    	// Création du garage et association des vehicules à ce garage
    	Garage garage = createTestGarage();
    	garage.setVehicules(vehicules2);
    	
    	 // Simulation de la récupération du garage par ID depuis le repository
    	when(garageRepository.findById(1L)).thenReturn(java.util.Optional.of(garage));
    	 // appel à la méthode à tester
		 List<Vehicule> vehiculesResult = vehiculeService.getVehiculeByGarage(1L);
		 // Vérifications des résultats
		 assertNotNull(vehiculesResult);
		 assertEquals(2, vehiculesResult.size());
		 assertTrue(vehiculesResult.contains(vehicule1));
		 assertTrue(vehiculesResult.contains(vehicule2));
		 assertFalse(vehiculesResult.contains(vehicule3));
		 
		 verify(garageRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
	}
	@Test
	void testGetVehiculeByModel() {
		// création des objets véhicules
				Vehicule vehicule1= createTestVehicule();
				Vehicule vehicule2 =Vehicule.builder()
		    			.brand("Renault")
		    			.anneeFabrication(2022)
		    			.model("CLIO")
		    			.typeCarburant(TypeCarburant.GASOIL)
		    			.build();
				Vehicule vehicule3 =Vehicule.builder()
		    			.brand("Renault")
		    			.anneeFabrication(2020)
		    			.model("CLIO")
		    			.typeCarburant(TypeCarburant.GASOIL)
		    			.build();
				List<Vehicule> vehiculesExpected = Arrays.asList(vehicule2,vehicule3);
				
				// Simuler la récupération des véhicules par model depuis le repository
			    when(vehiculeRepository.findByModel("CLIO")).thenReturn(vehiculesExpected);
			    
		    // appel à la méthode à tester
			List<Vehicule> vehiculesResult = vehiculeService.getVehiculeByModel("CLIO");
			 // Vérifications des résultats
			 assertNotNull(vehiculesResult);
			 assertEquals(2, vehiculesResult.size());
			 assertEquals(vehiculesExpected, vehiculesResult);
			 assertTrue(vehiculesResult.contains(vehicule2));
			 assertTrue(vehiculesResult.contains(vehicule3));
			 assertFalse(vehiculesResult.contains(vehicule1));
	}
}
