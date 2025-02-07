
package com.renault.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.renault.dtos.AccessoryDto;
import com.renault.dtos.GarageDto;
import com.renault.dtos.GarageOpeningTimeDto;
import com.renault.dtos.OpeningTimeDto;
import com.renault.dtos.VehicleDto;
import com.renault.entities.Accessory;
import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;
import com.renault.entities.Vehicle;
import com.renault.enums.TypeCarburant;
import com.renault.mappers.AccessoryMapper;
import com.renault.mappers.GarageMapper;
import com.renault.mappers.VehicleMapper;
import com.renault.repositories.AccessoryRepository;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehicleRepository;

import jakarta.transaction.Transactional;

public class GarageServiceTest {

	@Mock
	private GarageRepository garageRepository;

	@Mock
	private VehicleRepository vehicleRepository;

	@Mock
	private AccessoryRepository accessoryRepository;
	@Mock
	private VehicleMapper vehicleMapper;
	@Mock
	private AccessoryMapper accessoryMapper;
	@Mock
	private GarageMapper garageMapper;

	@InjectMocks
	private GarageServiceImpl garageService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private Garage createTestGarage() { // Création du garage
		Garage garage = Garage.builder().name("garage Casablanca").address("Casablanca").email("gar-casa@renault.com")
				.phone("0566778899").build();

		// Création des créneaux horaires
		List<OpeningTime> mondayTimes = Arrays.asList(
				OpeningTime.builder().dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(8, 0))
						.endTime(LocalTime.of(12, 0)).build(),
				OpeningTime.builder().dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(14, 0))
						.endTime(LocalTime.of(18, 0)).build());

		List<OpeningTime> tuesdayTimes = Arrays.asList(
				OpeningTime.builder().dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(8, 0))
						.endTime(LocalTime.of(12, 0)).build(),
				OpeningTime.builder().dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(15, 0))
						.endTime(LocalTime.of(18, 0)).build());

		// Création des groupes d'horaires
		Map<DayOfWeek, GarageOpeningTime> GarageOpeningTimes = new HashMap<>();

		GarageOpeningTimes.put(DayOfWeek.MONDAY,
				GarageOpeningTime.builder().dayOfWeek(DayOfWeek.MONDAY).openingTimes(mondayTimes).build());

		GarageOpeningTimes.put(DayOfWeek.TUESDAY,
				GarageOpeningTime.builder().dayOfWeek(DayOfWeek.TUESDAY).openingTimes(tuesdayTimes).build());

		// Lier les groupes au garage
		garage.setOpeningTimes(GarageOpeningTimes);

		// Définir le groupe pour chaque créneau horaire
		GarageOpeningTimes.values().forEach(groupTime -> groupTime.getOpeningTimes()
				.forEach(openingTime -> openingTime.setGarageOpeningTime(groupTime)));

		return garage;
	}

	private GarageDto createTestGarageDto() {
		// Création du garage
		GarageDto garageDto = GarageDto.builder().name("garage Casablanca").address("Casablanca")
				.email("gar-casa@renault.com").phone("0566778899").build();

		// Création des créneaux horaires
		List<OpeningTimeDto> mondayTimes = Arrays.asList(
				OpeningTimeDto.builder().dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(8, 0))
						.endTime(LocalTime.of(12, 0)).build(),
				OpeningTimeDto.builder().dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(14, 0))
						.endTime(LocalTime.of(18, 0)).build());

		List<OpeningTimeDto> tuesdayTimes = Arrays.asList(
				OpeningTimeDto.builder().dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(8, 0))
						.endTime(LocalTime.of(12, 0)).build(),
				OpeningTimeDto.builder().dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(15, 0))
						.endTime(LocalTime.of(18, 0)).build());

		// Création des groupes d'horaires
		Map<DayOfWeek, List<OpeningTimeDto>> garageOpeningTimeDtos = new HashMap<>();

		garageOpeningTimeDtos.put(DayOfWeek.MONDAY, mondayTimes);
		garageOpeningTimeDtos.put(DayOfWeek.TUESDAY, tuesdayTimes);

		// Lier les groupes au garage
		garageDto = GarageDto.builder().name("garage Casablanca").address("Casablanca").email("gar-casa@renault.com")
				.phone("0566778899").openingTimes(garageOpeningTimeDtos).build();

		return garageDto;
	}

	private GarageDto createTestUpdatedGarageDto() {
		// Création du garage
		GarageDto garageDto = GarageDto.builder().name("garage Rabat").address("Rabat").email("gar-rabat@renault.com")
				.phone("0566770055").build();

		// Création des créneaux horaires
		List<OpeningTimeDto> mondayTimes = Arrays.asList(
				OpeningTimeDto.builder().dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(8, 0))
						.endTime(LocalTime.of(12, 0)).build(),
				OpeningTimeDto.builder().dayOfWeek(DayOfWeek.MONDAY).startTime(LocalTime.of(14, 0))
						.endTime(LocalTime.of(20, 0)).build());

		List<OpeningTimeDto> tuesdayTimes = Arrays.asList(
				OpeningTimeDto.builder().dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(8, 0))
						.endTime(LocalTime.of(12, 0)).build(),
				OpeningTimeDto.builder().dayOfWeek(DayOfWeek.TUESDAY).startTime(LocalTime.of(15, 0))
						.endTime(LocalTime.of(18, 0)).build());

		// Création des groupes d'horaires
		Map<DayOfWeek, List<OpeningTimeDto>> garageOpeningTimeDtos = new HashMap<>();

		garageOpeningTimeDtos.put(DayOfWeek.MONDAY, mondayTimes);
		garageOpeningTimeDtos.put(DayOfWeek.TUESDAY, tuesdayTimes);

		// Lier les groupes au garage
		garageDto = GarageDto.builder().name("garage Rabat").address("Rabat").email("gar-rabat@renault.com")
				.phone("0566770055").openingTimes(garageOpeningTimeDtos).build();

		return garageDto;
	}

	@Test
	void testAddGarage() {
		// Création du garage
		Garage garage = createTestGarage();
		GarageDto garageDto = createTestGarageDto();

		// Mock du garageRepository
		when(garageRepository.save(any(Garage.class))).thenReturn(garage);
		// Mock du mapper
		when(garageMapper.toGarage(any(GarageDto.class))).thenReturn(garage);
		when(garageMapper.toGarageDto(any(Garage.class))).thenReturn(garageDto);

		// Appel de la méthode à tester
		GarageDto savedGarageDto = garageService.addGarage(garageDto);

		// Vérification du garage et des horaires
		assertNotNull(savedGarageDto);
		assertEquals("garage Casablanca", savedGarageDto.name());

		// Vérification des horaires d'ouverture
		assertNotNull(savedGarageDto.openingTimes());
		assertEquals(2, savedGarageDto.openingTimes().size()); // Vérifiez le nombre de jours enregistrés

		// Vérification des horaires du lundi
		List<OpeningTimeDto> mondayOpeningTimes = savedGarageDto.openingTimes().get(DayOfWeek.MONDAY);
		assertNotNull(mondayOpeningTimes);
		assertEquals(2, mondayOpeningTimes.size());

		// Vérification du premier créneau du lundi
		OpeningTimeDto firstMondayTime = mondayOpeningTimes.get(0);
		assertEquals(LocalTime.of(8, 0), firstMondayTime.startTime());
		assertEquals(LocalTime.of(12, 0), firstMondayTime.endTime());

		// Vérification que save a bien été appelé
		verify(garageRepository).save(garage); // Vérifier que le garage a été sauvegardé
	}

	@Test
	void testGetGarageById() {
		// Création du garage
		Garage garage = createTestGarage();
		// Création du GarageDto
		GarageDto garageDto = createTestGarageDto();

		// Mock du repository : Simuler la récupération du garage par ID
		when(garageRepository.findById(1L)).thenReturn(java.util.Optional.of(garage));
		// Mock du mapper : Simuler la conversion du Garage en GarageDto
		when(garageMapper.toGarageDto(any(Garage.class))).thenReturn(garageDto);
		when(garageMapper.toGarage(any(GarageDto.class))).thenReturn(garage);

		// Appel de la méthode à tester : Récupérer le garage par ID
		GarageDto retrievedGarageDto = garageService.getGarageById(1L);

		// Vérification du garage récupéré
		assertNotNull(retrievedGarageDto, "Le garage récupéré ne doit pas être null");
		assertEquals("garage Casablanca", retrievedGarageDto.name(), "Le nom du garage doit être 'garage Casablanca'");
		assertEquals("Casablanca", retrievedGarageDto.address(), "L'adresse du garage doit être 'Casablanca'");

		// Vérification que findById a bien été appelé
		verify(garageRepository).findById(1L);
	}

	@Test
	void testUpdateGarage() {
		// Création du garage
		Garage garage = createTestGarage();
		// Création du garageDTO avec les nouvelles données
		GarageDto garageDto = createTestUpdatedGarageDto();

		// Mock du repository Simuler la récupération du garage par ID
		when(garageRepository.findById(1L)).thenReturn(java.util.Optional.of(garage));
		// Mock du mapper
		when(garageMapper.toGarageDto(any(Garage.class))).thenReturn(garageDto);
		when(garageMapper.toGarage(any(GarageDto.class))).thenReturn(garage);

		// Simuler l'enregistrement du garage mis à jour
		when(garageRepository.save(any(Garage.class))).thenReturn(garage);

		// appel de la méthode à tester Garage updatedGarage =
		GarageDto updatedGarageDto = garageService.updateGarage(1L, garageDto);

		// Vérification du garage
		assertNotNull(updatedGarageDto);
		assertEquals("garage Rabat", updatedGarageDto.name());
		assertEquals("Rabat", updatedGarageDto.address());

		// Vérification que save a bien été appelé
		verify(garageRepository).save(garage);
		verify(garageRepository).findById(1L); // Vérifier que findById a bien été appelé

	}

	@Test
	void testDeleteGarage() {
		// Création du garage
		Garage garage = createTestGarage();
		// Simuler la récupération du garage par ID
		when(garageRepository.findById(1L)).thenReturn(java.util.Optional.of(garage));
		// Simuler la suppression du garage
		doNothing().when(garageRepository).delete(garage);
		// appel de la méthode à tester
		garageService.deleteGarage(1L);

		// Vérification que la méthode delete a bien été appelée
		verify(garageRepository).deleteById(1L);
		verify(garageRepository).findById(1L); // Vérifiez que findById a bien été appelé
	}

	@Test
	void testListGarages() {
		// Création des garages
		Garage garage1 = createTestGarage();
		Garage garage2 = Garage.builder().name("garage Rabat").address("Rabat").email("gar-rabat@renault.com")
				.phone("056677800").build();
		Garage garage3 = Garage.builder().name("garage Salé").address("Salé").email("gar-sale@renault.com")
				.phone("056677800").build();

		// Création des GarageDto
		GarageDto garageDto1 = createTestGarageDto();
		GarageDto garageDto2 = GarageDto.builder().name("garage Rabat").address("Rabat").email("gar-rabat@renault.com")
				.phone("056677800").build();
		GarageDto garageDto3 = GarageDto.builder().name("garage Salé").address("Salé").email("gar-sale@renault.com")
				.phone("056677800").build();

		// Mock du mapper
		when(garageMapper.toGarageDto(garage1)).thenReturn(garageDto1);
		when(garageMapper.toGarageDto(garage2)).thenReturn(garageDto2);
		when(garageMapper.toGarageDto(garage3)).thenReturn(garageDto3);

		// Simulation de la méthode findAll pour retourner une Page
		Page<Garage> pagedGarages = new PageImpl<>(Arrays.asList(garage1, garage2),
				PageRequest.of(0, 2, Sort.by("name").ascending()), 3);

		// Simuler findAll avec la page
		when(garageRepository.findAll(any(Pageable.class))).thenReturn(pagedGarages);

		// Appel de la méthode à tester
		Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());
		Page<GarageDto> result = garageService.listGarages(pageable);

		// Vérifications
		assertNotNull(result);
		assertEquals(2, result.getContent().size()); // Vérifier que la page contient 2 éléments
		assertEquals("garage Casablanca", result.getContent().get(0).name()); // Vérifier le premier garage
		assertEquals("garage Rabat", result.getContent().get(1).name()); // Vérifier le deuxième garage

		// Vérification de l'appel à findAll
		verify(garageRepository).findAll(pageable); // Vérifier que findAll a bien été appelé
	}

	@Test
	void testSearchGaragesByAccessory() {
	    // Création des garages
	    Garage garage1 = createTestGarage();
	    Garage garage2 = Garage.builder().name("garage Rabat").address("Rabat").email("gar-rabat@renault.com").phone("056677800").build();
	    Garage garage3 = Garage.builder().name("garage Salé").address("Salé").email("gar-sale@renault.com").phone("056677800").build();

	    // Création des accessoires
	    Accessory accessory1 = Accessory.builder().name("Caméra").description("Caméra de recul").price(2000).type("Sécurité").build();
	    Accessory accessory2 = Accessory.builder().name("GPS").description("Système de navigation intégré").price(2500).type("Sécurité").build();
	    Accessory accessory3 = Accessory.builder().name("Ecran").description("Ecran tactile").price(1500).type("Technologie").build();

	    // Création des AccessoryDto
	    AccessoryDto accessoryDto1 = AccessoryDto.builder().name("Caméra").description("Caméra de recul").price(2000).type("Sécurité").build();
	    AccessoryDto accessoryDto2 = AccessoryDto.builder().name("GPS").description("Système de navigation intégré").price(2500).type("Sécurité").build();
	    AccessoryDto accessoryDto3 = AccessoryDto.builder().name("Ecran").description("Ecran tactile").price(1500).type("Technologie").build();

	    // Création des listes d'accessoires
	    List<Accessory> accessories1 = Arrays.asList(accessory1, accessory2); // GPS et caméra
	    List<Accessory> accessories2 = Arrays.asList(accessory1); // Caméra uniquement
	    List<Accessory> accessories3 = Arrays.asList(accessory3); // Écran uniquement

	    List<AccessoryDto> accessoriesDto1 = Arrays.asList(accessoryDto1, accessoryDto2); // GPS et caméra
	    List<AccessoryDto> accessoriesDto2 = Arrays.asList(accessoryDto1); // Caméra uniquement
	    List<AccessoryDto> accessoriesDto3 = Arrays.asList(accessoryDto3); // Écran uniquement

	    // Création des véhicules
	    Vehicle vehicle1 = Vehicle.builder().brand("Renault").manufacturingYear(2021).model("Captur").accessories(accessories1).typeCarburant(TypeCarburant.ESSENCE).garage(garage1).build();
	    Vehicle vehicle2 = Vehicle.builder().brand("Renault").manufacturingYear(2022).model("CLIO").accessories(accessories2).typeCarburant(TypeCarburant.GASOIL).garage(garage2).build();
	    Vehicle vehicle3 = Vehicle.builder().brand("Renault").manufacturingYear(2022).model("Megan").accessories(accessories3).typeCarburant(TypeCarburant.GASOIL).garage(garage3).build();

	    // Création des VehicleDto
	    VehicleDto vehicleDto1 = VehicleDto.builder().brand("Renault").manufacturingYear(2021).model("Captur").accessories(accessoriesDto1).typeCarburant(TypeCarburant.ESSENCE).garageID(garage1.getId()).build();
	    VehicleDto vehicleDto2 = VehicleDto.builder().brand("Renault").manufacturingYear(2022).model("CLIO").accessories(accessoriesDto2).typeCarburant(TypeCarburant.GASOIL).garageID(garage2.getId()).build();
	    VehicleDto vehicleDto3 = VehicleDto.builder().brand("Renault").manufacturingYear(2022).model("Megan").accessories(accessoriesDto3).typeCarburant(TypeCarburant.GASOIL).garageID(garage3.getId()).build();

	    // Association des véhicules aux garages
	    garage1.setVehicles(Arrays.asList(vehicle1, vehicle2));
	    garage2.setVehicles(Arrays.asList(vehicle2));
	    garage3.setVehicles(Arrays.asList(vehicle3));

	    // Création des GarageDto
	    GarageDto garageDto1 = GarageDto.builder().name("garage Casablanca").address("Casablanca").email("gar-casa@renault.com").phone("0566778899").vehicles(Arrays.asList(vehicleDto1, vehicleDto2)).build();
	    GarageDto garageDto2 = GarageDto.builder().name("garage Rabat").address("Rabat").email("gar-rabat@renault.com").phone("056677800").vehicles(Arrays.asList(vehicleDto2)).build();
	    GarageDto garageDto3 = GarageDto.builder().name("garage Salé").address("Salé").email("gar-sale@renault.com").phone("056677800").vehicles(Arrays.asList(vehicleDto3)).build();

	    // Mock des repositories
	    when(garageRepository.save(any(Garage.class))).thenReturn(garage1, garage2, garage3);
	    when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle1, vehicle2, vehicle3);
	    when(accessoryRepository.save(any(Accessory.class))).thenReturn(accessory1, accessory2, accessory3);

	    // Mock du mapper
	    when(garageMapper.toGarageDto(garage1)).thenReturn(garageDto1);
	    when(garageMapper.toGarageDto(garage2)).thenReturn(garageDto2);
	    when(garageMapper.toGarageDto(garage3)).thenReturn(garageDto3);

	    when(vehicleMapper.toVehicleDto(vehicle1)).thenReturn(vehicleDto1);
	    when(vehicleMapper.toVehicleDto(vehicle2)).thenReturn(vehicleDto2);
	    when(vehicleMapper.toVehicleDto(vehicle3)).thenReturn(vehicleDto3);

	    when(accessoryMapper.toAccessoryDto(accessory1)).thenReturn(accessoryDto1);
	    when(accessoryMapper.toAccessoryDto(accessory2)).thenReturn(accessoryDto2);
	    when(accessoryMapper.toAccessoryDto(accessory3)).thenReturn(accessoryDto3);

	    // Simuler la recherche de garages par accessoire
	    List<Garage> garagesSearched = Arrays.asList(garage1, garage2);
	    List<GarageDto> garagesDtoSearched = Arrays.asList(garageDto1, garageDto2);

	    when(garageRepository.findGaragesByAccessory("Caméra")).thenReturn(garagesSearched);

	    // Appel de la méthode à tester
	    List<GarageDto> garageResult = garageService.searchGaragesByAccessory("Caméra");

	    // Vérifications
	    assertNotNull(garageResult);
	    assertEquals(garagesDtoSearched, garageResult);
	    assertEquals(2, garageResult.size());
	    assertTrue(garageResult.contains(garageDto1));
	    assertTrue(garageResult.contains(garageDto2));

	    // Vérifier que findGaragesByAccessory a bien été appelé
	    verify(garageRepository, times(1)).findGaragesByAccessory("Caméra");
	}
	
	@Test
	void testSearchGaragesByVehicleType() {
	    // Création des garages
	    Garage garage1 = createTestGarage();
	    Garage garage2 = Garage.builder().name("garage Rabat").address("Rabat").email("gar-rabat@renault.com").phone("056677800").build();
	    Garage garage3 = Garage.builder().name("garage Salé").address("Salé").email("gar-sale@renault.com").phone("056677800").build();

	    // Création des véhicules
	    Vehicle vehicle1 = Vehicle.builder().brand("Renault").manufacturingYear(2021).model("Captur").typeCarburant(TypeCarburant.ESSENCE).garage(garage1).build();
	    Vehicle vehicle2 = Vehicle.builder().brand("Renault").manufacturingYear(2022).model("CLIO").typeCarburant(TypeCarburant.GASOIL).garage(garage2).build();
	    Vehicle vehicle3 = Vehicle.builder().brand("Renault").manufacturingYear(2022).model("Megan").typeCarburant(TypeCarburant.HYBRID).garage(garage3).build();
	    Vehicle vehicle4 = Vehicle.builder().brand("Renault").manufacturingYear(2022).model("Koleos").typeCarburant(TypeCarburant.ESSENCE).garage(garage3).build();

	    // Création des VehicleDto
	    VehicleDto vehicleDto1 = VehicleDto.builder().brand("Renault").manufacturingYear(2021).model("Captur").typeCarburant(TypeCarburant.ESSENCE).garageID(garage1.getId()).build();
	    VehicleDto vehicleDto2 = VehicleDto.builder().brand("Renault").manufacturingYear(2022).model("CLIO").typeCarburant(TypeCarburant.GASOIL).garageID(garage2.getId()).build();
	    VehicleDto vehicleDto3 = VehicleDto.builder().brand("Renault").manufacturingYear(2022).model("Megan").typeCarburant(TypeCarburant.HYBRID).garageID(garage3.getId()).build();
	    VehicleDto vehicleDto4 = VehicleDto.builder().brand("Renault").manufacturingYear(2022).model("Koleos").typeCarburant(TypeCarburant.ESSENCE).garageID(garage3.getId()).build();

	    // Association des véhicules aux garages
	    garage1.setVehicles(Arrays.asList(vehicle1, vehicle2));
	    garage2.setVehicles(Arrays.asList(vehicle2));
	    garage3.setVehicles(Arrays.asList(vehicle3, vehicle4));

	    // Création des GarageDto
	    GarageDto garageDto1 = GarageDto.builder().name("garage Casablanca").address("Casablanca").email("gar-casa@renault.com").phone("0566778899").vehicles(Arrays.asList(vehicleDto1, vehicleDto2)).build();
	    GarageDto garageDto2 = GarageDto.builder().name("garage Rabat").address("Rabat").email("gar-rabat@renault.com").phone("056677800").vehicles(Arrays.asList(vehicleDto2)).build();
	    GarageDto garageDto3 = GarageDto.builder().name("garage Salé").address("Salé").email("gar-sale@renault.com").phone("056677800").vehicles(Arrays.asList(vehicleDto3, vehicleDto4)).build();

	    // Mock des repositories
	    when(garageRepository.save(any(Garage.class))).thenReturn(garage1, garage2, garage3);
	    when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle1, vehicle2, vehicle3, vehicle4);

	    // Mock du mapper
	    when(garageMapper.toGarageDto(garage1)).thenReturn(garageDto1);
	    when(garageMapper.toGarageDto(garage2)).thenReturn(garageDto2);
	    when(garageMapper.toGarageDto(garage3)).thenReturn(garageDto3);

	    when(vehicleMapper.toVehicleDto(vehicle1)).thenReturn(vehicleDto1);
	    when(vehicleMapper.toVehicleDto(vehicle2)).thenReturn(vehicleDto2);
	    when(vehicleMapper.toVehicleDto(vehicle3)).thenReturn(vehicleDto3);
	    when(vehicleMapper.toVehicleDto(vehicle4)).thenReturn(vehicleDto4);

	    // Simuler la recherche de garages par type de carburant
	    List<Vehicle> vehiclesReturned = Arrays.asList(vehicle1, vehicle4);
	    List<Garage> garagesSearched = Arrays.asList(garage1, garage3);
	    List<GarageDto> garagesDtoSearched = Arrays.asList(garageDto3, garageDto1);

	    when(vehicleRepository.findByTypeCarburant(TypeCarburant.ESSENCE)).thenReturn(vehiclesReturned);

	    // Appel de la méthode à tester
	    List<GarageDto> garageResult = garageService.searchGaragesByVehicleType("ESSENCE");

	    // Vérifications
	    assertNotNull(garageResult);
	    assertThat(garagesDtoSearched).containsExactlyInAnyOrderElementsOf(garageResult);
	    assertEquals(2, garageResult.size());
	    assertTrue(garageResult.contains(garageDto1));
	    assertTrue(garageResult.contains(garageDto3));

	    // Vérifier que findByTypeCarburant a bien été appelé
	    verify(vehicleRepository, times(1)).findByTypeCarburant(TypeCarburant.ESSENCE);
	}
}
