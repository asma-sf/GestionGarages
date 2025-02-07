package com.renault.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;

@Builder
public record OpeningTimeDto(@JsonIgnore DayOfWeek dayOfWeek,LocalTime startTime,LocalTime endTime) {


}
