package com.renault.exceptions;

public class AccessoireNotFoundException extends ResourceNotFoundException {

	public AccessoireNotFoundException(Long id) {
		super("Accessoire non trouve avec l\'ID: " + id);
		
	}

}
