package com.locassa.yamo.repository;

import com.locassa.yamo.model.Notification;
import com.locassa.yamo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findFirst10ByUserAndCreatedLessThanOrderByCreatedDesc(User user, Date from);

    List<Notification> findFirst10ByUserAndCreatedGreaterThanOrderByCreatedAsc(User user, Date from);

    Notification findByUuidAndUser(Long notificationId, User user);

}
