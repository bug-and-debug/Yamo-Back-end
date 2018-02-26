package com.locassa.yamo.task;

import com.locassa.yamo.model.UserSubscription;
import com.locassa.yamo.model.enums.SubscriptionType;
import com.locassa.yamo.model.enums.UserType;
import com.locassa.yamo.repository.UserRepository;
import com.locassa.yamo.repository.UserSubscriptionRepository;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SubscriptionExpiryTask {

    private static final Logger logger = Logger.getLogger(SubscriptionExpiryTask.class);

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedDelay = /*1 **/ 60 * 60 * 1000) // Every hour after process completion.
    public void findExpiredAdvancedSubscriptions() {

        logger.debug("findExpiredSubscriptions");

        findAppleExpiredSubscriptions();
        findAndroidExpiredSubscriptions();

    }

    private void findAppleExpiredSubscriptions() {
        // 1. Find expired subscriptions.
        Date now = YamoUtils.now();
        List<UserSubscription> lstSubscriptions = userSubscriptionRepository.findByTypeAndEndDateLessThan(SubscriptionType.APPLE, now);

        // 2. For each, check receipt against apple.
        if (null != lstSubscriptions) {
            Date subscriptionStart = null;
            Date subscriptionEnd = null;
            String updatedReceiptJson = null;
            String receiptUniqueIdentifier = null;
            Map<String, Object> responseReceiptMap = null;
            List<Long> lstSubscriptionIdsToDelete = new ArrayList<>();
            List<Long> lstUserIdsToDowngrade = new ArrayList<>();
            List<UserSubscription> lstSubscriptionsToUpdate = new ArrayList<>();
            for (UserSubscription userSubscription : lstSubscriptions) {
                responseReceiptMap = YamoUtils.getAppleInAppPurchaseReceiptResponse(userSubscription.getReceiptData(), YamoUtils.APPLE_INAPP_PURCHASE_SHARED_SECRET);
                if (null == responseReceiptMap) {
                    // TODO in case of error.
                } else {
                    try {
                        ArrayList receipts = (ArrayList) responseReceiptMap.get("latest_receipt_info");
                        if (null != receipts) {
                            HashMap updatedReceiptMap = (HashMap) receipts.get(receipts.size() - 1);
                            subscriptionStart = new Date(Long.parseLong((String) updatedReceiptMap.get("purchase_date_ms")));
                            subscriptionEnd = new Date(Long.parseLong((String) updatedReceiptMap.get("expires_date_ms")));
                            receiptUniqueIdentifier = String.valueOf(updatedReceiptMap.get("original_transaction_id"));
                            updatedReceiptJson = YamoUtils.fromMapToJsonString(updatedReceiptMap);

                            if (subscriptionEnd.before(YamoUtils.now())) {
                                // Delete
                                lstSubscriptionIdsToDelete.add(userSubscription.getUuid());
                                lstUserIdsToDowngrade.add(userSubscription.getAssociatedUser().getUuid());
                            } else {
                                // Update
                                userSubscription.setStartDate(subscriptionStart);
                                userSubscription.setEndDate(subscriptionEnd);
                                userSubscription.setLatestAppleReceipt(updatedReceiptJson);
                                userSubscription.setUniqueIdentifier(receiptUniqueIdentifier);
                                userSubscription.setReceiptData((String) responseReceiptMap.get("latest_receipt"));
                                lstSubscriptionsToUpdate.add(userSubscription);
                            }

                        }

                    } catch (Exception e) {
                        continue;
                    }
                }
            }


            // 3. Delete passed subscriptions.
            if (!lstSubscriptionIdsToDelete.isEmpty()) {
                userSubscriptionRepository.deleteSubscriptionsById(lstSubscriptionIdsToDelete);
            }

            // 4. Downgrade users to Enthusiast.
            if (!lstUserIdsToDowngrade.isEmpty()) {
                userRepository.updateUserTypeFromTo(lstUserIdsToDowngrade, UserType.USER.getValue(), UserType.GUEST.getValue());
            }

            // 5. Update user subscriptions.
            if (!lstSubscriptionsToUpdate.isEmpty()) {
                userSubscriptionRepository.save(lstSubscriptionsToUpdate);
            }
        }
    }

    private void findAndroidExpiredSubscriptions() {

    }

}
