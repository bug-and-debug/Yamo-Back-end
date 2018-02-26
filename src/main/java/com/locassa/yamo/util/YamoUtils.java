package com.locassa.yamo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locassa.yamo.model.dto.LocationDTO;
import com.locassa.yamo.model.enums.AppleReceiptResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class YamoUtils {

    public static final String INFO_ABOUT_NO_AUTH_KEY = "To call this endpoint, you must be authenticated with the no-auth username and password. ";
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    public static final float DISTANCE_UNIT_FACTOR_KILOMETRES = 111.045f;
    public static final float DISTANCE_UNIT_FACTOR_MILES = 69.0f;
    public static final int DEFAULT_DAYS_FOR_GET_TO_KNOW_ME_REMINDER = 7;
    public static final int DEFAULT_DAYS_FOR_EXHIBITIONS_ENDING_SOON = 7;
    public static final int DEFAULT_DECIMAL_POSITIONS_TO_DIVIDE = 6;
    public static final double DEFAULT_MILES_FOR_FILTER = 100.0;
    public static final String USER_SOCIAL_SECRET_WORD = "taMaF7bBT6j3u8JtBns9rQBHVCHfMLTL";
    public static final String GUEST_SHARED_SECRET = "nVcnbpx2UE65TTb9Er4yCcPjdTHPFAyp";
    public static final String APPLE_INAPP_PURCHASE_SHARED_SECRET = "9229451b121a49f8b05b141b1b48bafa";
    public static final String APPLE_RECEIPT_VALIDATION_URL_TEST = "https://sandbox.itunes.apple.com/verifyReceipt";
    public static final String APPLE_RECEIPT_VALIDATION_URL_PRODUCTION = "https://buy.itunes.apple.com/verifyReceipt";
    public static final String DEFAULT_PROFILE_IMAGE_PLACEHOLDER = "https://cdn.futurelearn.com/assets/user/profile_image_placeholder-476df86f7922f3309119892b35d81511.png";
    public static final String DEFAULT_CITY = "London";
    public static final double DEFAULT_LOCATION_LAT = 51.507351;
    public static final double DEFAULT_LOCATION_LON = -0.127758;
    public static final int NUM_REPORTED_USERS_NEEDED = 3;
    public static final long DEFAULT_INITIAL_TAG_WEIGHT = 1000;
    public static final double DEFAULT_RESULTS_FOR_RELEVANCE = 60;
    //sharmaNeh property created for YMS-44
    public static final int DEFAULT_RESULTS_FOR_PAGING = 10;
    private static final Logger logger = Logger.getLogger(YamoUtils.class);
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9\\+]+(\\.[_A-Za-z0-9\\+]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final int DEFAULT_PASSWORD_MIN_LENGTH = 6;
    private static final int SECRET_CODE_LENGTH = 8;

    public static final List<String> MEDIUMS_TO_SHOW_IN_FILTERS = Arrays.asList(
            "Painting",
            "Film",
            "Drawing",
            "Sculpture",
            "Performance",
            "Prints"
    );

    public static boolean isLocationValid(double latitude, double longitude, String location) {
        return
                -90.0f < latitude && 90.0f > latitude

                        && -180.0f < longitude && 180.0 > longitude

                        && stringIsNotNullAndNotEmpty(location);
    }

    public static boolean isLocationValid(LocationDTO locationDTO) {
        return isLocationValid(locationDTO.getLat(), locationDTO.getLon(), locationDTO.getLocation());
    }

    public static boolean stringIsNullOrEmpty(String x) {
        return null == x || x.trim().isEmpty();
    }

    public static boolean stringIsNotNullAndNotEmpty(String x) {
        return !stringIsNullOrEmpty(x);
    }

    public static boolean isEmailValid(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }

    public static boolean isPasswordValid(String password) {
        return null != password && password.length() >= DEFAULT_PASSWORD_MIN_LENGTH;
    }

    public static String generateRandomSecretCode() {
        return RandomStringUtils.randomNumeric(4);
    }

    public static String generateRandomString() {
        return RandomStringUtils.randomAlphanumeric(32).toLowerCase();
    }

    public static Date now() {
        return new Date();
    }

    public static List<Long> fromBigIntegerListToLongList(List<BigInteger> lstBigInteger) {
        if (null == lstBigInteger || lstBigInteger.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<Long> result = new ArrayList<>();
            for (BigInteger bi : lstBigInteger) {
                result.add(bi.longValue());
            }
            return result;
        }
    }

    public static double distFromInMiles(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    public static double distFromInKm(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0; // kilometers (or 3958.75 miles)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    /* ************************************* */
    /*             SUBSCRIPTIONS             */
    /* ************************************* */

    public static String apiCallAppleValidateReceipt(String endpoint, String receiptData, String sharedSecret) {

        String jsonResponse = "";
        HttpURLConnection conn = null;

        try {

            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.addRequestProperty("Content-Type", "application/" + "POST");
            conn.setDoOutput(true);

            String query = String.format("{\n" +
                            "\"receipt-data\":\"%s\",\n" +
                            "\"password\":\"%s\"\t\n" +
                            "}",
                    receiptData,
                    sharedSecret);

            conn.setRequestProperty("Content-Length", Integer.toString(query.length()));
            conn.getOutputStream().write(query.getBytes("UTF8"));

            int responseCode = conn.getResponseCode();
            logger.info("Sending 'POST' request to URL : " + url);
            logger.info("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            jsonResponse = response.toString();

        } catch (Exception e) {
            jsonResponse = "{\"error\":\"Could not validate Apple receipt.\"}";
        } finally {
            try {
                if (null != conn) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                ;
            }
        }

        return jsonResponse;
    }

    public static Map<String, Object> validateAppleReceipt(String endpoint, String receiptData, String sharedSecret) {

        Map<String, Object> result = new HashMap<>();

        Document doc = null;
        try {
            logger.error("Connecting to Apple receipt validation: " + endpoint);

            result = fromJsonStringToMap(apiCallAppleValidateReceipt(endpoint, receiptData, sharedSecret));

        } catch (Exception e) {
        }

        return result;
    }

    public static Map<String, Object> fromJsonStringToMap(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };
        Map<String, Object> map = new HashMap<>();
        try {
            map = mapper.readValue(json, typeRef);
        } catch (Exception e) {
            ;
        }
        return map;
    }

    public static String fromMapToJsonString(Map map) {

        String result = "";
        try {
            result = new ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            result = "{}";
        }

        return result;
    }

    public static Map<String, Object> getAppleInAppPurchaseReceiptResponse(String receiptData, String sharedSecret) {

        logger.error("Apple receipt data received for validation: " + receiptData);

        Map<String, Object> responseReceiptMap = validateAppleReceipt(APPLE_RECEIPT_VALIDATION_URL_PRODUCTION, receiptData, sharedSecret);
        AppleReceiptResponse response = null;

        try {
            response = AppleReceiptResponse.fromInt((Integer) responseReceiptMap.get("status"));
        } catch (Exception e) {
            response = null;
        }

        if (AppleReceiptResponse.TEST_TO_PRODUCTION.equals(response)) {
            responseReceiptMap = validateAppleReceipt(APPLE_RECEIPT_VALIDATION_URL_TEST, receiptData, sharedSecret);

            try {
                response = AppleReceiptResponse.fromInt((Integer) responseReceiptMap.get("status"));
            } catch (Exception e) {
                response = null;
            }
        }

        return null == response ? null : responseReceiptMap;

    }


}
