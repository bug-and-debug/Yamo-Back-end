package com.locassa.yamo.repository;

import com.locassa.yamo.model.summary.RouteSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteSummaryRepository extends JpaRepository<RouteSummary, Long> {

    @Query(value = "select r.uuid, r.name, r.created from route r where r.user_uuid = ?1 order by r.name asc", nativeQuery = true)
    List<RouteSummary> findRoutesForProfile(Long userId);

    @Query(value = "select r.uuid, r.name, r.created from route r where r.user_uuid = ?1 and r.counter >= 3 order by r.counter desc limit 2", nativeQuery = true)
    List<RouteSummary> findPopularRoutesForProfile(Long userId);

}
