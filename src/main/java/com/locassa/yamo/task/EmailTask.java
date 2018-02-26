package com.locassa.yamo.task;

import com.locassa.yamo.model.QueuedEmail;
import com.locassa.yamo.repository.QueuedEmailRepository;
import com.locassa.yamo.service.aws.AwsSESService;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailTask {

    private static final Logger logger = Logger.getLogger(EmailTask.class);

    @Autowired
    private QueuedEmailRepository queuedEmailRepository;

    @Autowired
    private AwsSESService awsSESService;

    @Scheduled(fixedDelay = 5 * 60 * 1000) // Every 5 minutes after process completion.
    public void deliverQueuedEmails() {

        List<QueuedEmail> lstQueuedEmails = queuedEmailRepository.findByScheduledDateLessThanEqual(YamoUtils.now());
        if (null != lstQueuedEmails && !lstQueuedEmails.isEmpty()) {
            for (QueuedEmail queuedEmail : lstQueuedEmails) {
                try {
                    awsSESService.sendEmail(queuedEmail.getTargetUser(), queuedEmail.getSubject(), queuedEmail.getHtmlContent(), false);
                    queuedEmailRepository.delete(queuedEmail);
                } catch (Exception e) {
                    logger.error("Could not deliver queued email.", e);
                }
            }
        }

    }

}
