package com.renault.dtos;

import java.time.DayOfWeek;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;

@Builder
public record GarageOpeningTimeDto(@JsonIgnore DayOfWeek dayOfWeek, List<OpeningTimeDto> openingTimes) {

}
