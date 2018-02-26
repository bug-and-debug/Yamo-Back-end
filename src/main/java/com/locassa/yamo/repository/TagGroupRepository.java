package com.locassa.yamo.repository;

import com.locassa.yamo.model.TagGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagGroupRepository extends JpaRepository<TagGroup, Long> {
}
