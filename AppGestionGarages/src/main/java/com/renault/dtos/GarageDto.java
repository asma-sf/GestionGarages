package com.renault.dtos;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

import com.renault.entities.Vehicle;

import lombok.Builder;



@Builder
public record GarageDto(Long id, String name,String address, String phone, String email,
		List<VehicleDto> vehicles,
		 Map<DayOfWeek, List<OpeningTimeDto>> openingTimes) {

}