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
import jakarta.persistence.MapKey;
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
	private String phone;
	private String email;
	
	@OneToMany(mappedBy = "garage",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Vehicle> vehicles;

	@OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKey(name = "dayOfWeek")
	//private Map<DayOfWeek, GarageOpeningTime> garageOpeningTimes = new HashMap<>();
	private Map<DayOfWeek, GarageOpeningTime> openingTimes = new HashMap<>();
	
}
