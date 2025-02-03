package com.renault.exceptions;

public class GarageLimitVehiculeException extends RuntimeException {

	public GarageLimitVehiculeException(int max) {
		super("Le garage ne peut pas depasser "+max+" vehicules");
		
	}

}
