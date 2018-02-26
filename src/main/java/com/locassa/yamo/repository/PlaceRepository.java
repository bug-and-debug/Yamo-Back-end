package com.locassa.yamo.repository;

import com.locassa.yamo.model.Place;
import com.locassa.yamo.model.User;
import com.locassa.yamo.model.enums.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByUser(User user);

    List<Place> findByUserAndPlaceType(User user, PlaceType type);

}
