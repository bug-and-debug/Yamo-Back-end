package com.locassa.yamo.service.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locassa.yamo.model.*;
import com.locassa.yamo.model.enums.NotificationPlatform;
import com.locassa.yamo.model.enums.NotificationType;
import com.locassa.yamo.repository.DeviceRepository;
import com.locassa.yamo.repository.NotificationRepository;
import com.locassa.yamo.repository.NotificationUnseenRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

public class AwsSNSService {

    private static final Logger logger = Logger.getLogger(AwsSNSService.class);

    private static final String APPLE_CERTIFICATE_DEV = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFkTCCBHmgAwIBAgIIEBMfJaXcRM4wDQYJKoZIhvcNAQEFBQAwgZYxCzAJBgNV\n" +
            "BAYTAlVTMRMwEQYDVQQKDApBcHBsZSBJbmMuMSwwKgYDVQQLDCNBcHBsZSBXb3Js\n" +
            "ZHdpZGUgRGV2ZWxvcGVyIFJlbGF0aW9uczFEMEIGA1UEAww7QXBwbGUgV29ybGR3\n" +
            "aWRlIERldmVsb3BlciBSZWxhdGlvbnMgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkw\n" +
            "HhcNMTYwNjIxMDgyNTAzWhcNMTcwNjIxMDgyNTAzWjCBkDEmMCQGCgmSJomT8ixk\n" +
            "AQEMFmNvbS5ncmFuZGFwcHMueWFtby5pb3MxRDBCBgNVBAMMO0FwcGxlIERldmVs\n" +
            "b3BtZW50IElPUyBQdXNoIFNlcnZpY2VzOiBjb20uZ3JhbmRhcHBzLnlhbW8uaW9z\n" +
            "MRMwEQYDVQQLDAoyOFE2VE5EM1JMMQswCQYDVQQGEwJVUzCCASIwDQYJKoZIhvcN\n" +
            "AQEBBQADggEPADCCAQoCggEBAJj+PdLaBopZZZbgqCzglPjodS7RT+k3oILOGjbV\n" +
            "UFUfgKadyNUHJHURxfzD05JSV1nM/XphoDPSH+Yxugl8PMpwT33JCIj2wP5V8YH2\n" +
            "FmJrI2Nw0t8oeTxZ29AKueatO2OujkDo+7SoeJap6NhZW/JmJ4Plqzg6n2buLgaI\n" +
            "Sr9HuScJs14A2bF4bBdlgEKELdHLwiyfREjiUNuPWo1xJlrJBDeaO/kgBUAj+lBK\n" +
            "ZNpfXNL/IDxxtiE57kW4Y/7xUiM27kIIa5hIjZb9s0d6g0cp6OQu+Euh37ooL0/g\n" +
            "Cf1fzTe8IZf5FlgCyDKcMUMIkLEQERyMW5mn1T1xUwkI8hkCAwEAAaOCAeUwggHh\n" +
            "MB0GA1UdDgQWBBTOiQdOtgr+RJ/sgB/RsvMwQFUuqjAJBgNVHRMEAjAAMB8GA1Ud\n" +
            "IwQYMBaAFIgnFwmpthhgi+zruvZHWcVSVKO3MIIBDwYDVR0gBIIBBjCCAQIwgf8G\n" +
            "CSqGSIb3Y2QFATCB8TCBwwYIKwYBBQUHAgIwgbYMgbNSZWxpYW5jZSBvbiB0aGlz\n" +
            "IGNlcnRpZmljYXRlIGJ5IGFueSBwYXJ0eSBhc3N1bWVzIGFjY2VwdGFuY2Ugb2Yg\n" +
            "dGhlIHRoZW4gYXBwbGljYWJsZSBzdGFuZGFyZCB0ZXJtcyBhbmQgY29uZGl0aW9u\n" +
            "cyBvZiB1c2UsIGNlcnRpZmljYXRlIHBvbGljeSBhbmQgY2VydGlmaWNhdGlvbiBw\n" +
            "cmFjdGljZSBzdGF0ZW1lbnRzLjApBggrBgEFBQcCARYdaHR0cDovL3d3dy5hcHBs\n" +
            "ZS5jb20vYXBwbGVjYS8wTQYDVR0fBEYwRDBCoECgPoY8aHR0cDovL2RldmVsb3Bl\n" +
            "ci5hcHBsZS5jb20vY2VydGlmaWNhdGlvbmF1dGhvcml0eS93d2RyY2EuY3JsMAsG\n" +
            "A1UdDwQEAwIHgDATBgNVHSUEDDAKBggrBgEFBQcDAjAQBgoqhkiG92NkBgMBBAIF\n" +
            "ADANBgkqhkiG9w0BAQUFAAOCAQEAt9em+VUQwVaJgtZKUEpGWq40L0AZ8I01pae2\n" +
            "qhp/Y1aO4nrR49JLZiAkw/TuY04IRr+8LfdBxbreC0tkQr4wZuuX/D08ewEuyKeR\n" +
            "lPp27xVH+qwbaFVuduXIOIifA2V9EhsapdvNvKed9ED3PhizeGmDTKr0sUyCGkth\n" +
            "SBZfCL1N8+XcQwVXiQDc5RcUf7syGxhNWVdCqLTr+xmuWYA9fcqCWhu9NwwwqmfI\n" +
            "lnyjBSoZfd5rX1O93Zf7VnzV2Vxq8x0p8wsl0p0JO8xWDCeDIDmoaoDNkqPFC73+\n" +
            "XrUAZi0zPAmaXZbM7iAkTWkCD/ijJ3zTDt6Ua0+bXw/Nupk/jQ==\n" +
            "-----END CERTIFICATE-----";
    private static final String APPLE_PRIVATE_KEY_DEV = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpQIBAAKCAQEAmP490toGilllluCoLOCU+Oh1LtFP6Teggs4aNtVQVR+App3I\n" +
            "1QckdRHF/MPTklJXWcz9emGgM9If5jG6CXw8ynBPfckIiPbA/lXxgfYWYmsjY3DS\n" +
            "3yh5PFnb0Aq55q07Y66OQOj7tKh4lqno2Flb8mYng+WrODqfZu4uBohKv0e5Jwmz\n" +
            "XgDZsXhsF2WAQoQt0cvCLJ9ESOJQ249ajXEmWskEN5o7+SAFQCP6UEpk2l9c0v8g\n" +
            "PHG2ITnuRbhj/vFSIzbuQghrmEiNlv2zR3qDRyno5C74S6HfuigvT+AJ/V/NN7wh\n" +
            "l/kWWALIMpwxQwiQsRARHIxbmafVPXFTCQjyGQIDAQABAoIBAFdIbOL3KdYSLGgI\n" +
            "YUnBEpxymjAMkCeAad1WUhRIXF1D7LShvery+TSk4CxVWEXZHiaIUXBZc/k8fqBo\n" +
            "bDdvHotryZ38bBcfl3lCdNbtG1Yow3a+7j8MKyZoKYSmKerVl9VFx7h2cutdVfa2\n" +
            "lrWfPVqsNw0DoUwh/cDOT4nAhcxb7/ilmWmIgQhn8kAmuUvLKQx8fOMYm947j/CO\n" +
            "KXaojBK7Uj9vtW/zXuREgYUr5iem9KUHmilu6KbfV/4pexqTreodCKyr5HnnPAcG\n" +
            "7D+Prbzq29ZofejDxW4hBzJKPlqIjANfclsLOpAHixLzaQWfV76GIUbp7YPdFvcD\n" +
            "8BclltUCgYEAy6tdYFfCWkunuLza904GHzKaNe9Oyqq0bE3Mp3qA6yE6fryStrCE\n" +
            "4xg551XwUBbs8mVunTwKpPTApaqw1Ln7UOADUnoobsXaSwER/woVI8GDA1Jzvh5H\n" +
            "w4dslLQ1UAQ5nfejKNDQ29p70iGLA2ve9NYILRngSGNs/ZjYfRdf1CsCgYEAwE2T\n" +
            "X6m1WYVmeQfkWZkd8OGVp34/TZ7eLDoFmAsjwaDTieREXQTl4enioEmDZK1WYeIT\n" +
            "u12DeXQ6yuY2ZA7YIp9HjhpEXpV7NUe+JkdvIMJOCfc6YSXs3bF1p0eR4aHYNvzr\n" +
            "TDmbQnjbWxU7y2ijB6JTTBxlCsHecbSAy6AFHMsCgYEAn83fwF5bkrnV6e/U50KD\n" +
            "LytnMHaKnh5+3pPUxnwqd1NlMFgJzeqG+iDiHg+iNVUnqbHIrvqTZ4bbOaHKib87\n" +
            "3+NE+Av9eZ0ogL1gP3rLx3hoscyaIExmBpdVrQAQ7K3D/5x0muvtVPQDP4cuKgsT\n" +
            "r4vYcnrhLitFM14gT1ZtzG0CgYEAl68ZJgYJI9nToHgXKI1cTprBnuI4MJO1j5Ec\n" +
            "xHdU9vBSq5vASNcKNQ51UhVqbMlFL+RRyCLVB8IkddLs6DPFTFny6SXS9ABYtQgn\n" +
            "9q21/FMHl1jJVcPCeIP318DeUumEUKtjNjbiPqZx8ABpr+KTZfwmVAy5rqiJO4sz\n" +
            "ZGqOXc0CgYEAhRsqNpzhPH8C65GKUymjJOctlKXfFyutcunxRo+YLc5ACmlWmAAy\n" +
            "EUfcIfACZco3MEAVt1bSoo85qND3i4IJcsZMuUD9A2wlYAca0HpHWCcpYX3JmdTN\n" +
            "+LZQv9qLIPxRvTAt8FzUnnO50lhWL6rTrw0KRLmvkbGkSiZfEynO25I=\n" +
            "-----END RSA PRIVATE KEY-----";
    private static final String APPLE_CERTIFICATE_PROD = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFkTCCBHmgAwIBAgIIEBMfJaXcRM4wDQYJKoZIhvcNAQEFBQAwgZYxCzAJBgNV\n" +
            "BAYTAlVTMRMwEQYDVQQKDApBcHBsZSBJbmMuMSwwKgYDVQQLDCNBcHBsZSBXb3Js\n" +
            "ZHdpZGUgRGV2ZWxvcGVyIFJlbGF0aW9uczFEMEIGA1UEAww7QXBwbGUgV29ybGR3\n" +
            "aWRlIERldmVsb3BlciBSZWxhdGlvbnMgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkw\n" +
            "HhcNMTYwNjIxMDgyNTAzWhcNMTcwNjIxMDgyNTAzWjCBkDEmMCQGCgmSJomT8ixk\n" +
            "AQEMFmNvbS5ncmFuZGFwcHMueWFtby5pb3MxRDBCBgNVBAMMO0FwcGxlIERldmVs\n" +
            "b3BtZW50IElPUyBQdXNoIFNlcnZpY2VzOiBjb20uZ3JhbmRhcHBzLnlhbW8uaW9z\n" +
            "MRMwEQYDVQQLDAoyOFE2VE5EM1JMMQswCQYDVQQGEwJVUzCCASIwDQYJKoZIhvcN\n" +
            "AQEBBQADggEPADCCAQoCggEBAJj+PdLaBopZZZbgqCzglPjodS7RT+k3oILOGjbV\n" +
            "UFUfgKadyNUHJHURxfzD05JSV1nM/XphoDPSH+Yxugl8PMpwT33JCIj2wP5V8YH2\n" +
            "FmJrI2Nw0t8oeTxZ29AKueatO2OujkDo+7SoeJap6NhZW/JmJ4Plqzg6n2buLgaI\n" +
            "Sr9HuScJs14A2bF4bBdlgEKELdHLwiyfREjiUNuPWo1xJlrJBDeaO/kgBUAj+lBK\n" +
            "ZNpfXNL/IDxxtiE57kW4Y/7xUiM27kIIa5hIjZb9s0d6g0cp6OQu+Euh37ooL0/g\n" +
            "Cf1fzTe8IZf5FlgCyDKcMUMIkLEQERyMW5mn1T1xUwkI8hkCAwEAAaOCAeUwggHh\n" +
            "MB0GA1UdDgQWBBTOiQdOtgr+RJ/sgB/RsvMwQFUuqjAJBgNVHRMEAjAAMB8GA1Ud\n" +
            "IwQYMBaAFIgnFwmpthhgi+zruvZHWcVSVKO3MIIBDwYDVR0gBIIBBjCCAQIwgf8G\n" +
            "CSqGSIb3Y2QFATCB8TCBwwYIKwYBBQUHAgIwgbYMgbNSZWxpYW5jZSBvbiB0aGlz\n" +
            "IGNlcnRpZmljYXRlIGJ5IGFueSBwYXJ0eSBhc3N1bWVzIGFjY2VwdGFuY2Ugb2Yg\n" +
            "dGhlIHRoZW4gYXBwbGljYWJsZSBzdGFuZGFyZCB0ZXJtcyBhbmQgY29uZGl0aW9u\n" +
            "cyBvZiB1c2UsIGNlcnRpZmljYXRlIHBvbGljeSBhbmQgY2VydGlmaWNhdGlvbiBw\n" +
            "cmFjdGljZSBzdGF0ZW1lbnRzLjApBggrBgEFBQcCARYdaHR0cDovL3d3dy5hcHBs\n" +
            "ZS5jb20vYXBwbGVjYS8wTQYDVR0fBEYwRDBCoECgPoY8aHR0cDovL2RldmVsb3Bl\n" +
            "ci5hcHBsZS5jb20vY2VydGlmaWNhdGlvbmF1dGhvcml0eS93d2RyY2EuY3JsMAsG\n" +
            "A1UdDwQEAwIHgDATBgNVHSUEDDAKBggrBgEFBQcDAjAQBgoqhkiG92NkBgMBBAIF\n" +
            "ADANBgkqhkiG9w0BAQUFAAOCAQEAt9em+VUQwVaJgtZKUEpGWq40L0AZ8I01pae2\n" +
            "qhp/Y1aO4nrR49JLZiAkw/TuY04IRr+8LfdBxbreC0tkQr4wZuuX/D08ewEuyKeR\n" +
            "lPp27xVH+qwbaFVuduXIOIifA2V9EhsapdvNvKed9ED3PhizeGmDTKr0sUyCGkth\n" +
            "SBZfCL1N8+XcQwVXiQDc5RcUf7syGxhNWVdCqLTr+xmuWYA9fcqCWhu9NwwwqmfI\n" +
            "lnyjBSoZfd5rX1O93Zf7VnzV2Vxq8x0p8wsl0p0JO8xWDCeDIDmoaoDNkqPFC73+\n" +
            "XrUAZi0zPAmaXZbM7iAkTWkCD/ijJ3zTDt6Ua0+bXw/Nupk/jQ==\n" +
            "-----END CERTIFICATE-----";
    private static final String APPLE_PRIVATE_KEY_PROD = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpQIBAAKCAQEAmP490toGilllluCoLOCU+Oh1LtFP6Teggs4aNtVQVR+App3I\n" +
            "1QckdRHF/MPTklJXWcz9emGgM9If5jG6CXw8ynBPfckIiPbA/lXxgfYWYmsjY3DS\n" +
            "3yh5PFnb0Aq55q07Y66OQOj7tKh4lqno2Flb8mYng+WrODqfZu4uBohKv0e5Jwmz\n" +
            "XgDZsXhsF2WAQoQt0cvCLJ9ESOJQ249ajXEmWskEN5o7+SAFQCP6UEpk2l9c0v8g\n" +
            "PHG2ITnuRbhj/vFSIzbuQghrmEiNlv2zR3qDRyno5C74S6HfuigvT+AJ/V/NN7wh\n" +
            "l/kWWALIMpwxQwiQsRARHIxbmafVPXFTCQjyGQIDAQABAoIBAFdIbOL3KdYSLGgI\n" +
            "YUnBEpxymjAMkCeAad1WUhRIXF1D7LShvery+TSk4CxVWEXZHiaIUXBZc/k8fqBo\n" +
            "bDdvHotryZ38bBcfl3lCdNbtG1Yow3a+7j8MKyZoKYSmKerVl9VFx7h2cutdVfa2\n" +
            "lrWfPVqsNw0DoUwh/cDOT4nAhcxb7/ilmWmIgQhn8kAmuUvLKQx8fOMYm947j/CO\n" +
            "KXaojBK7Uj9vtW/zXuREgYUr5iem9KUHmilu6KbfV/4pexqTreodCKyr5HnnPAcG\n" +
            "7D+Prbzq29ZofejDxW4hBzJKPlqIjANfclsLOpAHixLzaQWfV76GIUbp7YPdFvcD\n" +
            "8BclltUCgYEAy6tdYFfCWkunuLza904GHzKaNe9Oyqq0bE3Mp3qA6yE6fryStrCE\n" +
            "4xg551XwUBbs8mVunTwKpPTApaqw1Ln7UOADUnoobsXaSwER/woVI8GDA1Jzvh5H\n" +
            "w4dslLQ1UAQ5nfejKNDQ29p70iGLA2ve9NYILRngSGNs/ZjYfRdf1CsCgYEAwE2T\n" +
            "X6m1WYVmeQfkWZkd8OGVp34/TZ7eLDoFmAsjwaDTieREXQTl4enioEmDZK1WYeIT\n" +
            "u12DeXQ6yuY2ZA7YIp9HjhpEXpV7NUe+JkdvIMJOCfc6YSXs3bF1p0eR4aHYNvzr\n" +
            "TDmbQnjbWxU7y2ijB6JTTBxlCsHecbSAy6AFHMsCgYEAn83fwF5bkrnV6e/U50KD\n" +
            "LytnMHaKnh5+3pPUxnwqd1NlMFgJzeqG+iDiHg+iNVUnqbHIrvqTZ4bbOaHKib87\n" +
            "3+NE+Av9eZ0ogL1gP3rLx3hoscyaIExmBpdVrQAQ7K3D/5x0muvtVPQDP4cuKgsT\n" +
            "r4vYcnrhLitFM14gT1ZtzG0CgYEAl68ZJgYJI9nToHgXKI1cTprBnuI4MJO1j5Ec\n" +
            "xHdU9vBSq5vASNcKNQ51UhVqbMlFL+RRyCLVB8IkddLs6DPFTFny6SXS9ABYtQgn\n" +
            "9q21/FMHl1jJVcPCeIP318DeUumEUKtjNjbiPqZx8ABpr+KTZfwmVAy5rqiJO4sz\n" +
            "ZGqOXc0CgYEAhRsqNpzhPH8C65GKUymjJOctlKXfFyutcunxRo+YLc5ACmlWmAAy\n" +
            "EUfcIfACZco3MEAVt1bSoo85qND3i4IJcsZMuUD9A2wlYAca0HpHWCcpYX3JmdTN\n" +
            "+LZQv9qLIPxRvTAt8FzUnnO50lhWL6rTrw0KRLmvkbGkSiZfEynO25I=\n" +
            "-----END RSA PRIVATE KEY-----";
    private static final String ANDROID_API_KEY = "AIzaSyBXEEPJWt7LFHwOZE47tIxvEZLNOXWNz1Q";
    private static final String APPLICATION_NAME = "Yamo";
    private static final String DEFAULT_SOUND = "default";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AmazonSNS amazonSNS;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationUnseenRepository notificationUnseenRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public AwsSNSService(String key, String secret, String endpoint) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(key, secret);
        amazonSNS = new AmazonSNSClient(credentials);
        amazonSNS.setEndpoint(endpoint);
    }

    public void sendNotification(final NotificationType type, final User sourceUser, final List<User> targetUsers, final Venue venue, final boolean database, final boolean push, final String altText) {

        // 1. Store on DB.
        if (database) {

            List<Notification> lstNotifications = new ArrayList<>(targetUsers.size());
            Notification newNotification;
            for (User targetUser : targetUsers) {
                newNotification = new Notification();
                newNotification.setUser(targetUser);
                newNotification.setAssociatedUser(null == sourceUser ? -1L : sourceUser.getUuid());
                newNotification.setAssociatedVenue(null == venue ? -1L : venue.getUuid());
                newNotification.setUserText(null == sourceUser ? null : sourceUser.generateFullName());
                newNotification.setVenueText(null == venue ? null : venue.getName());
                newNotification.setType(type);
                newNotification.setAssociatedUserProfileImageUrl(null == sourceUser ? "" : sourceUser.getProfileImageUrl());
                lstNotifications.add(newNotification);
            }
            if (!lstNotifications.isEmpty()) {
                List<NotificationUnseen> lstUnseenNotifications = new ArrayList<>(targetUsers.size());
                NotificationUnseen unseenNotification;
                Iterable<Notification> itSavedNotifications = notificationRepository.save(lstNotifications);
                if (null != itSavedNotifications) {
                    for (Notification n : itSavedNotifications) {
                        unseenNotification = new NotificationUnseen();
                        unseenNotification.setNotification(n);
                        unseenNotification.setUser(n.getUser());
                        lstUnseenNotifications.add(unseenNotification);
                    }
                    if (!lstUnseenNotifications.isEmpty()) {
                        notificationUnseenRepository.save(lstUnseenNotifications);
                    }
                }
            }
        }

        // 2. Send push to target users.
        if (push) {

            sendPushToTargetUsers(targetUsers, type, sourceUser, venue, altText);

        }
    }

    private void sendPushToTargetUsers(final List<User> targetUsers, final NotificationType type, final User sourceUser, final Venue venue, final String altText) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    // 1. For each target user, find devices.
                    List<Device> lstDevices;
                    int badge;
                    for (User targetUser : targetUsers) {
                        lstDevices = deviceRepository.findByUser(targetUser);
                        if (null != lstDevices) {

                            // 2. Calculate badge.
                            logger.info("Calculating badge for push.");
                            badge = (int) notificationUnseenRepository.countByUserId(targetUser.getUuid());
                            logger.info(String.format("Badge count: %d", badge));

                            // 3. For each device, jsonify message and send push.
                            String message = null;

                            for (Device device : lstDevices) {
                                logger.info(String.format("Sending push to device with token '%s' and platform '%s'.", device.getToken(), device.getPlatform().name()));

                                message = jsonifyMessage(badge, type, device.getPlatform(), null == sourceUser ? -1L : sourceUser.getUuid(), null == venue ? -1L : venue.getUuid(), null == sourceUser ? "" : sourceUser.getFirstName(), null == venue ? "" : venue.getName(), altText);
                                if (null != message) {
                                    try {
                                        forwardNotification(device, message);
                                    } catch (Exception e) {
                                        logger.error("Could not forward notification.", e);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Could not forward notification.", e);
                }

            }
        };

        Thread push = new Thread(runnable);
        push.start();

    }

    private void forwardNotification(Device device, String message) throws Exception {
        NotificationPlatform platform = device.getPlatform();
        CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(platform);
        String platformApplicationArn = platformApplicationResult.getPlatformApplicationArn();
        CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(device.getToken(), platformApplicationArn);
        publish(platformEndpointResult.getEndpointArn(), platform, message);
    }

    private CreatePlatformApplicationResult createPlatformApplication(NotificationPlatform platform) {
        CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();

        Map<String, String> attributes = new HashMap<>();

        switch (platform) {
            case APNS:
                attributes.put("PlatformPrincipal", APPLE_CERTIFICATE_PROD);
                attributes.put("PlatformCredential", APPLE_PRIVATE_KEY_PROD);
                break;

            case APNS_SANDBOX:
                attributes.put("PlatformPrincipal", APPLE_CERTIFICATE_DEV);
                attributes.put("PlatformCredential", APPLE_PRIVATE_KEY_DEV);
                break;

            case GCM:
                attributes.put("PlatformCredential", ANDROID_API_KEY);
                break;
        }

        platformApplicationRequest.setAttributes(attributes);
        platformApplicationRequest.setName(APPLICATION_NAME);
        platformApplicationRequest.setPlatform(platform.name());

        return amazonSNS.createPlatformApplication(platformApplicationRequest);
    }

    private CreatePlatformEndpointResult createPlatformEndpoint(String platformToken, String applicationArn) throws AmazonServiceException {
        CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
        platformEndpointRequest.setToken(platformToken);
        platformEndpointRequest.setPlatformApplicationArn(applicationArn);
        return amazonSNS.createPlatformEndpoint(platformEndpointRequest);
    }

    private PublishResult publish(String endpointArn, NotificationPlatform platform, String message) throws Exception {
        PublishRequest publishRequest = new PublishRequest();
        publishRequest.setMessageStructure("json");

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put(platform.name(), message);
        message = jsonify(messageMap);

        publishRequest.setTargetArn(endpointArn);
        publishRequest.setMessage(message);
        return amazonSNS.publish(publishRequest);
    }

    private String jsonifyMessage(final int badge, final NotificationType type, final NotificationPlatform platform, Long associatedUserId, Long associatedVenueId, String associatedUser, String associatedVenue, String altText) throws IOException {

        String json = "";

        switch (platform) {
            case APNS:
            case APNS_SANDBOX:
                json = createApplePushNotificationMessage(badge, type.getValue(), associatedUserId, associatedVenueId, associatedUser, associatedVenue, altText);
                break;

            case GCM:
                json = createAndroidPushNotificationMessage(type.getValue(), associatedUserId, associatedVenueId);
                break;
        }

        logger.info(String.format("Jsonified message: %s", json));
        return json;
    }

    private String createApplePushNotificationMessage(int badge, Integer type, Long associatedUserId, Long associatedVenueId, String associatedUser, String associatedVenue, String altText) throws IOException {
        Map<String, Object> appleMessageMap = new HashMap<>();
        Map<String, Object> appMessageMap = new HashMap<>();

        Map<String, Object> alert = new HashMap<>();
        alert.put("loc-key", generateNotificationLocalisedText(type));

        switch (NotificationType.fromInt(type)) {

            case UNSPECIFIED:
                alert.put("loc-args", Collections.singletonList(associatedUser));
                break;
            case FACEBOOK_FRIEND_JOINED:
                alert.put("loc-args", Collections.singletonList(associatedUser));
                break;
            case EXHIBITION_SUGGESTION:
                alert.put("loc-args", Arrays.asList(altText, associatedVenue));
                break;
            case GET_TO_KNOW_ME_REMINDER:
                alert.put("loc-args", Collections.emptyList());
                break;
            case CURRENT_LOCATION_SUGGESTION:
                alert.put("loc-args", Arrays.asList(altText, associatedVenue));
                break;
            case EXHIBITION_CLOSING_SOON:
                alert.put("loc-args", Arrays.asList(altText, associatedVenue));
                break;
            case NEW_EXHIBITION_AT_FAVOURITE_GALLERY:
                alert.put("loc-args", Arrays.asList(altText, associatedVenue));
                break;
        }

        appMessageMap.put("alert", alert);
        appMessageMap.put("badge", badge);
        appMessageMap.put("sound", DEFAULT_SOUND);
        appleMessageMap.put("aps", appMessageMap);
        appleMessageMap.put("type", type);
        if (null != associatedUserId) {
            appleMessageMap.put("associatedUserId", associatedUserId);
        }
        if (null != associatedUser) {
            appleMessageMap.put("associatedUser", associatedUser);
        }
        if (null != associatedVenueId) {
            appleMessageMap.put("associatedVenueId", associatedVenueId);
        }
        if (null != associatedVenue) {
            appleMessageMap.put("associatedVenue", associatedVenue);
        }

        return jsonify(appleMessageMap);
    }

    private String createAndroidPushNotificationMessage(Integer type, Long associatedUserId, Long associatedVenueId) throws IOException {
        Map<String, Object> androidMessageMap = new HashMap<>();
        Map<String, String> payload = new HashMap<>();

        payload.put("title", APPLICATION_NAME);
        payload.put("message", generateNotificationLocalisedText(type));
        payload.put("type", String.valueOf(type));
        if (null != associatedUserId) {
            payload.put("associatedUserId", associatedUserId.toString());
        }
        if (null != associatedVenueId) {
            payload.put("associatedVenueId", associatedVenueId.toString());
        }
        androidMessageMap.put("data", payload);

        return jsonify(androidMessageMap);
    }

    private String jsonify(Object message) throws IOException {
        return objectMapper.writeValueAsString(message);
    }

    private String generateNotificationLocalisedText(Integer type) {
        String localisedText = "";

        if (null != type) {
            switch (NotificationType.fromInt(type)) {

                case UNSPECIFIED:
                    localisedText = "notification.text.unspecified";
                    break;
                case FACEBOOK_FRIEND_JOINED:
                    localisedText = "notification.text.friend.joined";
                    break;
                case EXHIBITION_SUGGESTION:
                    localisedText = "notification.text.exhibition.suggestion";
                    break;
                case GET_TO_KNOW_ME_REMINDER:
                    localisedText = "notification.text.gettoknowme.reminder";
                    break;
                case CURRENT_LOCATION_SUGGESTION:
                    localisedText = "notification.text.location.suggestion";
                    break;
                case EXHIBITION_CLOSING_SOON:
                    localisedText = "notification.text.exhibition.closing";
                    break;
                case NEW_EXHIBITION_AT_FAVOURITE_GALLERY:
                    localisedText = "notification.text.new.exhibition";
                    break;
            }
        }

        return localisedText;
    }

}
