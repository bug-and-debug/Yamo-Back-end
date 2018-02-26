package com.locassa.yamo.repository;

import com.locassa.yamo.model.Device;
import com.locassa.yamo.model.User;
import com.locassa.yamo.model.enums.NotificationPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByUser(User user);

    Device findByUserAndPlatformAndToken(User user, NotificationPlatform platform, String token);

}
