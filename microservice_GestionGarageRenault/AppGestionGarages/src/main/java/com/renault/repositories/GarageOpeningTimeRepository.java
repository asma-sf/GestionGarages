package com.renault.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.renault.entities.GarageOpeningTime;

@Repository
public interface GarageOpeningTimeRepository extends JpaRepository<GarageOpeningTime, Long> {

}
