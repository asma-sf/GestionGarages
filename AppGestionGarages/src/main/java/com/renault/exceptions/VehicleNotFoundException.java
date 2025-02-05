package com.renault.exceptions;

public class VehicleNotFoundException extends ResourceNotFoundException {

	public VehicleNotFoundException(Long id) {
		super("Vehicule non trouve avec l\'ID: " + id);
		
	}

}
