
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

import com.renault.dtos.GarageDto;
import com.renault.dtos.VehicleDto;
import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;
import com.renault.entities.Vehicle;
import com.renault.enums.TypeCarburant;
import com.renault.exceptions.GarageLimitVehiculeException;
import com.renault.mappers.GarageMapper;
import com.renault.mappers.VehicleMapper;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehicleRepository;

import jakarta.transaction.Transactional;
import javafx.beans.binding.When;

public class VehicleServiceTest {

	@Mock
	private VehicleRepository vehicleRepository;

	@Mock
	private GarageRepository garageRepository;

	@Mock
	private VehicleMapper vehicleMapper;

	@Mock
	private GarageMapper garageMapper;

	@InjectMocks
	private VehicleServiceImpl vehicleService;

	private Garage garageWithMaxVehicles;

	private static final int MAX_VEHICLES = 50;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Créer un garage avec 50 véhicules
		garageWithMaxVehicles = Garage.builder().id(1L).build();

		List<Vehicle> vehicules = new ArrayList<>();
		for (int i = 0; i < MAX_VEHICLES; i++) {
			Vehicle v = Vehicle.builder().id((long) i).build();
			vehicules.add(v);
		}
		garageWithMaxVehicles.setVehicles(vehicules);
	}

	private Vehicle createTestVehicule() {
		return Vehicle.builder().id(1L).brand("Renault").manufacturingYear(2021).model("Captur")
				.typeCarburant(TypeCarburant.ESSENCE).accessories((new ArrayList<>())).build();

	}

	private VehicleDto createTestVehiculeDto() {
		return VehicleDto.builder().id(1L).brand("Renault").manufacturingYear(2021).model("Captur")
				.typeCarburant(TypeCarburant.ESSENCE).accessories((new ArrayList<>())).build();

	}

	private Garage createTestGarage() {
		return Garage.builder().name("garage Casablanca").address("Casablanca").email("gar-casa@renault.com")
				.phone("0566778899").vehicles(new ArrayList<>()).build();
	}

	private GarageDto createTestGaragedDto() {
		return GarageDto.builder().name("garage Casablanca").address("Casablanca").email("gar-casa@renault.com")
				.phone("0566778899").vehicles(new ArrayList<>()).build();
	}

	@Test
	void testAddVehicleToGarage() {

		// Création des objets de test
		Garage garage = createTestGarage();
		GarageDto garageDto = createTestGaragedDto();
		Vehicle vehicle = createTestVehicule(); // Vérifier que la liste est bien initialisée
		VehicleDto vehicleDto = createTestVehiculeDto();

		// Mocke des mappers dto
		when(vehicleMapper.toVehicleDto(any(Vehicle.class))).thenReturn(vehicleDto);
		when(garageMapper.toGarageDto(any(Garage.class))).thenReturn(garageDto);

		// Vérifier que la liste est bien initialisée
		assertNotNull(garage.getVehicles());
		assertNotNull(garageDto.vehicles());
		assertTrue(garage.getVehicles().isEmpty());

		// Mock des repositories
		when(garageRepository.findById(garage.getId())).thenReturn(Optional.of(garage));
		when(garageRepository.save(any(Garage.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Mocke des vehciule mapper entity
		when(vehicleMapper.toVehicleEntity(any(VehicleDto.class))).thenReturn(vehicle);

		// Mise à jour de l'objet garageDto après ajout de l'accessoire
		GarageDto garageDtoUpdated = GarageDto.builder().id(1L).name("garage Casablanca").address("Casablanca")
				.email("gar-casa@renault.com").phone("0566778899").vehicles(Arrays.asList(vehicleDto)).build();

		// Mocke des garage mapper
		when(garageMapper.toGarageDto(any(Garage.class))).thenReturn(garageDtoUpdated);
		// Appel à la méthode à tester Vehicle vehiculeResult =
		VehicleDto vehiculeResultDto = vehicleService.addVehicleToGarage(garage.getId(), vehicleDto);

		// Vérifications assertNotNull(vehiculeResult);
		assertEquals("Renault", vehiculeResultDto.brand());
		assertEquals("Captur", vehiculeResultDto.model());
		assertNotNull(garage.getVehicles());
		assertEquals(1, garage.getVehicles().size());
		assertTrue(garage.getVehicles().contains(vehicle));
		assertNotNull(garageDtoUpdated.vehicles());
		assertEquals(1, garageDtoUpdated.vehicles().size());
		assertTrue(garageDtoUpdated.vehicles().contains(vehicleDto));

		// Vérifications des appels aux mocks
		verify(garageRepository, times(1)).findById(garage.getId());
		verify(vehicleRepository, times(1)).save(vehicle);

	}

	@Test void shouldThrowExceptionWhenGarageIsFull() { 
		
		// Simuler la récupération du garage avec 50 véhicules
		  
		when(garageRepository.findById(1L)).thenReturn(Optional.of(garageWithMaxVehicles));
  
  // Vérifier que l'exception est levée 
		  VehicleDto newVehicleDto =VehicleDto.builder()
				  .id(1L)
				  .brand("Renault")
				  .manufacturingYear(2021)
				  .model("CLIO")
				   .typeCarburant(TypeCarburant.GASOIL)
				   .accessories((new ArrayList<>())).build();
  Exception exception = assertThrows(GarageLimitVehiculeException.class, () ->
  { 
	  vehicleService.addVehicleToGarage(1L, newVehicleDto); 
  });
  
  assertEquals("Le garage ne peut pas depasser "+MAX_VEHICLES+" vehicules",exception.getMessage());
  
  // Vérifier que `vehiculeRepository.save()` n'est jamais appelé
  verify(vehicleRepository, never()).save(any()); 
  }
	
	@Test void testUpdateVehicle() {
		// création de l'objet vehicule 
		Vehicle vehicle = createTestVehicule(); // Simuler la récupération du vehciule par ID 
		  when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicle)); 
		// Création de vehiculeDTO avec les nouvelles données
		  VehicleDto vehicleDto =VehicleDto.builder()
				  .id(1L)
				  .brand("Renault")
				  .manufacturingYear(2021)
				  .model("CLIO")
				   .typeCarburant(TypeCarburant.GASOIL)
				   .accessories((new ArrayList<>())).build();
		  
		  // Mock des mappers
		  when(vehicleMapper.toVehicleDto(any(Vehicle.class))).thenReturn(vehicleDto);
		// Mise à jour de vehicule (transformer vehiculeDto en vehicule)
		  when(vehicleMapper.toVehicleEntity(any(VehicleDto.class))).thenReturn(vehicle);
		  // appel de la méthode à tester 
		  VehicleDto vehiculeDtoUpdated =vehicleService.updateVehicle(1L, vehicleDto); 
		  // Vérification du vehicule
		  assertNotNull(vehiculeDtoUpdated); 
		  assertEquals("GASOIL",vehiculeDtoUpdated.typeCarburant().toString());
		  assertEquals("CLIO",vehiculeDtoUpdated.model());
		  
		  verify(vehicleRepository, times(1)).save(vehicle); // Vérifier que save a bien été appelé 
		  verify(vehicleRepository, times(1)).findById(1L); //Vérifier que findBy a bien été appelé 
		  }


	@Test
	void testDeleteVehicle() {
		// création de l'objet vehicule
		Vehicle vehicule = createTestVehicule(); // Simuler la récupération du vehciule parID

		when(vehicleRepository.findById(1L)).thenReturn(java.util.Optional.of(vehicule)); // Simuler la suppression du
																							// vehicule

		doNothing().when(vehicleRepository).delete(vehicule);
		// appel de la méthode à tester
		vehicleService.deleteVehicle(1L);
		// Vérifier que la méthode delete() a bien été appelée une fois
		verify(vehicleRepository, times(1)).deleteById(1L);
	}
	@Test
	void testGetVehicleByModel() { 
	    // création des objets véhicules
	    Vehicle vehicle1 = createTestVehicule();
	    VehicleDto vehicleDto1 = createTestVehiculeDto();
	    Vehicle vehicle2 = Vehicle.builder().brand("Renault").manufacturingYear(2022).model("CLIO")
	            .typeCarburant(TypeCarburant.GASOIL).build();
	    VehicleDto vehicleDto2 = VehicleDto.builder().brand("Renault").manufacturingYear(2022).model("CLIO")
	            .typeCarburant(TypeCarburant.GASOIL).build();
	    Vehicle vehicle3 = Vehicle.builder().brand("Renault").manufacturingYear(2020).model("CLIO")
	            .typeCarburant(TypeCarburant.GASOIL).build();
	    VehicleDto vehicleDto3 = VehicleDto.builder().brand("Renault").manufacturingYear(2020).model("CLIO")
	            .typeCarburant(TypeCarburant.GASOIL).build();
	    
	    // Mock des mappers
	    when(vehicleMapper.toVehicleDto(vehicle2)).thenReturn(vehicleDto2);
	    when(vehicleMapper.toVehicleDto(vehicle3)).thenReturn(vehicleDto3);
	    
	    List<Vehicle> expectedVehicles = Arrays.asList(vehicle2, vehicle3);
	    List<VehicleDto> expectedVehicleDtos = Arrays.asList(vehicleDto2, vehicleDto3);
	    
	    // Simuler la récupération des véhicules par model depuis le repository
	    when(vehicleRepository.findByModel("CLIO")).thenReturn(expectedVehicles);

	    // appel à la méthode à tester
	    List<VehicleDto> resultVehicleDtos = vehicleService.getVehicleByModel("CLIO");
	    
	    // Vérifications des résultats
	    assertNotNull(resultVehicleDtos);
	    assertEquals(2, resultVehicleDtos.size());
	    assertEquals(expectedVehicleDtos, resultVehicleDtos);
	    assertTrue(resultVehicleDtos.contains(vehicleDto2));
	    assertTrue(resultVehicleDtos.contains(vehicleDto3));
	    assertFalse(resultVehicleDtos.contains(vehicleDto1));
	 // Vérifier que la méthode findByModel() a bien été appelée une fois
	 		verify(vehicleRepository, times(1)).findByModel("CLIO");
	}

		@Test
		void testGetVehicleByGarage() { 
		    // création des objets véhicules
		    Vehicle vehicle1 = createTestVehicule(); 
		    VehicleDto vehicleDto1 = createTestVehiculeDto();
		    
		    Vehicle vehicle2 = Vehicle.builder()
		            .brand("Renault").manufacturingYear(2022).model("CLIO").typeCarburant(TypeCarburant.GASOIL).build();
		    VehicleDto vehicleDto2 = VehicleDto.builder()
		            .brand("Renault").manufacturingYear(2022).model("CLIO").typeCarburant(TypeCarburant.GASOIL).build();
		    
		    Vehicle vehicle3 = Vehicle.builder() 
		            .brand("Renault").manufacturingYear(2020).model("CLIO").typeCarburant(TypeCarburant.ESSENCE).build();
		    VehicleDto vehicleDto3 = VehicleDto.builder()
		            .brand("Renault").manufacturingYear(2020).model("CLIO").typeCarburant(TypeCarburant.ESSENCE).build();
		    
		    // Mock des mappers
		    when(vehicleMapper.toVehicleDto(vehicle2)).thenReturn(vehicleDto2);
		    when(vehicleMapper.toVehicleDto(vehicle3)).thenReturn(vehicleDto3);

		    // Création d'une liste de véhicules qui sera ajoutée aux garages
		    List<Vehicle> garageVehicles = Arrays.asList(vehicle2, vehicle3);
		    
		    // Création du garage et association des véhicules à ce garage
		    Garage garage = createTestGarage();
		    garage.setVehicles(garageVehicles);
		    
		    // Création du garageDto et association des véhicules à ce garage
		    GarageDto garageDto = GarageDto.builder().id(1L).name("garage Casablanca").address("Casablanca")
		            .email("gar-casa@renault.com").phone("0566778899").vehicles(Arrays.asList(vehicleDto2, vehicleDto3)).build();
		    
		    // Simulation de la récupération du garage par ID depuis le repository
		    when(garageRepository.findById(1L)).thenReturn(java.util.Optional.of(garage)); 
		    
		    // appel à la méthode à tester 
		    List<VehicleDto> actualVehicleDtos = vehicleService.getVehicleByGarage(1L); 
		    
		    // Vérifications des résultats
		    assertNotNull(actualVehicleDtos);
		    assertEquals(2, actualVehicleDtos.size());
		    assertFalse(actualVehicleDtos.contains(vehicleDto1));
		    assertTrue(actualVehicleDtos.contains(vehicleDto2));
		    assertTrue(actualVehicleDtos.contains(vehicleDto3));
		    
		    // Vérifier que la méthode findById() a bien été appelée une fois
		    verify(garageRepository, times(1)).findById(1L);
		}
		
}
