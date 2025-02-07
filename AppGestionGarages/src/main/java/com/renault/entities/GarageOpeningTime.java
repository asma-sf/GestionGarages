package com.renault.entities;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class GarageOpeningTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;
	@Enumerated(EnumType.STRING)
	@JsonIgnore
	private DayOfWeek dayOfWeek; 

	//@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	@OneToMany(mappedBy = "garageOpeningTime", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OpeningTime> openingTimes = new ArrayList<>();
	
	
	
}
