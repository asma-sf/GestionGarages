package com.renault;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.renault.dtos.GarageDto;
import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;
import com.renault.repositories.GarageOpeningTimeRepository;
import com.renault.repositories.GarageRepository;
import com.renault.services.GarageService;






@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.renault.repositories")
@ComponentScan(basePackages = "com.renault")
public class AppGestionGaragesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppGestionGaragesApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(GarageService garageService, GarageRepository garageRepository) {
		return arg-> {
			
			Garage garage1 = Garage.builder()
					.name("Garage Quods")
					.address("Boulveard Quods Sidi Maarouf")
					.email("gar.quods@renault.com")
					.phone("0522322367")
					.openingTimes(new HashMap<>())
					.build();
			Garage garage2 = Garage.builder()
					.name("Garage Calfornia")
					.address("Boulveard California")
					.email("gar.california@renault.com")
					.phone("0522333345")
					.openingTimes(new HashMap<>())
					.build();
			Garage garage3 = Garage.builder()
					.name("Garage Oasis")
					.address("Boulveard Oasis")
					.email("gar.oasis@renault.com")
					.phone("0522334466")
					.openingTimes(new HashMap<>())
					.build();

			
			// création des openningTime pour garage 1
			OpeningTime openingTimeMorningMonday = OpeningTime.builder()
					.startTime(LocalTime.of(8, 0))
					.endTime(LocalTime.of(12, 0))		
					.build();
			OpeningTime openingTimeEveningMonday = OpeningTime.builder()
					.startTime(LocalTime.of(14, 0))
					.endTime(LocalTime.of(18,0))		
					.build();
			OpeningTime openingTimeMorningTuesday= OpeningTime.builder()
					.startTime(LocalTime.of(8, 0))
					.endTime(LocalTime.of(12, 0))		
					.build();
			OpeningTime openingTimeEveningTuesday = OpeningTime.builder()
					.startTime(LocalTime.of(15, 0))
					.endTime(LocalTime.of(18,0))		
					.build();
			OpeningTime openingTimeMorningWensday= OpeningTime.builder()
					.startTime(LocalTime.of(9, 0))
					.endTime(LocalTime.of(12, 0))		
					.build();
			OpeningTime openingTimeEveningWensday = OpeningTime.builder()
					.startTime(LocalTime.of(14, 0))
					.endTime(LocalTime.of(18,0))		
					.build();
			 //  Création du groupe d'horaires pour lundi,mardi et mercredi pour garge 1
			GarageOpeningTime garage1OpeningTimeMONDAY = GarageOpeningTime.builder()
					.dayOfWeek(DayOfWeek.MONDAY)
					.openingTimes(Arrays.asList(openingTimeMorningMonday,openingTimeEveningMonday))
					.build();

			GarageOpeningTime garage1OpeningTimeTUESDAY = GarageOpeningTime.builder()
					.dayOfWeek(DayOfWeek.TUESDAY)
					.openingTimes(Arrays.asList(openingTimeMorningTuesday,openingTimeEveningTuesday))
					.build();
			GarageOpeningTime garage1OpeningTimeWEDNESDAY = GarageOpeningTime.builder()
					.dayOfWeek(DayOfWeek.WEDNESDAY)
					.openingTimes(Arrays.asList(openingTimeMorningWensday,openingTimeEveningWensday))
					.build();
			
			 // Associer chaque OpeningTime à son groupe
			openingTimeMorningMonday.setGarageOpeningTime(garage1OpeningTimeMONDAY);
			openingTimeEveningMonday.setGarageOpeningTime(garage1OpeningTimeMONDAY);
			openingTimeEveningTuesday.setGarageOpeningTime(garage1OpeningTimeTUESDAY);
			openingTimeEveningTuesday.setGarageOpeningTime(garage1OpeningTimeTUESDAY);
			openingTimeMorningWensday.setGarageOpeningTime(garage1OpeningTimeWEDNESDAY);
			openingTimeEveningWensday.setGarageOpeningTime(garage1OpeningTimeWEDNESDAY);
			
			 //  Associer les groupes au garage
			garage1.getOpeningTimes().put(DayOfWeek.MONDAY, garage1OpeningTimeMONDAY);
			garage1.getOpeningTimes().put(DayOfWeek.TUESDAY, garage1OpeningTimeTUESDAY);
			garage1.getOpeningTimes().put(DayOfWeek.WEDNESDAY, garage1OpeningTimeWEDNESDAY);
			
			 //  Création du groupe d'horaires pour lundi,mardi  pour garage 2
			OpeningTime openingTimeMorningMondayG2 = OpeningTime.builder()
					.startTime(LocalTime.of(8, 0))
					.endTime(LocalTime.of(12, 0))		
					.build();
			OpeningTime openingTimeEveningMondayG2 = OpeningTime.builder()
					.startTime(LocalTime.of(14, 0))
					.endTime(LocalTime.of(18,0))		
					.build();
			OpeningTime openingTimeMorningTuesdayG2= OpeningTime.builder()
					.startTime(LocalTime.of(8, 0))
					.endTime(LocalTime.of(12, 0))		
					.build();
			OpeningTime openingTimeEveningTuesdayG2 = OpeningTime.builder()
					.startTime(LocalTime.of(14, 0))
					.endTime(LocalTime.of(18,0))		
					.build();
			GarageOpeningTime garage2OpeningTimeMONDAY = GarageOpeningTime.builder()
					.dayOfWeek(DayOfWeek.MONDAY)
					.openingTimes(Arrays.asList(openingTimeMorningMondayG2,openingTimeEveningMondayG2))
					.build();

			GarageOpeningTime garage2OpeningTimeTUESDAY = GarageOpeningTime.builder()
					.dayOfWeek(DayOfWeek.TUESDAY)
					.openingTimes(Arrays.asList(openingTimeMorningTuesdayG2,openingTimeEveningTuesdayG2))
					.build();

			// Associer chaque OpeningTime à son groupe pour garage 2
						openingTimeMorningMondayG2.setGarageOpeningTime(garage2OpeningTimeMONDAY);
						openingTimeEveningMondayG2.setGarageOpeningTime(garage2OpeningTimeMONDAY);
						openingTimeMorningTuesdayG2.setGarageOpeningTime(garage2OpeningTimeTUESDAY);
						openingTimeEveningTuesdayG2.setGarageOpeningTime(garage2OpeningTimeTUESDAY);
						
			garage2.getOpeningTimes().put(DayOfWeek.MONDAY, garage2OpeningTimeMONDAY);
			garage2.getOpeningTimes().put(DayOfWeek.TUESDAY, garage2OpeningTimeTUESDAY);
			
		//  Création du groupe d'horaires pour lundi  pour garage 3
					OpeningTime openingTimeMorningMondayG3 = OpeningTime.builder()
							.startTime(LocalTime.of(8, 0))
							.endTime(LocalTime.of(12, 0))		
							.build();
					OpeningTime openingTimeEveningMondayG3 = OpeningTime.builder()
							.startTime(LocalTime.of(14, 0))
							.endTime(LocalTime.of(18,0))		
							.build();

					GarageOpeningTime garage3OpeningTimeMONDAY = GarageOpeningTime.builder()
							.dayOfWeek(DayOfWeek.MONDAY)
							.openingTimes(Arrays.asList(openingTimeMorningMondayG3,openingTimeEveningMondayG3))
							.build();


					// Associer chaque OpeningTime à son groupe pour garage 2

								openingTimeEveningMondayG3.setGarageOpeningTime(garage3OpeningTimeMONDAY);
								openingTimeEveningMondayG3.setGarageOpeningTime(garage3OpeningTimeMONDAY);

			                    garage3.getOpeningTimes().put(DayOfWeek.MONDAY, garage3OpeningTimeMONDAY);


			// sauvegarder les garages
			// 🔹 Sauvegarde en cascade
			garageRepository.save(garage1);
			garageRepository.save(garage2);
	        garageRepository.save(garage3);
			
			Pageable pageable = PageRequest.of(0, 3, Sort.by("name").ascending());
			Page<GarageDto> pagedGarages = garageService.listGarages(pageable);
			if (pagedGarages != null && pagedGarages.hasContent()) {
			    System.out.println("Liste paginee des garages :");
			    pagedGarages.forEach(garage -> System.out.println(garage.name()));
			} else {
			    System.out.println("Aucun garage trouve.");
			}
	        
		};
	}
}
