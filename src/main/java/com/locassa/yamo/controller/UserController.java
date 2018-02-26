package com.locassa.yamo.controller;

import com.locassa.yamo.exception.*;
import com.locassa.yamo.model.*;
import com.locassa.yamo.model.dto.*;
import com.locassa.yamo.model.enums.AppleReceiptResponse;
import com.locassa.yamo.model.view.UserLabelNormalisedValue;
import com.locassa.yamo.service.UserService;
import com.locassa.yamo.service.aws.AwsS3Service;
import com.locassa.yamo.util.CheckAndroidPurchase;
import com.locassa.yamo.util.YamoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.ImageType;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/user")
@Api(description = "Endpoints to manage users.")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AwsS3Service awsS3Service;

    private static final Logger logger = Logger.getLogger(UserController.class);

    @ApiOperation(value = "Get auth user", notes = "It returns the authenticated user.")
    @PreAuthorize("hasRole('GUEST') or hasRole('USER')")
    @RequestMapping(method = RequestMethod.GET)
    public User getUser() {

        logger.debug("getUser");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Retrieve user.
        return authUser;
    }

    @ApiOperation(value = "Get own profile", notes = "It retrieves the authenticated user profile.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ProfileDTO getProfile() {

        logger.debug("getProfile");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Retrieve profile.
        return userService.getUserProfile(authUser, authUser, true);
    }

    @ApiOperation(value = "Get other user's profile", notes = "It retrieves the profile for the specified user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{userId}/profile", method = RequestMethod.GET)
    public ProfileDTO getOtherProfile(@PathVariable("userId") Long userId) {

        logger.debug("getOtherProfile");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing user.
        User existingUser = userService.findUserById(userId);
        if (null == existingUser) {
            throw new UserNotFoundException(userId);
        }

        // 2. Retrieve profile.
        return userService.getOtherProfile(authUser, existingUser);
    }

    @ApiOperation(value = "Edit profile", notes = "It modifies the authenticated user profile. Field 'nickNameEnabled' is mandatory.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/profile/edit", method = RequestMethod.PUT)
    public User editProfile(@RequestBody EditProfileDTO editProfileDTO) {

        logger.debug("editProfile");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Retrieve profile.
        return userService.editProfile(authUser, editProfileDTO);
    }

    @ApiOperation(value = "Update user location", notes = "It updates the location for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/location", method = RequestMethod.PUT)
    public User editUserLocation(@RequestBody LocationDTO locationDTO) {

        logger.debug("editUserLocation");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Update location.
        User updatedUser = userService.updateUserLocation(authUser, locationDTO);

        if (null == updatedUser) {
            throw new ValidationException("location", String.format("(%s, %s) %s", locationDTO.getLat(), locationDTO.getLon(), locationDTO.getLocation()));
        }

        return updatedUser;
    }

    @ApiOperation(value = "Report user", notes = "It reports the specified user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{userId}/report", method = RequestMethod.POST)
    public ResponseDTO reportUser(@PathVariable("userId") Long userId) {

        logger.debug("reportUser");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing user.
        User existingUser = userService.findUserById(userId);
        if (null == existingUser) {
            throw new UserNotFoundException(userId);
        }

        // 3. Check that auth user didn't report the existing user already.
        if (null != existingUser.getReports() && existingUser.getReports().contains(authUser)) {
            throw new UserAlreadyReportedException(authUser.getEmail(), existingUser.getEmail());
        }

        return userService.reportUser(authUser, existingUser);
    }

    @ApiOperation(value = "Follow user", notes = "It makes the authenticated user follow the specified user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{userId}/follow", method = RequestMethod.POST)
    public ResponseDTO followUser(@PathVariable("userId") Long userId) {
        logger.debug("followUser");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing user.
        User existingUser = userService.findUserById(userId);
        if (null == existingUser) {
            throw new UserNotFoundException(userId);
        }

        // 3. Check that auth user didn't follow the existing user already.
        if (null != existingUser.getFollowers() && existingUser.getFollowers().contains(authUser)) {
            throw new UserAlreadyFollowedException(authUser.getEmail(), existingUser.getEmail());
        }

        return userService.followUser(authUser, existingUser);
    }

    @ApiOperation(value = "Un-follow user", notes = "It makes the authenticated user un-follow the specified user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{userId}/un-follow", method = RequestMethod.DELETE)
    public ResponseDTO unFollowUser(@PathVariable("userId") Long userId) {
        logger.debug("unFollowUser");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing user.
        User existingUser = userService.findUserById(userId);
        if (null == existingUser) {
            throw new UserNotFoundException(userId);
        }

        // 3. Check that auth user was following the existing user.
        if (null != existingUser.getFollowers() && !existingUser.getFollowers().contains(authUser)) {
            throw new UserAlreadyFollowedException(authUser.getEmail(), existingUser.getEmail(), null);
        }

        return userService.unFollowUser(authUser, existingUser);
    }

    @ApiOperation(value = "Register device", notes = "It registers a device for the authenticated user.<br/>" +
            "Allowed values for platform property are:<br/>" +
            " 0 (APNS)<br/>" +
            " 1 (APNS_SANDBOX)<br/>" +
            " 2 (GCM)<br/>")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/device/register", method = RequestMethod.POST)
    public Device registerDevice(@RequestBody DeviceDTO deviceDTO) {

        logger.debug("registerDevice");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Register device.
        return userService.registerDevice(authUser, deviceDTO);

    }

    @ApiOperation(value = "Send test push", notes = "It sends a push notification to the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/notification/test", method = RequestMethod.POST)
    public ResponseDTO sendTestPush() {

        logger.debug("sendTestPush");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        userService.sendTestPush(authUser);

        return ResponseDTO.statusOK();
    }

    @ApiOperation(value = "Get counters", notes = "It returns the counters for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/counters", method = RequestMethod.GET)
    public CountersDTO getCounters() {

        logger.debug("getCounters");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        return userService.getCounters(authUser);
    }

    @ApiOperation(value = "List notifications", notes = "It gets a list of notifications for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/notification/list", method = RequestMethod.PUT)
    public List<Notification> listNotifications(@RequestParam(value = "from", required = false, defaultValue = "-1") Long from, @RequestParam("older") boolean older) {

        logger.debug("listNotifications");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find notifications.
        if (Long.valueOf(-1).equals(from)) {
            from = YamoUtils.now().getTime();
        }
        return userService.findNotificationsForUser(authUser, new Date(from), older);

    }

    @ApiOperation(value = "Mark notification as read", notes = "It marks the specified notification as read for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/notification/{notificationId}/mark", method = RequestMethod.DELETE)
    public ResponseDTO markNotificationAsRead(@PathVariable("notificationId") Long notificationId) {

        logger.debug("markNotificationAsRead");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Mark notification as read.
        return userService.markNotificationAsRead(authUser, notificationId);

    }

    @ApiOperation(value = "Delete notification", notes = "It deletes the specified notification for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/notification/{notificationId}/delete", method = RequestMethod.DELETE)
    public ResponseDTO deleteNotification(@PathVariable("notificationId") Long notificationId) {

        logger.debug("deleteNotification");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing notification.
        Notification existingNotification = userService.findSingleNotificationForUser(authUser, notificationId);
        if (null == existingNotification) {
            throw new NotificationNotFoundException(notificationId);
        }

        // 2. Mark notification as read.
        return userService.deleteNotificationForUser(authUser, existingNotification);

    }

    @ApiOperation(value = "Find user normalised tag values", notes = "It gets a list of normalised tag values for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/tag/normalised", method = RequestMethod.GET)
    public List<UserLabelNormalisedValue> findNormalisedTagValues() {

        logger.debug("findNormalisedTagValues");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find values.
        return userService.findNormalisedTagsForUser(authUser.getUuid());

    }

    @ApiOperation(value = "Upgrade user (Android)", notes = "It upgrades the authenticated guest user to a normal user.")
    @RequestMapping(value = "/upgrade/android", method = RequestMethod.POST)
    @PreAuthorize("hasRole('GUEST')")
    public UserSubscription upgradeAccountAndroid() {

        logger.debug("upgradeAccountApple");

        try {
            CheckAndroidPurchase.validatePurchase("packageName", "subscriptionId", "token");
        } catch (Exception e) {
            logger.error("Error occurred: ", e);
        }

        return null;
    }

    @ApiOperation(value = "Upgrade user (Apple)", notes = "It upgrades the authenticated guest user to a normal user.")
    @RequestMapping(value = "/upgrade/apple", method = RequestMethod.POST)
    @PreAuthorize("hasRole('GUEST')")
    public UserSubscription upgradeAccountApple(@RequestBody AppleReceiptDTO appleReceiptDTO, HttpServletRequest request) {

        logger.debug("upgradeAccountApple");

        // 1. Find the signed in user.
        User signedInUser = userService.findAuthenticatedUser();
        if (null == signedInUser) {
            throw new UserNotFoundException();
        }

        // 2. Check in-app purchase token.
        Map<String, Object> responseReceiptMap = YamoUtils.getAppleInAppPurchaseReceiptResponse(appleReceiptDTO.getReceiptData(), YamoUtils.APPLE_INAPP_PURCHASE_SHARED_SECRET);

        AppleReceiptResponse response = null == responseReceiptMap ? null : AppleReceiptResponse.fromInt((Integer) responseReceiptMap.get("status"));
        boolean verified = AppleReceiptResponse.RECEIPT_VALID.equals(response);
        if (!verified) {
            throw new AppleReceiptException(null == response ? null : response.getMessage());
        }

        // 3. Find the subscription end date.
        Date subscriptionStart = null;
        Date subscriptionEnd = null;
        String updatedReceiptJson = null;
        String receiptUniqueIdentifier = null;

        try {
            ArrayList receipts = (ArrayList) responseReceiptMap.get("latest_receipt_info");
            if (null != receipts) {
                HashMap updatedReceiptMap = (HashMap) receipts.get(receipts.size() - 1);
                subscriptionStart = new Date(Long.parseLong((String) updatedReceiptMap.get("purchase_date_ms")));
                subscriptionEnd = new Date(Long.parseLong((String) updatedReceiptMap.get("expires_date_ms")));
                receiptUniqueIdentifier = String.valueOf(updatedReceiptMap.get("original_transaction_id"));
                updatedReceiptJson = YamoUtils.fromMapToJsonString(updatedReceiptMap);
            }

        } catch (Exception e) {
            throw new UserSubscriptionException();
        }

        // 4. Check that this receipt is associated to the same user.
        if (!userService.proceedWithAppleSubscription(signedInUser.getUuid(), receiptUniqueIdentifier)) {
            // error
            throw new AppleIdAlreadyUsedException();
        }

        // 5. Promote account.
        if (null != subscriptionEnd && subscriptionEnd.before(YamoUtils.now())) {
            return null;
        }

        UserSubscription subscription = userService.upgradeAccountForApple(signedInUser, subscriptionStart, subscriptionEnd, updatedReceiptJson, receiptUniqueIdentifier, appleReceiptDTO.getReceiptData());

        // 6. Remove user information from session.
        userService.deleteUserCredentialsAndToken(request, signedInUser);

        if (null != subscription) {
            return subscription;
        } else {
            throw new UserSubscriptionException(true);
        }
    }

    @ApiOperation(value = "Sign up for guest account", notes = "It modifies an existing guest user or logs them in.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/sign-up/guest", method = RequestMethod.POST)
    public User signUp(@RequestBody SignUpDTO signUpDTO) {

        logger.debug("signUp");

        // 0. Find the authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        String userEmail = signUpDTO.getEmail();
        String userPassword = signUpDTO.getPassword();

        // 1. Find the user.
        User existingUser = userService.findUserByEmail(userEmail);
        if (null != existingUser) {

            if (existingUser.isSignUpCompleted()) {

                // Return user if password is correct.
                if (userService.authenticateUser(existingUser.getPassword(), signUpDTO.getPassword())) {
                    return existingUser;
                } else {
                    throw new UserAlreadyExistsException(userEmail);
                }

            } else {

                if (userService.authenticateUser(existingUser.getPassword(), signUpDTO.getPassword())) {
                    existingUser.setFirstName(signUpDTO.getFirstName());
                    existingUser.setLastName(signUpDTO.getLastName());
                    existingUser.setProfileImageUrl(
                            awsS3Service.storeFileDataForUser(
                                    signUpDTO.getImageContent(), String.valueOf(existingUser.getUuid())));
                    return userService.save(existingUser);
                } else {
                    throw new UserAlreadyExistsException(userEmail);
                }

            }
        }

        // 2. Validate email.
        if (!YamoUtils.isEmailValid(userEmail)) {
            throw new ValidationException("email", userEmail);
        }

        // 3. Validate password.
        if (!YamoUtils.isPasswordValid(userPassword)) {
            throw new ValidationException("password", "***");
        }

        // 4. Create new user.
        return userService.createNewUserFromGuest(authUser, signUpDTO);

    }

    @ApiOperation(value = "Connect with Facebook - Guest", notes = "It authenticates a user using their FB account. The access token provided by Facebook is needed.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/connect/facebook", method = RequestMethod.POST)
    public User connectWithFacebook(@RequestBody StringDTO stringDTO) {
        logger.debug("connectWithFacebook");

        // 0. Find the authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        String userToken = stringDTO.getValue();

        // 1. Get the facebook template.
        Facebook facebook;
        try {
            facebook = new FacebookTemplate(userToken);
        } catch (Exception e) {
            throw new WrongCredentialsException();
        }

        if (facebook.isAuthorized()) {

            // 2. Get the user email from Facebook.
            String email;
            try {
                email = facebook.userOperations().getUserProfile().getEmail();
            } catch (Exception e) {
                logger.error("An error occurred when trying to get the user profile email.", e);
                throw new WrongCredentialsException();
            }

            // 3. Get the user by email from DB.
            User user;
            if (null != email) {
                user = userService.findUserByEmail(email);

                String facebookId = facebook.userOperations().getUserProfile().getId();

                if (null == user) {

                    // 4. The user does not exist. Create.
                    String firstName = facebook.userOperations().getUserProfile().getFirstName();
                    String lastName = facebook.userOperations().getUserProfile().getLastName();
                    String gender = facebook.userOperations().getUserProfile().getGender();
                    byte[] userProfilePicture = facebook.userOperations().getUserProfileImage(ImageType.LARGE);
                    return userService.createNewUserFromGuestFacebook(authUser, firstName, lastName, email, facebookId, gender, userProfilePicture);

                } else {

                    // 5. The user exists. Check that the facebook Id matches.
                    if (null != user.getFacebookId() && user.getFacebookId().equals(facebookId)) {
                        if (userService.authenticateUser(user.getPassword(), String.format("%s%s", facebookId, YamoUtils.USER_SOCIAL_SECRET_WORD))) {
                            return user;
                        } else {
                            throw new ConnectWithFacebookException();
                        }
                    } else {
                        throw new ConnectWithFacebookException();
                    }

                }

            } else {
                logger.error("The retrieved user profile email is null.");
                throw new WrongCredentialsException();
            }
        } else {
            logger.error("Facebook access is not authorized.");
            throw new WrongCredentialsException();
        }
    }

//    @ApiOperation(value = "TEST: Delete user credentials", notes = "Testing endpoint to be called when changing roles.")
//    @PreAuthorize("hasRole('GUEST')")
//    @RequestMapping(value = "/test/deleteCredentials", method = RequestMethod.POST)
//    public ResponseDTO deleteCredentials(HttpServletRequest request) {
//
//        logger.debug("deleteCredentials");
//
//        // 0. Find the authenticated user.
//        User authUser = userService.findAuthenticatedUser();
//        if (null == authUser) {
//            throw new UserNotFoundException();
//        }
//
//        userService.deleteUserCredentialsAndToken(request, authUser);
//
//        return ResponseDTO.statusOK();
//    }

}
