package com.renault.exceptions;

public class VehiculeNotFoundException extends ResourceNotFoundException {

	public VehiculeNotFoundException(Long id) {
		super("Vehicule non trouve avec l\'ID: " + id);
		
	}

}
