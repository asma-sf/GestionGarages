package com.renault.exceptions;

public class GarageNotFoundException extends ResourceNotFoundException {

	public GarageNotFoundException(Long id) {
		super("Garage non trouve avec l\'ID: " + id);
		
	}

}
