package com.locassa.yamo.task;

import com.locassa.yamo.model.ManualSubscription;
import com.locassa.yamo.model.enums.UserType;
import com.locassa.yamo.repository.ManualSubscriptionRepository;
import com.locassa.yamo.repository.UserRepository;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ManualSubscriptionTask {

    private static final Logger logger = Logger.getLogger(ManualSubscriptionTask.class);

    @Autowired
    private ManualSubscriptionRepository manualSubscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 6 * * ?") // Everyday at 6am
    public void findManualSubscriptions() {

        logger.debug("findManualSubscriptions");

        // 1. Find passed manual subscriptions.
        List<ManualSubscription> lstPassedSubscriptions = manualSubscriptionRepository.findByEndDateLessThan(YamoUtils.now());

        // 2. Change user types.
        if (null != lstPassedSubscriptions && !lstPassedSubscriptions.isEmpty()) {
            for (ManualSubscription ms : lstPassedSubscriptions) {
                userRepository.updateUserTypeFromTo(
                        Collections.singletonList(ms.getUserUuid()),
                        UserType.USER.getValue(),
                        UserType.GUEST.getValue()
                );
                manualSubscriptionRepository.delete(ms);
            }
        }

    }

}
