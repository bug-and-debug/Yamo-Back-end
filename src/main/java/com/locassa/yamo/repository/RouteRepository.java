package com.locassa.yamo.repository;

import com.locassa.yamo.model.Route;
import com.locassa.yamo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Route findByUuidAndUser(Long routeId, User user);

}
