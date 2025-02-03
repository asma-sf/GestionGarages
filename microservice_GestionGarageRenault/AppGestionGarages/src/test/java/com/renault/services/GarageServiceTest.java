package com.renault.services;

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

import com.renault.entities.Accessoire;
import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;
import com.renault.entities.Vehicule;
import com.renault.enums.TypeVehicule;
import com.renault.repositories.AccessoireRepository;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehiculeRepository;

import jakarta.transaction.Transactional;

@Transactional
public class GarageServiceTest {

    @Mock
    private GarageRepository garageRepository;
    
    @Mock
    private VehiculeRepository vehiculeRepository;
    
    @Mock
    private AccessoireRepository accessoireRepository;
    
   
    
    @InjectMocks
    private GarageServiceImpl garageService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    private Garage createTestGarage() {
        // Création du garage
        Garage garage = Garage.builder()
        		.name("garage Casablanca")
                .address("Casablanca")
                .email("gar-casa@renault.com")
                .telephone("0566778899")
            .build();

        // Création des créneaux horaires
        List<OpeningTime> mondayTimes = Arrays.asList(
            OpeningTime.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build(),
            OpeningTime.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(18, 0))
                .build()
        );

        List<OpeningTime> tuesdayTimes = Arrays.asList(
            OpeningTime.builder()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build(),
            OpeningTime.builder()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(18, 0))
                .build()
        );


        // Création des groupes d'horaires
        Map<DayOfWeek, GarageOpeningTime> openingTimes = new HashMap<>();
        
        openingTimes.put(DayOfWeek.MONDAY, GarageOpeningTime.builder()
            .dayOfWeek(DayOfWeek.MONDAY)
            .openingTimes(mondayTimes)
            .build());
        
        openingTimes.put(DayOfWeek.TUESDAY, GarageOpeningTime.builder()
            .dayOfWeek(DayOfWeek.TUESDAY)
            .openingTimes(tuesdayTimes)
            .build());

        // Lier les groupes au garage
        garage.setGarageOpeningTimes(openingTimes);

        // Définir le groupe pour chaque créneau horaire
        openingTimes.values().forEach(groupTime -> 
            groupTime.getOpeningTimes().forEach(openingTime -> 
                openingTime.setGroup(groupTime)
            )
        );

        return garage;
    }
    
    @Test
    void testAddGarage() {
    	
    
        // Création du garage
        Garage garage = createTestGarage();

        // Mock du garageRepository
        when(garageRepository.save(any(Garage.class))).thenReturn(garage);

        // Appel de la méthode à tester
        Garage savedGarage = garageService.addGarage(garage);

        // Vérification du garage et des horaires
        assertNotNull(savedGarage);
        assertEquals("garage Casablanca", savedGarage.getName());
        
        // Vérification des horaires d'ouverture
        assertNotNull(savedGarage.getGarageOpeningTimes());
        assertEquals(2, savedGarage.getGarageOpeningTimes().size()); // Vérifiez le nombre de jours enregistrés
        
        // Vérification des horaires du lundi
        GarageOpeningTime mondayOpeningTime = savedGarage.getGarageOpeningTimes().get(DayOfWeek.MONDAY);
        assertNotNull(mondayOpeningTime);
        assertEquals(DayOfWeek.MONDAY, mondayOpeningTime.getDayOfWeek());
        
        // Vérification des créneaux horaires du lundi
        List<OpeningTime> mondayTimes = mondayOpeningTime.getOpeningTimes();
        assertNotNull(mondayTimes);
        assertEquals(2, mondayTimes.size());
        
        // Vérification du premier créneau du lundi
        OpeningTime firstMondayTime = mondayTimes.get(0);
        assertEquals(LocalTime.of(8, 0), firstMondayTime.getStartTime());
        assertEquals(LocalTime.of(12, 0), firstMondayTime.getEndTime());
        // Vérification que save a bien été appelé
        verify(garageRepository).save(garage); // Vérifier que le garage a été sauvegardé
    }
    @Test
    void testUpdateGarage() {
    	// Création du garage
        Garage garage = createTestGarage();
        
        // Simuler la récupération du garage par ID
        when(garageRepository.findById(1L)).thenReturn(java.util.Optional.of(garage)); 
     // Simuler l'enregistrement du garage mis à jour
        when(garageRepository.save(any(Garage.class))).thenReturn(garage);
       // garage.setName("garage rabat");
        //garage.setAddress("Rabat");
        Garage garegeToUpdate= Garage
        		.builder().id(garage.getId()).name("garage rabat").address("Rabat").build();
        
       // appel de la méthode à tester
        Garage updatedGarage = garageService.updateGarage(1L, garegeToUpdate);
        // Vérification du garage
        assertNotNull(updatedGarage);
        assertEquals("garage rabat", updatedGarage.getName());
        assertEquals("Rabat", updatedGarage.getAddress());
     // Vérification que save a bien été appelé
        verify(garageRepository).save(updatedGarage);  // Vérifier que save a bien été appelé
        verify(garageRepository).findById(1L);  // Vérifier que findById a bien été appelé 
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
        verify(garageRepository).findById(1L);  // Vérifiez que findById a bien été appelé
    }
    @Test
    void testGetGarageById() {
    	// Création du garage
        Garage garage = createTestGarage();
     // Simuler la récupération du garage par ID
        when(garageRepository.findById(1L)).thenReturn(java.util.Optional.of(garage)); 
     // Simuler la recuperation du garage
        Garage garageRecupere= garageService.getGarageById(1L);
        
        // Vérification du garage récupéré
    assertNotNull(garageRecupere);
    assertEquals("garage Casablanca", garageRecupere.getName());
    assertEquals("Casablanca", garageRecupere.getAddress());
    // Vérifier que findById a bien été appelé 
    verify(garageRepository).findById(1L);  
    }
    @Test
    void testListGarages() {
    	// Création du garage
        Garage garage = createTestGarage();
        Garage garage2 =Garage.builder()
        .name("garage rabat")
        .address("Rabat")
        .email("gar-rabat@renault.com")
        .telephone("056677800")
        /*.garageOpeningTimes(Arrays.asList(
            GarageOpeningTime.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .openingTimes(Arrays.asList(
                    OpeningTime.builder().startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(12, 0)).build(),
                    OpeningTime.builder().startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(18, 0)).build()))
                .build()))*/
        .build();
        Garage garage3 =Garage.builder()
                .name("garage Salé")
                .address("Salé")
                .email("gar-sale@renault.com")
                .telephone("056677800")
               /* .garageOpeningTimes(Arrays.asList(
                    GarageOpeningTime.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTimes(Arrays.asList(
                            OpeningTime.builder().startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(12, 0)).build(),
                            OpeningTime.builder().startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(18, 0)).build()))
                        .build()))*/
                .build();
        when(garageRepository.save(any(Garage.class))).thenReturn(garage);
        when(garageRepository.save(any(Garage.class))).thenReturn(garage2);
        when(garageRepository.save(any(Garage.class))).thenReturn(garage3);
        
     // Simulation de la méthode findAll pour retourner une Page
        Page<Garage> pagedGarages = new PageImpl<>(Arrays.asList(garage, garage2), PageRequest.of(0, 2, Sort.by("name").ascending()), 3);
        
        when(garageRepository.findAll(any(Pageable.class))).thenReturn(pagedGarages); // Simuler findAll avec la page

     // Appel de la méthode à tester
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());
        Page<Garage> result = garageService.listGarages(pageable);
        // Vérifications
        assertNotNull(result);
        assertEquals(2, result.getContent().size());  // Vérifier que la page contient 2 éléments
        assertEquals("garage Casablanca", result.getContent().get(0).getName());  // Vérifier le premier garage
        assertEquals("garage rabat", result.getContent().get(1).getName());  // Vérifier le deuxième garage

        // Vérification de l'appel à findAll
        verify(garageRepository).findAll(pageable);  // Vérifier que findAll a bien été appelé

    }
        @Test
    	void testSearchGaragesByAccessoire() {
    		
        	Garage garage1 = createTestGarage();
        	Garage garage2 =Garage.builder()
        	        .name("garage rabat")
        	        .address("Rabat")
        	        .email("gar-rabat@renault.com")
        	        .telephone("056677800")
        	        /*.garageOpeningTimes(Arrays.asList(
        	            GarageOpeningTime.builder()
        	                .dayOfWeek(DayOfWeek.MONDAY)
        	                .openingTimes(Arrays.asList(
        	                    OpeningTime.builder().startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(12, 0)).build(),
        	                    OpeningTime.builder().startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(18, 0)).build()))
        	                .build()))*/
        	        .build();
        	Garage garage3 =Garage.builder()
                    .name("garage Salé")
                    .address("Salé")
                    .email("gar-sale@renault.com")
                    .telephone("056677800")
                    /*.garageOpeningTimes(Arrays.asList(
                        GarageOpeningTime.builder()
                            .dayOfWeek(DayOfWeek.MONDAY)
                            .openingTimes(Arrays.asList(
                                OpeningTime.builder().startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(12, 0)).build(),
                                OpeningTime.builder().startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(18, 0)).build()))
                            .build()))*/
                    .build();
        	Accessoire accessoire1 = Accessoire.builder()
        			.nom("Caméra")
        			.description("Caméra de recul")
        			.prix(2000)
        			.type("Sécurité")
        			.build();
        	Accessoire accessoire2 = Accessoire.builder()
        			.nom("GPS")
        			.description("Système de navigation intégré")
        			.prix(2500)
        			.type("Sécurité")
        			.build();   	
        	Accessoire accessoire3 = Accessoire.builder()
        			.nom("Ecran")
        			.description("Ecran tactil")
        			.prix(1500)
        			.type("Technologie")
        			.build();


        	List<Accessoire> accessoires1 = Arrays.asList(accessoire1, accessoire2);// ajouter GPS et camera
            List<Accessoire> accessoires2 = Arrays.asList(accessoire1); // ajouter que la camera
            List<Accessoire> accessoires3 = Arrays.asList(accessoire3); // ajouter que l'ecran
        	Vehicule vehicule1 =Vehicule.builder()
        			.brand("Renault")
        			.anneeFabrication(2021)
        			.model("Captur")
        			.accessoires(accessoires1)
        			.typeCarburant("Diesel")
        			.typeVehicule(TypeVehicule.SUV)
        			.garage(garage1)
        			.build();
        	Vehicule vehicule2 =Vehicule.builder()
        			.brand("Renault")
        			.anneeFabrication(2022)
        			.model("CLIO")
        			.accessoires(accessoires2)
        			.typeCarburant("Diesel")
        			.typeVehicule(TypeVehicule.SUV)
        			.garage(garage2)
        			.build();
        	Vehicule vehicule3 =Vehicule.builder()
        			.brand("Renault")
        			.anneeFabrication(2022)
        			.model("Megan")
        			.accessoires(accessoires3)
        			.typeCarburant("Essence")
        			.typeVehicule(TypeVehicule.BERLINE)
        			.garage(garage3)
        			.build();
        	
        	List<Vehicule> vehiculesG1 = Arrays.asList(vehicule1,vehicule2);
        	List<Vehicule> vehiculesG2 = Arrays.asList(vehicule1);
        	List<Vehicule> vehiculesG3 = Arrays.asList(vehicule3);
        	garage1.setVehicules(vehiculesG1);
        	garage2.setVehicules(vehiculesG2);
        	garage3.setVehicules(vehiculesG3);
        	 // Mock des repositories
            when(garageRepository.save(any(Garage.class))).thenReturn(garage1, garage2, garage3);
            when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(vehicule1, vehicule2, vehicule3);
            when(accessoireRepository.save(any(Accessoire.class))).thenReturn(accessoire1, accessoire2, accessoire3);

            // on va chercher par l accesoire camera qui se trouve dans les deux vehicules une dans le garage A et la 2eme dans le garage 2
            List<Garage> garagesSearched = Arrays.asList(garage1,garage2);

            		
            		// Simuler findGaragesByAccessoire 
            		when(garageRepository.findGaragesByAccessoire("Caméra")).thenReturn(garagesSearched);
            		
            		// Appel de la méthode à tester
            		List<Garage> garageResult= garageService.searchGaragesByAccessoire("Caméra");
            		

            		//vérifications
            		assertNotNull(garageResult);
            		assertEquals(garagesSearched,garageResult);
            		assertEquals(2, garageResult.size());
            		assertTrue(garageResult.contains(garage1));
            		assertTrue(garageResult.contains(garage2));
            		// Vérifier que findGaragesByAccessoire a bien été appelé
            		verify(garageRepository, times(1)).findGaragesByAccessoire("Caméra");
    	    }
}

