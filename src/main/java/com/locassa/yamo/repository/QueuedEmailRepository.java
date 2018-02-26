package com.locassa.yamo.repository;

import com.locassa.yamo.model.QueuedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface QueuedEmailRepository extends JpaRepository<QueuedEmail, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from queued_email where target_user_id = ?1", nativeQuery = true)
    void deleteByUserId(Long userId);

    List<QueuedEmail> findByScheduledDateLessThanEqual(Date date);

}
