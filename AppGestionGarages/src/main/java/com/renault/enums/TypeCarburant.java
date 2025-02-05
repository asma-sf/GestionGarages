package com.renault.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeCarburant {
	ESSENCE,GASOIL,ELECTRIC,HYBRID;
	// si on ecrit le type en min, on ajoute ce code
	/*@JsonCreator
    public static TypeCarburant fromString(String value) {
        if (value == null) {
            return null;
        }
        return TypeCarburant.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name();
    }*/
}
