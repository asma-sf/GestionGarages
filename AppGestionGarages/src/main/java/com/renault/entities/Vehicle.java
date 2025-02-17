package com.renault.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renault.enums.TypeCarburant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
public class Vehicle {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String brand;
	private String model;
	@Enumerated(EnumType.STRING)
	private TypeCarburant typeCarburant; // enum
	private int manufacturingYear;
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Garage garage;
	@OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Accessory> accessories;
}
