package com.renault.mappers;


import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.renault.dtos.GarageDto;
import com.renault.dtos.GarageOpeningTimeDto;
import com.renault.dtos.OpeningTimeDto;
import com.renault.entities.Garage;
import com.renault.entities.GarageOpeningTime;
import com.renault.entities.OpeningTime;

@Mapper(componentModel = "spring")
public interface GarageMapper {

    @Mapping(source = "openingTimes", target = "openingTimes", qualifiedByName = "mapOpeningTimes")
    GarageDto toGarageDto(Garage garage);

    @Mapping(source = "openingTimes", target = "openingTimes", qualifiedByName = "mapOpeningTimeDtos")
    Garage toGarage(GarageDto garageDto);

    @Named("mapOpeningTimes")
    default Map<DayOfWeek, List<OpeningTimeDto>> mapOpeningTimes(Map<DayOfWeek, GarageOpeningTime> openingTimes) {
        if (openingTimes == null) return null;
        
        return openingTimes.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getOpeningTimes().stream()
                              .map(this::toOpeningTimeDto)
                              .collect(Collectors.toList())
            ));
    }

    @Named("mapOpeningTimeDtos")
    default Map<DayOfWeek, GarageOpeningTime> mapOpeningTimeDtos(Map<DayOfWeek, List<OpeningTimeDto>> openingTimes) {
        if (openingTimes == null) return null;
        
        return openingTimes.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> GarageOpeningTime.builder()
                    .dayOfWeek(entry.getKey())
                    .openingTimes(entry.getValue().stream()
                        .map(this::toOpeningTime)
                        .collect(Collectors.toList()))
                    .build()
            ));
    }

    OpeningTimeDto toOpeningTimeDto(OpeningTime openingTime);
    OpeningTime toOpeningTime(OpeningTimeDto openingTimeDto);
    
}
