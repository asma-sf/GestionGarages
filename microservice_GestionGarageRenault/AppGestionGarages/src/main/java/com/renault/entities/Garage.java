package com.renault.entities;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Garage {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String address;
	private String telephone;
	private String email;
	
	@OneToMany(mappedBy = "garage",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Vehicule> vehicules;

	@OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
	private Map<DayOfWeek, GarageOpeningTime> garageOpeningTimes = new HashMap<>();
	
}
