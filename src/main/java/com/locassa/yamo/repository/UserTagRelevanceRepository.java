package com.locassa.yamo.repository;

import com.locassa.yamo.model.UserTag;
import com.locassa.yamo.model.UserTagRelevance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTagRelevanceRepository extends JpaRepository<UserTagRelevance, Long> {

    @Query(value = "select * from user_tag where user_uuid = ?1", nativeQuery = true)
    List<UserTagRelevance> findByUserId(Long userId);

}
