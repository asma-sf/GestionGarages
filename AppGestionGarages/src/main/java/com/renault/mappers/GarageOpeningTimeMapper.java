package com.renault.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.renault.dtos.GarageOpeningTimeDto;
import com.renault.dtos.OpeningTimeDto;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;

/*@Mapper(componentModel = "spring")
public interface GarageOpeningTimeMapper {

	 @Mapping(source = "openingTimes", target = "openingTimes") 
	GarageOpeningTimeDto toGarageOpeningTimeDto(GarageOpeningTime garageOpeningTime);
	 
	 @Mapping(source = "openingTimes", target = "openingTimes") 
	 GarageOpeningTime toGarageOpeningTimeEntity(GarageOpeningTimeDto garageOpeningTimeDto);
	 
	 List<GarageOpeningTimeDto> toGarageOpeningTimeDtoList(List<GarageOpeningTimeDto> garageOpeningTimeEntities);
	 List<GarageOpeningTime> toGarageOpeningTimeEntityList(List<GarageOpeningTime> garageOpeningTimeDtos);
	 
	 OpeningTimeDto toOpeningTimeDto(OpeningTime openingTime);
	 OpeningTime toOpeningTimeEntity(OpeningTimeDto openingTimeDto);
	 
	 List<OpeningTimeDto> toOpeningTimeDtoList(List<OpeningTime> openingTimes);
	 List<OpeningTime> toOpeningTimesEntityList(List<OpeningTimeDto> openingTimeDtos);
	 
}*/
@Mapper(componentModel = "spring")
public interface GarageOpeningTimeMapper {

    @Mapping(source = "openingTimes", target = "openingTimes") 
    GarageOpeningTimeDto toDto(GarageOpeningTime entity);

    @Mapping(source = "openingTimes", target = "openingTimes")
    GarageOpeningTime toEntity(GarageOpeningTimeDto dto);

    List<GarageOpeningTimeDto> toDtoList(List<GarageOpeningTime> entities);
    List<GarageOpeningTime> toEntityList(List<GarageOpeningTimeDto> dtos);

    OpeningTimeDto toOpeningTimeDto(OpeningTime openingTime);
    OpeningTime toOpeningTimeEntity(OpeningTimeDto openingTimeDto);

    List<OpeningTimeDto> toOpeningTimeDtoList(List<OpeningTime> openingTimes);
    List<OpeningTime> toOpeningTimeEntityList(List<OpeningTimeDto> openingTimeDtos);
}

