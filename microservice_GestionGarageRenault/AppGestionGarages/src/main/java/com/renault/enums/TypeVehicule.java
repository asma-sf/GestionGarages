package com.renault.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeVehicule {
	CITADINE,BERLINE,BREAK,SUV,MONOSPACE;
	@JsonCreator
    public static TypeVehicule fromString(String value) {
        if (value == null) {
            return null;
        }
        return TypeVehicule.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
