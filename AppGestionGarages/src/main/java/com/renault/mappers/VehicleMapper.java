package com.renault.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.renault.dtos.VehicleDto;
import com.renault.entities.Vehicle;

@Mapper(componentModel = "spring", uses = AccessoryMapper.class)
public interface VehicleMapper {

	@Mapping(source = "garageID", target = "garage.id")
	Vehicle toVehicleEntity(VehicleDto vehicleDto);
	
	@Mapping(source = "garage.id", target = "garageID")
	VehicleDto toVehicleDto(Vehicle vehicle);
}
