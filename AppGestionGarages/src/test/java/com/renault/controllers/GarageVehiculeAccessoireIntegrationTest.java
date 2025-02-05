package com.renault.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renault.entities.Accessory;
import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;
import com.renault.entities.Vehicle;
import com.renault.enums.TypeCarburant;
import com.renault.exceptions.AccessoryNotFoundException;
import com.renault.exceptions.VehicleNotFoundException;
import com.renault.repositories.AccessoryRepository;
import com.renault.repositories.GarageRepository;
import com.renault.repositories.VehicleRepository;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class GarageVehiculeAccessoireIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private GarageRepository garageRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private AccessoryRepository accessoryRepository;

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

	private Vehicle createTestVehicule() {
		return Vehicle.builder().brand("Renault").manufacturingYear(2021).model("Captur")
				.typeCarburant(TypeCarburant.ESSENCE).accessories(new ArrayList<>()).build();
		
	}

	private Accessory createTestAccessoire() {
		return Accessory.builder().name("Caméra").description("Caméra de recul").price(2000)
				.type("Sécurité").build();
		
	}

	@Test
	public void LifecycleOperationsOnGarageVehiculeAndAccessoire() throws Exception {
		// 1. Créer et tester le garage
		Garage garage = createTestGarage();

		String garageJson = mockMvc
				.perform(post("/api/garages").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(garage)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("garage Casablanca"))
				.andExpect(jsonPath("$.address").value("Casablanca"))
				.andExpect(jsonPath("$.email").value("gar-casa@renault.com"))
				.andExpect(jsonPath("$.telephone").value("0566778899"))
				.andDo(print())
				.andExpect(jsonPath("$.garageOpeningTimes.MONDAY").exists())
				.andExpect(jsonPath("$.garageOpeningTimes.MONDAY.openingTimes[0].startTime").value("08:00:00"))
				.andExpect(jsonPath("$.garageOpeningTimes.MONDAY.openingTimes[0].endTime").value("12:00:00"))
				.andReturn().getResponse().getContentAsString();


		Garage createdGarage = objectMapper.readValue(garageJson, Garage.class);
		Long garageId = createdGarage.getId();
	
		// Vérification dans la base de données
		Garage savedGarage = garageRepository.findByName("garage Casablanca");
		assertNotNull(savedGarage);
		assertEquals("Casablanca", savedGarage.getAddress());
		assertNotNull(savedGarage.getId()); // Vérifier que l'ID est bien généré
		
		/*************************************************************************************************************************/
		// 2. Créer un véhicule et l'ajouter au garage
		Vehicle vehicule = createTestVehicule();
		String vehiculeJson = mockMvc
				.perform(post("/api/garages/{garageId}/vehicles", garageId).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(vehicule)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.brand").value("Renault"))
				.andExpect(jsonPath("$.model").value("Captur")).andExpect(jsonPath("$.manufacturingYear").value("2021"))
				// .andDo(print()) // Affiche la réponse complète pour la déboguer
				.andReturn().getResponse().getContentAsString();

		Vehicle createdVehicule = objectMapper.readValue(vehiculeJson, Vehicle.class);
		Long vehicleId = createdVehicule.getId();
		// Vérification dans la base de données
				Vehicle savedVehicule= vehicleRepository.findById(vehicleId).orElseThrow(()-> new VehicleNotFoundException(vehicleId));
				assertNotNull(savedVehicule);
				assertEquals("Renault", savedVehicule.getBrand());
				assertNotNull(savedGarage.getId()); // Vérifier que l'ID est bien généré
		
		/*************************************************************************************************************************/
		
	     // 3. Créer un accessoire et l'ajouter au véhicule
		Accessory accessory = createTestAccessoire();
		String accessoryJson = mockMvc
				.perform(post("/api/vehicles/{vehicleId}/accessories", vehicleId).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(accessory)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Caméra"))
				.andExpect(jsonPath("$.price").value(2000.0)).andExpect(jsonPath("$.type").value("Sécurité"))
				.andDo(print()) // Affiche la réponse complète pour la déboguer
				.andReturn().getResponse().getContentAsString();

		System.out.println("accesoireJson: " + accessoryJson);
		Accessory accessoryCreated = objectMapper.readValue(accessoryJson, Accessory.class);
		Long accessoryId = accessoryCreated.getId();
		// Vérification dans la base de données
		Accessory savedAccessory= accessoryRepository.findById(accessoryId).orElseThrow(()-> new AccessoryNotFoundException(vehicleId));
		assertNotNull(savedAccessory);
		assertEquals("Caméra", savedAccessory.getName());
		assertNotNull(savedAccessory.getId()); 
		
		/*************************************************************************************************************************/
		
		// 4. Modifier l'accessoire
		accessoryCreated.setName("Caméra 3D");
		accessoryCreated.setPrice(2500);
		// Convertir l'objet en JSON pour l'envoyer dans le corps de la requête
		String updatedAccessoryJson = objectMapper.writeValueAsString(accessoryCreated);
		mockMvc.perform(put("/api/accessories/{accessoryId}", accessoryId).contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON).content(updatedAccessoryJson)) // Body avec les nouvelles données
				.andExpect(status().isOk()) // Vérifie que la réponse est 200 OK
				.andExpect(jsonPath("$.name").value("Caméra 3D")).andExpect(jsonPath("$.price").value(2500.0));

		// Vérification dans la base de données
				Accessory savedUpdatedAccessory= accessoryRepository.findById(accessoryId).orElseThrow(()-> new AccessoryNotFoundException(vehicleId));
				assertNotNull(savedUpdatedAccessory);
				assertEquals("Caméra 3D", savedAccessory.getName());
				assertNotNull(savedUpdatedAccessory.getId()); 
		/*************************************************************************************************************************/
		// 5. Modifier le véhicule
		createdVehicule.setBrand("Bmw");
		createdVehicule.setModel("Bmw X3");
		String updatedVehicileJson = objectMapper.writeValueAsString(createdVehicule);
		mockMvc.perform(put("/api/vehicles/{vehicleId}", vehicleId).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(updatedVehicileJson)) // Body avec les nouvelles données
				.andExpect(status().isOk()) // Vérifie que la réponse est 200 OK
				.andExpect(jsonPath("$.brand").value("Bmw")).andExpect(jsonPath("$.model").value("Bmw X3"));
		
		// Vérification dans la base de données
		Vehicle savedUpdatedVehicule= vehicleRepository.findById(vehicleId).orElseThrow(()-> new VehicleNotFoundException(vehicleId));
		assertNotNull(savedUpdatedVehicule);
		assertEquals("Bmw", savedUpdatedVehicule.getBrand());
		assertNotNull(savedUpdatedVehicule.getId()); 
		/*************************************************************************************************************************/
		// 6. Supprimer l'accessoire
		
		//Avant la suppression :");
		assertEquals(1,accessoryRepository.findAll().size());

		mockMvc.perform(delete("/api/accessories/{accessoireId}", accessoryId)
		        .accept(MediaType.APPLICATION_JSON))
		        .andExpect(status().isOk());
		       // .andDo(print()); // Pour voir la réponse complète
		
		// Vérifier que l'accesoire  n'existe pas
		    //Apres la suppression 
		        Optional<Accessory> accessoireApresDelete = accessoryRepository.findById(accessoryId);
				assertEquals(0,accessoryRepository.findAll().size());
		        assertFalse(accessoireApresDelete.isPresent());

		  /*************************************************************************************************************************/
		     // 7. Supprimer le véhicule
		      //Avant la suppression :");
				assertEquals(1,vehicleRepository.findAll().size());
				mockMvc.perform(delete("/api/vehicles/{vehicleId}", vehicleId).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk());
				
				// Vérifier que le vehicule  n'existe pas
			    //Apres la suppression 
			        Optional<Vehicle> vehiculeApresDelete = vehicleRepository.findById(vehicleId);
					assertEquals(0,vehicleRepository.findAll().size());
			        assertFalse(vehiculeApresDelete.isPresent());

		/*************************************************************************************************************************/
				// 8. Supprimer le garage
			        //Avant la suppression :");
						assertEquals(1,garageRepository.findAll().size());
			        
				mockMvc.perform(delete("/api/garages/{garageId}", garageId).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk());

				// Vérifier que le garage  n'existe pas
			    //Apres la suppression 
			        Optional<Garage> garageApresDelete = garageRepository.findById(garageId);
					assertEquals(0,garageRepository.findAll().size());
			        assertFalse(garageApresDelete.isPresent());                    
	}

}
