package com.locassa.yamo.repository;

import com.locassa.yamo.model.ArtWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ArtWorkRepository extends JpaRepository<ArtWork, Long> {

    List<ArtWork> findFirst10ByOrderByCreatedDesc();

    List<ArtWork> findFirst10ByCreatedLessThanOrderByCreatedDesc(Date created);

    List<ArtWork> findFirst10ByUuidNotInOrderByCreatedDesc(List<Long> exclude);

    List<ArtWork> findFirst10ByUuidNotInAndCreatedLessThanOrderByCreatedDesc(List<Long> exclude, Date created);

    List<ArtWork> findFirst10ByUuidInOrderByCreatedDesc(List<Long> include);

    List<ArtWork> findFirst10ByUuidInAndCreatedLessThanOrderByCreatedDesc(List<Long> include, Date created);


}
