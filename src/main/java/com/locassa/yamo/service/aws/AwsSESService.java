package com.locassa.yamo.service.aws;

import com.locassa.yamo.model.QueuedEmail;
import com.locassa.yamo.model.User;
import com.locassa.yamo.repository.QueuedEmailRepository;
import com.locassa.yamo.service.FreeMarkerService;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AwsSESService {

    private static final Logger logger = Logger.getLogger(AwsSESService.class);

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private QueuedEmailRepository queuedEmailRepository;

    @Autowired
    private FreeMarkerService freeMarkerService;

    private void queueEmail(final User targetUser, final String subject, final String htmlContent, final Date scheduledDate) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {

                    QueuedEmail queuedEmail = new QueuedEmail();
                    queuedEmail.setTargetUser(targetUser);
                    queuedEmail.setSubject(subject);
                    queuedEmail.setHtmlContent(htmlContent);
                    queuedEmail.setScheduledDate(scheduledDate);
                    queuedEmailRepository.save(queuedEmail);

                } catch (Exception e) {

                    logger.error("Could not queue email.", e);

                }

            }
        };

        Thread queue = new Thread(runnable);
        queue.start();

    }

    public void sendEmail(final User targetUser, final String subject, final String htmlContent, final boolean queueIfError) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    @Override
                    public void prepare(javax.mail.internet.MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                        message.setFrom(mailSender.getJavaMailProperties().getProperty("mail.from"));
                        message.setTo(targetUser.getEmail());
                        message.setSubject(subject);
                        message.setText(htmlContent, true);
                    }
                };

                try {

                    mailSender.send(preparator);

                } catch (Exception e) {
                    logger.error("Could not send email.", e);
                    if (queueIfError) {
                        logger.info("Trying to queue email...");
                        queueEmail(targetUser, subject, htmlContent, YamoUtils.now());
                    }
                }

            }
        };

        Thread email = new Thread(runnable);
        email.start();

    }

    public void sendRecoverPasswordEmail(User user) {

        String subject = "Recover your password!";
        String htmlContent = freeMarkerService.recoverPasswordHtmlContent(user);

        sendEmail(user, subject, htmlContent, true);
    }

}
