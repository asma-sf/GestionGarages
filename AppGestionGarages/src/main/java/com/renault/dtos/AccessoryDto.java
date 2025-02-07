package com.renault.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;

@Builder
public record AccessoryDto(Long id, String name,
		String description, double price, String type,
		@JsonIgnore Long vehiculeId) {

}
