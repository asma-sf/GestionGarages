package com.renault.exceptions;

public class AccessoryNotFoundException extends ResourceNotFoundException {

	public AccessoryNotFoundException(Long id) {
		super("Accessoire non trouve avec l\'ID: " + id);
		
	}

}
