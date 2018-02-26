package com.locassa.yamo.task;

import com.locassa.yamo.model.User;
import com.locassa.yamo.model.Venue;
import com.locassa.yamo.model.enums.NotificationType;
import com.locassa.yamo.repository.UserRepository;
import com.locassa.yamo.repository.VenueRepository;
import com.locassa.yamo.service.aws.AwsSNSService;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationReminderTask {

    private static final Logger logger = Logger.getLogger(NotificationReminderTask.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AwsSNSService awsSNSService;

    @Autowired
    private VenueRepository venueRepository;

    @Scheduled(cron = "0 0 12 * * ?") // Everyday at noon
    public void triggerGetToKnowMeReminder() {

        logger.debug("triggerGetToKnowMeReminder");

        // 1. Find users whose latest replies were 7 days ago.
        List<User> lstUsers = userRepository.findUsersForGetToKnowMeReminder(YamoUtils.DEFAULT_DAYS_FOR_GET_TO_KNOW_ME_REMINDER);

        if (null != lstUsers && !lstUsers.isEmpty()) {

            awsSNSService.sendNotification(
                    NotificationType.GET_TO_KNOW_ME_REMINDER,
                    null,
                    lstUsers,
                    null,
                    true,
                    true,
                    ""
            );

        }

    }

    @Scheduled(cron = "0 0 11 * * ?") // Everyday at 11am
    public void triggerExhibitionEndingSoonReminder() {

        logger.debug("triggerExhibitionEndingSoonReminder");

        // 1. Find exhibitions ending soon.
        List<Venue> lstExhibitions = venueRepository.findExhibitionsEndingSoon(YamoUtils.DEFAULT_DAYS_FOR_EXHIBITIONS_ENDING_SOON);

        // 2. For each exhibition, find users and send pushes.
        if (null != lstExhibitions && !lstExhibitions.isEmpty()) {
            List<User> lstTempTargetUsers;
            for (Venue exhibition : lstExhibitions) {

                lstTempTargetUsers = userRepository.findUsersWhoFavouritedExhibition(exhibition.getUuid());

                if (null != lstTempTargetUsers && !lstTempTargetUsers.isEmpty()) {

                    awsSNSService.sendNotification(
                            NotificationType.EXHIBITION_CLOSING_SOON,
                            null,
                            lstTempTargetUsers,
                            exhibition,
                            true,
                            true,
                            ""
                    );

                }

            }
        }

    }

}
