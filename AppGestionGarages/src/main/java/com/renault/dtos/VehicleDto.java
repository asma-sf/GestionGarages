package com.renault.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renault.enums.TypeCarburant;

import lombok.Builder;

@Builder
public record VehicleDto( Long id,
 String brand,
 String model,
 TypeCarburant typeCarburant,
 int manufacturingYear,
 @JsonIgnore Long garageID,
 List<AccessoryDto> accessories) {

}
