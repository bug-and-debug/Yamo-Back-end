package com.locassa.yamo.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum AppleReceiptResponse implements Serializable {

    RECEIPT_VALID(0),
    UNREADABLE(21000),
    MALFORMED(21002),
    UNAUTHENTICATED(21003),
    SHARED_SECRET_NOT_MATCH(21004),
    UNAVAILABLE(21005),
    SUBSCRIPTION_EXPIRED(21006),
    TEST_TO_PRODUCTION(21007),
    PRODUCTION_TO_TEST(21008);

    private static final Map<Integer, AppleReceiptResponse> intToTypeMap = new HashMap<Integer, AppleReceiptResponse>();

    static {
        for (AppleReceiptResponse type : AppleReceiptResponse.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    private final int value;

    private AppleReceiptResponse(int value) {
        this.value = value;
    }

    public static AppleReceiptResponse fromInt(int value) {
        return intToTypeMap.get(value);
    }

    public int getValue() {
        return this.value;
    }

    public String getMessage() {
        String message = null;

        switch (this) {

            case UNREADABLE:
                message = "The App Store could not read the JSON object you provided.";
                break;
            case MALFORMED:
                message = "The data in the receipt-data property was malformed or missing.";
                break;
            case UNAUTHENTICATED:
                message = "The receipt could not be authenticated.";
                break;
            case SHARED_SECRET_NOT_MATCH:
                message = "The shared secret you provided does not match the shared secret on file for your account. Only returned for iOS 6 style transaction receipts for auto-renewable subscriptions.";
                break;
            case UNAVAILABLE:
                message = "The receipt server is not currently available.";
                break;
            case SUBSCRIPTION_EXPIRED:
                message = "This receipt is valid but the subscription has expired. When this status code is returned to your server, the receipt data is also decoded and returned as part of the response. Only returned for iOS 6 style transaction receipts for auto-renewable subscriptions.";
                break;
            case TEST_TO_PRODUCTION:
                message = "This receipt is from the test environment, but it was sent to the production environment for verification. Send it to the test environment instead.";
                break;
            case PRODUCTION_TO_TEST:
                message = "This receipt is from the production environment, but it was sent to the test environment for verification. Send it to the production environment instead.";
                break;
        }

        return message;
    }

}
