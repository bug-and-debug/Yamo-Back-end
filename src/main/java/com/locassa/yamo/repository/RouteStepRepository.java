package com.locassa.yamo.repository;

import com.locassa.yamo.model.RouteStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteStepRepository extends JpaRepository<RouteStep, Long> {
}
