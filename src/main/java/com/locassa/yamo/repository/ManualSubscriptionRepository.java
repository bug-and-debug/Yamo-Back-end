package com.locassa.yamo.repository;

import com.locassa.yamo.model.ManualSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ManualSubscriptionRepository extends JpaRepository<ManualSubscription, Long> {

    List<ManualSubscription> findByEndDateLessThan(Date now);

}
