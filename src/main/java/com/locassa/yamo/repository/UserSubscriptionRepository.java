package com.locassa.yamo.repository;

import com.locassa.yamo.model.User;
import com.locassa.yamo.model.UserSubscription;
import com.locassa.yamo.model.enums.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    UserSubscription findByAssociatedUser(User user);

    List<UserSubscription> findByTypeAndEndDateLessThan(SubscriptionType type, Date now);

    @Transactional
    @Modifying
    @Query(value = "delete from user_subscription where uuid in (?1)", nativeQuery = true)
    void deleteSubscriptionsById(List<Long> lstSubscriptionIds);

    @Query(value = "select * from user_subscription where unique_identifier = ?1 limit 1", nativeQuery = true)
    UserSubscription findSubscriptionByUniqueIdentifier(String uniqueIdentifier);

    @Query(value = "select * from user_subscription where purchase_token = ?1 limit 1", nativeQuery = true)
    UserSubscription findSubscriptionByPurchaseToken(String purchaseToken);

}
