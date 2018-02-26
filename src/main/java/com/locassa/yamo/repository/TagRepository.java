package com.locassa.yamo.repository;

import com.locassa.yamo.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query(value = "select * from tag where uuid in (select tags_uuid from venue_tags where venue_uuid = ?1) order by priority desc", nativeQuery = true)
    List<Tag> findTagsByVenueUuid(Long venueId);

}
