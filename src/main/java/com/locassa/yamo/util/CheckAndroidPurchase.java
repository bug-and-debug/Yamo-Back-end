package com.locassa.yamo.util;

import com.google.api.services.androidpublisher.AndroidPublisher;
import org.apache.log4j.Logger;

public class CheckAndroidPurchase {

    private static final Logger logger = Logger.getLogger(CheckAndroidPurchase.class);

    public static boolean validatePurchase(String packageName, String subscriptionId, String token) {

        boolean valid = true;

        try {
            logger.error("Attempting validation against play services with the following values: ");
            logger.error("Application name: " + ApplicationConfig.APPLICATION_NAME);
            logger.error("Service account email: " + ApplicationConfig.SERVICE_ACCOUNT_EMAIL);
            logger.error("Package name: " + packageName);
            logger.error("Subscription Id: " + subscriptionId);
            logger.error("Purchase token: " + token);
            AndroidPublisher androidPublisher = AndroidPublisherHelper.init(ApplicationConfig.APPLICATION_NAME, ApplicationConfig.SERVICE_ACCOUNT_EMAIL);
            AndroidPublisher.Purchases.Subscriptions.Get subscriptions = androidPublisher.purchases().subscriptions().get(packageName, subscriptionId, token);

            logger.error("Http content: " + subscriptions.getHttpContent());
            logger.error("Subscriptions content: " + subscriptions);

        } catch (Exception e) {
            logger.error("Failed to validate Android purchase!", e);
            valid = false;
        }

        return valid;

    }

}
