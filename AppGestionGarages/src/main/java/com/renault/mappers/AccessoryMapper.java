package com.renault.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.renault.dtos.AccessoryDto;
import com.renault.entities.Accessory;

@Mapper(componentModel = "spring")
public interface AccessoryMapper {
	 @Mapping(source = "vehicule.id", target = "vehiculeId") 
	AccessoryDto toAccessoryDto(Accessory accessoryEntity);	
	 @Mapping(source = "vehiculeId", target = "vehicule.id")
	Accessory toAccessoryEntity(AccessoryDto accessoryDto);
}
