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
import com.renault.entities.Vehicle;
import com.renault.enums.TypeCarburant;
import com.renault.exceptions.GarageLimitVehiculeException;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehicleRepository;

import jakarta.transaction.Transactional;

public class VehicleServiceTest {

	@Mock
	private VehicleRepository vehicleRepository;

	@Mock
	private GarageRepository garageRepository;

	@InjectMocks
	private VehicleServiceImpl vehicleService;

	private Garage garageWithMaxVehicles;
	
	private static final int MAX_VEHICLES = 50;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		// Créer un garage avec 50 véhicules
		garageWithMaxVehicles = Garage.builder().id(1L).build();
		
        List<Vehicle> vehicules = new ArrayList<>();
        for (int i = 0; i < MAX_VEHICLES; i++) {
            Vehicle v =Vehicle.builder().id((long) i).build();
            vehicules.add(v);
        }
        garageWithMaxVehicles.setVehicles(vehicules);
	}
	private Vehicle createTestVehicule() {
		return Vehicle.builder()
				.id(1L)
    			.brand("Renault")
    			.manufacturingYear(2021)
    			.model("Captur")
    			.typeCarburant(TypeCarburant.ESSENCE)
    			.accessories((new ArrayList<>()))
    			.build();
		 
	}
	private Garage createTestGarage() {
        return Garage.builder()
                .name("garage Casablanca")
                .address("Casablanca")
                .email("gar-casa@renault.com")
                .telephone("0566778899")
                .vehicles(new ArrayList<>())
                .build();
    }
	@Test
	void testAddVehiculeToGarage() {
		 
		 // Création des objets de test
	    Garage garage = createTestGarage();
	    Vehicle vehicule = createTestVehicule();
	 // Vérifier que la liste est bien initialisée
	    assertNotNull(garage.getVehicles());

	    // Mock des repositories
	    when(garageRepository.findById(garage.getId())).thenReturn(Optional.of(garage));
	    when(garageRepository.save(any(Garage.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    // Appel à la méthode à tester
	    Vehicle vehiculeResult = vehicleService.addVehicleToGarage(garage.getId(), vehicule);

	    // Vérifications
	    assertNotNull(vehiculeResult);
	    assertEquals("Renault", vehiculeResult.getBrand());
	    assertEquals("Captur", vehiculeResult.getModel());
	    assertNotNull(garage.getVehicles());
	    assertEquals(1, garage.getVehicles().size());
	    assertTrue(garage.getVehicles().contains(vehiculeResult));

	    // Vérifications des appels aux mocks

	    verify(garageRepository, times(1)).findById(garage.getId());
	    verify(vehicleRepository, times(1)).save(vehicule);
	}
	@Test
    void shouldThrowExceptionWhenGarageIsFull() {
        // Simuler la récupération du garage avec 50 véhicules
        when(garageRepository.findById(1L)).thenReturn(Optional.of(garageWithMaxVehicles));

        // Vérifier que l'exception est levée
        Vehicle newVehicule = new Vehicle();
        Exception exception = assertThrows(GarageLimitVehiculeException.class, () -> {
        	vehicleService.addVehicleToGarage(1L, newVehicule);
        });

        assertEquals("Le garage ne peut pas depasser "+MAX_VEHICLES+" vehicules", exception.getMessage());
        
        // Vérifier que `vehiculeRepository.save()` n'est jamais appelé
        verify(vehicleRepository, never()).save(any());
    }
	
	@Test
	void testUpdateVehicule() {
		// création de l'objet vehicule
		Vehicle vehicule = createTestVehicule();
		 // Simuler la récupération du vehciule par ID	
		when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicule));
		// modifier les données
		vehicule.setBrand("BM");
		vehicule.setTypeCarburant(TypeCarburant.HYBRID);
		

		// appel de la méthode à tester
		Vehicle vehiculeUpdated = vehicleService.updateVehicle(1L, vehicule);
		 // Vérification du vehicule
		assertNotNull(vehiculeUpdated);
		assertEquals("HYBRID", vehiculeUpdated.getTypeCarburant().toString());
		assertEquals("BM", vehiculeUpdated.getBrand());
		
		verify(vehicleRepository, times(1)).save(vehiculeUpdated); // Vérifier que save a bien été appelé
		verify(vehicleRepository, times(1)).findById(1L); // Vérifier que findBy a bien été appelé
	}
	@Test
    void testDeleteVehicule() {
		// création de l'objet vehicule
		Vehicle vehicule = createTestVehicule();
		// Simuler la récupération du vehciule par ID		
		when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicule));
     // Simuler la suppression du vehicule
        doNothing().when(vehicleRepository).delete(vehicule);
     // appel de la méthode à tester
        vehicleService.deleteVehicle(1L);
        // Vérifier que la méthode delete() a bien été appelée une fois
        verify(vehicleRepository, times(1)).deleteById(1L);
 }
	@Test
	void testGetVehiculeByGarage() {
		// création des objets véhicules
		Vehicle vehicule1= createTestVehicule();
		Vehicle vehicule2 =Vehicle.builder()
    			.brand("Renault")
    			.manufacturingYear(2022)
    			.model("CLIO")
    			.typeCarburant(TypeCarburant.GASOIL)
    			.build();
		Vehicle vehicule3 =Vehicle.builder()
    			.brand("Renault")
    			.manufacturingYear(2022)
    			.model("Megan")
    			.typeCarburant(TypeCarburant.GASOIL)
    			.build();	
    	
		  // Création d'une liste de vehicules qui sera ajoutée aux garages
    	List<Vehicle> vehicules1 = Arrays.asList(vehicule1,vehicule2,vehicule3);
    	List<Vehicle> vehicules2 = Arrays.asList(vehicule1,vehicule2);
    	
    	// Création du garage et association des vehicules à ce garage
    	Garage garage = createTestGarage();
    	garage.setVehicles(vehicules2);
    	
    	 // Simulation de la récupération du garage par ID depuis le repository
    	when(garageRepository.findById(1L)).thenReturn(java.util.Optional.of(garage));
    	 // appel à la méthode à tester
		 List<Vehicle> vehiculesResult = vehicleService.getVehicleByGarage(1L);
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
				Vehicle vehicule1= createTestVehicule();
				Vehicle vehicule2 =Vehicle.builder()
		    			.brand("Renault")
		    			.manufacturingYear(2022)
		    			.model("CLIO")
		    			.typeCarburant(TypeCarburant.GASOIL)
		    			.build();
				Vehicle vehicule3 =Vehicle.builder()
		    			.brand("Renault")
		    			.manufacturingYear(2020)
		    			.model("CLIO")
		    			.typeCarburant(TypeCarburant.GASOIL)
		    			.build();
				List<Vehicle> vehiculesExpected = Arrays.asList(vehicule2,vehicule3);
				
				// Simuler la récupération des véhicules par model depuis le repository
			    when(vehicleRepository.findByModel("CLIO")).thenReturn(vehiculesExpected);
			    
		    // appel à la méthode à tester
			List<Vehicle> vehiculesResult = vehicleService.getVehicleByModel("CLIO");
			 // Vérifications des résultats
			 assertNotNull(vehiculesResult);
			 assertEquals(2, vehiculesResult.size());
			 assertEquals(vehiculesExpected, vehiculesResult);
			 assertTrue(vehiculesResult.contains(vehicule2));
			 assertTrue(vehiculesResult.contains(vehicule3));
			 assertFalse(vehiculesResult.contains(vehicule1));
	}
}
