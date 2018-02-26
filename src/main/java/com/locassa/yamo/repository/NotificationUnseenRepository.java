package com.locassa.yamo.repository;

import com.locassa.yamo.model.NotificationUnseen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationUnseenRepository extends JpaRepository<NotificationUnseen, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from notification_unseen where user_uuid = ?1", nativeQuery = true)
    void deleteByUserId(Long userId);

    @Transactional
    @Modifying
    @Query(value = "delete from notification_unseen where notification_uuid in (select n.uuid from notification n where n.associated_item = ?1 and n.type = ?2 and n.user_uuid = ?3)", nativeQuery = true)
    void deleteByItemIdAndTypeAndUserId(Long itemId, int typeValue, Long userId);

    @Transactional
    @Modifying
    @Query(value = "delete from notification_unseen where notification_uuid in (select n.uuid from notification n where n.uuid = ?1 and n.user_uuid = ?2)", nativeQuery = true)
    void deleteByUuidAndUserId(Long notificationId, Long userId);

    @Query(value = "select coalesce(count(*), 0) from notification_unseen where user_uuid = ?1", nativeQuery = true)
    long countByUserId(Long userId);

}
