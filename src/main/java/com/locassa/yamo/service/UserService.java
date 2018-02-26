package com.locassa.yamo.service;

import com.locassa.yamo.OAuth2SecurityConfig;
import com.locassa.yamo.model.*;
import com.locassa.yamo.model.dto.*;
import com.locassa.yamo.model.enums.NotificationPlatform;
import com.locassa.yamo.model.enums.NotificationType;
import com.locassa.yamo.model.enums.SubscriptionType;
import com.locassa.yamo.model.enums.UserType;
import com.locassa.yamo.model.summary.RouteSummary;
import com.locassa.yamo.model.summary.UserSummary;
import com.locassa.yamo.model.summary.VenueSummary;
import com.locassa.yamo.model.view.UserLabelNormalisedValue;
import com.locassa.yamo.repository.*;
import com.locassa.yamo.service.aws.AwsS3Service;
import com.locassa.yamo.service.aws.AwsSESService;
import com.locassa.yamo.service.aws.AwsSNSService;
import com.locassa.yamo.util.NotificationCreatedDescComparator;
import com.locassa.yamo.util.UserCreatedDescComparator;
import com.locassa.yamo.util.VenueSummaryComparator;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteSummaryRepository routeSummaryRepository;

    @Autowired
    private UserSummaryRepository userSummaryRepository;

    @Autowired
    private VenueSummaryRepository venueSummaryRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private AwsSESService awsSESService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AwsSNSService awsSNSService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationUnseenRepository notificationUnseenRepository;

    @Autowired
    private UserLabelNormalisedValueRepository userLabelNormalisedValueRepository;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private OAuth2SecurityConfig.AuthorizationServerConfig authorizationServerConfig;

    public User findAuthenticatedUser() {
        logger.debug("findAuthenticatedUser");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (null != principal && principal instanceof org.springframework.security.core.userdetails.User) {
            return userRepository.findByEmailAndEnabledTrue(
                    ((org.springframework.security.core.userdetails.User) principal)
                            .getUsername());
        }
        return null;
    }

    public void deleteUserCredentials(HttpServletRequest request) {
        SecurityContextHolder.getContext().setAuthentication(null);
        request.getSession().invalidate();
    }

    public void deleteUserCredentialsAndToken(HttpServletRequest request, User authUser) {
        deleteUserCredentials(request);
        TokenStore tokenStore = authorizationServerConfig.getTokenStore();
        Collection<OAuth2AccessToken> lstTokens = tokenStore.findTokensByClientIdAndUserName("a4PqNmXHP482ZK89", authUser.getEmail());
        if (null != lstTokens) {
            OAuth2RefreshToken tempRefreshToken;
            for (OAuth2AccessToken accessToken : lstTokens) {
                tempRefreshToken = accessToken.getRefreshToken();
                tokenStore.removeAccessTokenUsingRefreshToken(tempRefreshToken);
                tokenStore.removeRefreshToken(tempRefreshToken);
            }
        }
    }

    public User findUserByEmail(String email) {
        logger.debug("findUserByEmail");
        return userRepository.findByEmailAndEnabledTrue(email);
    }

    public User findUserByEmailOnly(String email) {
        logger.debug("findUserByEmailOnly");
        return userRepository.findByEmail(email);
    }

    public boolean authenticateUser(String userPassword, String inputPassword) {
        logger.debug("authenticateUser");
        return new BCryptPasswordEncoder().matches(inputPassword, userPassword);
    }

    public User save(User existingUser) {
        logger.debug("save");
        return userRepository.save(existingUser);
    }

    @Transactional
    public User createNewUserFromGuest(User authUser, SignUpDTO signUpDTO) {

        logger.debug("createNewUserFromGuest");

        User newUser = authUser;
        newUser.setFirstName(signUpDTO.getFirstName());
        newUser.setLastName(signUpDTO.getLastName());
        newUser.setEmail(signUpDTO.getEmail());
        newUser.setPassword(new BCryptPasswordEncoder().encode(signUpDTO.getPassword()));
        newUser.setSecretCode(YamoUtils.generateRandomSecretCode());
        newUser.setFacebookId(null);
        newUser.setProfileImageUrl(awsS3Service.storeFileDataForUser(signUpDTO.getImageContent(), ""));

        newUser.setCity(YamoUtils.DEFAULT_CITY);
        newUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
        newUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
        newUser.setLocation(YamoUtils.DEFAULT_CITY);

        newUser.setEnabled(true);
        newUser.setVisible(true);
        newUser.setNickname(null);
        newUser.setNickNameEnabled(false);
        newUser.setSignUpCompleted(false);
        newUser.setReady(false);
//        newUser.setUserType(UserType.USER); This should not happen until the user pays.

        newUser = userRepository.save(newUser);

        // TODO: Send email through AWS SES.

        return newUser;

    }

    @Transactional
    public User createNewUser(SignUpDTO signUpDTO) {
        logger.debug("createNewUser");

        User newUser = new User();
        newUser.setFirstName(signUpDTO.getFirstName());
        newUser.setLastName(signUpDTO.getLastName());
        newUser.setEmail(signUpDTO.getEmail());
        newUser.setPassword(new BCryptPasswordEncoder().encode(signUpDTO.getPassword()));
        newUser.setSecretCode(YamoUtils.generateRandomSecretCode());
        newUser.setFacebookId(null);
        newUser.setProfileImageUrl(awsS3Service.storeFileDataForUser(signUpDTO.getImageContent(), ""));

        newUser.setCity(YamoUtils.DEFAULT_CITY);
        newUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
        newUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
        newUser.setLocation(YamoUtils.DEFAULT_CITY);

        newUser.setEnabled(true);
        newUser.setVisible(true);
        newUser.setNickname(null);
        newUser.setNickNameEnabled(false);
        newUser.setSignUpCompleted(false);
        newUser.setReady(false);
        newUser.setUserType(UserType.USER);

        newUser = userRepository.save(newUser);

        // TODO: Send email through AWS SES.

        return newUser;
    }

    @Transactional
    public User createGuestUser() {
        logger.debug("createGuestUser");

        String username = YamoUtils.generateRandomString();
        String password = String.format("%s%s", username, YamoUtils.GUEST_SHARED_SECRET);

        User newUser = new User();
        newUser.setFirstName("");
        newUser.setLastName("");
        newUser.setEmail(username);
        newUser.setPassword(new BCryptPasswordEncoder().encode(password));
        newUser.setSecretCode(null);
        newUser.setFacebookId(null);
        newUser.setProfileImageUrl(null);

        newUser.setCity(YamoUtils.DEFAULT_CITY);
        newUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
        newUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
        newUser.setLocation(YamoUtils.DEFAULT_CITY);

        newUser.setEnabled(true);
        newUser.setVisible(true);
        newUser.setNickname(null);
        newUser.setNickNameEnabled(false);
        newUser.setSignUpCompleted(true);
        newUser.setReady(false);
        newUser.setUserType(UserType.GUEST);

        newUser = userRepository.save(newUser);

        return newUser;
    }

    public User findBySecretCode(String secretCode) {
        logger.debug("findBySecretCode");
        return userRepository.findBySecretCode(secretCode);
    }

    public boolean verifyUser(User userToVerify) {
        logger.debug("verifyUser");

        boolean result = true;
        try {
            userToVerify.setSecretCode(null);
            userToVerify.setSignUpCompleted(true);
            userRepository.save(userToVerify);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    public User findByEmailAndSecretCode(String email, String secretCode) {
        logger.debug("findByEmailAndSecretCode");
        return userRepository.findByEmailAndSecretCode(email, secretCode);
    }

    @Transactional
    public ResponseDTO recoverPassword(User user) {
        logger.debug("recoverPassword");

        // 1. Set random secret word.
        user.setSecretCode(YamoUtils.generateRandomSecretCode());
        user = userRepository.save(user);

        // 2. Send email.
        awsSESService.sendRecoverPasswordEmail(user);

        return ResponseDTO.statusOK();
    }

    @Transactional
    public User resetPassword(User user, String newPassword) {
        logger.debug("resetPassword");
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setSecretCode(null);
        user.setSignUpCompleted(true);
        return userRepository.save(user);
    }

    public ProfileDTO getUserProfile(User authUser, User existingUser, boolean ownProfile) {
        logger.debug("getUserProfile");

        ProfileDTO profile = new ProfileDTO();

        // 1. User information.
        profile.setProfileImageUrl(existingUser.getProfileImageUrl());
        profile.setFirstName(existingUser.getFirstName());
        profile.setLastName(existingUser.getLastName());
        profile.setNickname(existingUser.getNickname());
        profile.setNicknameEnabled(existingUser.isNickNameEnabled());
        profile.setLocation(existingUser.getLocation());
        profile.setVisible(existingUser.isVisible());
        profile.setReported(false);
        profile.setUserFollowing(false);
        profile.setOwnProfile(ownProfile);

        if (existingUser.isVisible() || ownProfile) {

            // 2. Venues.
            List<VenueSummary> lstVenues = new ArrayList<>();

            // 2.1. Popular venues.
            List<VenueSummary> popularVenueList = venueSummaryRepository.findPopularVenuesForProfile(existingUser.getUuid());
            if (!popularVenueList.isEmpty()) {
                for (VenueSummary vs : popularVenueList) {
                    vs.setPopularVenue(true);
                }
                lstVenues.addAll(popularVenueList);
            }

            // 2.2. Sorted venue list.
            lstVenues.addAll(venueSummaryRepository.findVenuesForProfile(existingUser.getUuid()));

            List<VenueSummary> finalVenueSummaryList = new ArrayList<>(new HashSet<>(lstVenues));
            if (null != finalVenueSummaryList) {
                Collections.sort(finalVenueSummaryList, new VenueSummaryComparator());
            }

            profile.setVenues(finalVenueSummaryList);

            // 3. Followers and following.
            profile.setFollowers(userSummaryRepository.findFollowersForProfile(authUser.getUuid(), existingUser.getUuid()));
            profile.setFollowing(userSummaryRepository.findFollowingForProfile(authUser.getUuid(), existingUser.getUuid()));

            // 4. Routes.
            List<RouteSummary> lstRoutes = new ArrayList<>();

            // 4.1. Popular routes.
            List<RouteSummary> popularRouteList = routeSummaryRepository.findPopularRoutesForProfile(existingUser.getUuid());
            if (!popularRouteList.isEmpty()) {
                for (RouteSummary rs : popularRouteList) {
                    rs.setPopularRoute(true);
                }
                lstRoutes.addAll(popularRouteList);
            }

            // 4.2. Sorted route list.
            lstRoutes.addAll(routeSummaryRepository.findRoutesForProfile(existingUser.getUuid()));

            profile.setRoutes(lstRoutes);

            // 5. Mediums.
            profile.setMediums(new ArrayList<>(existingUser.getMediums()));

            // 6. Places.
            List<Place> places = placeRepository.findByUser(existingUser);
            profile.setPlaces(null == places ? new ArrayList<Place>() : places);

        } else {
            profile.setVenues(new ArrayList<VenueSummary>());
            profile.setFollowers(new ArrayList<UserSummary>());
            profile.setRoutes(new ArrayList<RouteSummary>());
            profile.setMediums(new ArrayList<Medium>());
        }

        return profile;
    }

    public ProfileDTO getOtherProfile(User authUser, User existingUser) {
        logger.debug("getOtherProfile");

        boolean ownProfile = existingUser.equals(authUser);

        ProfileDTO profile = getUserProfile(authUser, existingUser, ownProfile);

        // 1. User information.
        profile.setReported(null != existingUser.getReports() && existingUser.getReports().contains(authUser));
        profile.setUserFollowing(null != existingUser.getFollowers() && existingUser.getFollowers().contains(authUser));
        profile.setOwnProfile(ownProfile);

        return profile;
    }

    @Transactional
    public User editProfile(User existingUser, EditProfileDTO editProfileDTO) {

        logger.debug("editProfile");

        // Profile image.
        if (YamoUtils.stringIsNotNullAndNotEmpty(editProfileDTO.getProfileImageContent())) {
            String newImageUrl = awsS3Service.storeFileDataForUser(editProfileDTO.getProfileImageContent(), String.valueOf(existingUser.getUuid()));
            existingUser.setProfileImageUrl(newImageUrl);
        }

        // Nickname
        if (YamoUtils.stringIsNotNullAndNotEmpty(editProfileDTO.getNickname())) {
            existingUser.setNickname(editProfileDTO.getNickname().trim());
        }

        // Nickname enabled
        existingUser.setNickNameEnabled(editProfileDTO.isNickNameEnabled());

        // First name
        if (YamoUtils.stringIsNotNullAndNotEmpty(editProfileDTO.getFirstName())) {
            existingUser.setFirstName(editProfileDTO.getFirstName().trim());
        }

        // Last name
        if (YamoUtils.stringIsNotNullAndNotEmpty(editProfileDTO.getLastName())) {
            existingUser.setLastName(editProfileDTO.getLastName().trim());
        }

        // City
        if (YamoUtils.stringIsNotNullAndNotEmpty(editProfileDTO.getCity())) {
            existingUser.setCity(editProfileDTO.getCity().trim());
        }

        // Visibility
        existingUser.setVisible(editProfileDTO.isVisible());

        // Sign up completed.
        existingUser.setSignUpCompleted(editProfileDTO.isSignUpCompleted());

        return userRepository.save(existingUser);
    }

    @Transactional
    public User updateUserLocation(User existingUser, LocationDTO locationDTO) {

        logger.debug("updateUserLocation");

        if (YamoUtils.isLocationValid(locationDTO)) {
            existingUser.setLat(locationDTO.getLat());
            existingUser.setLon(locationDTO.getLon());
            existingUser.setLocation(locationDTO.getLocation());
            return userRepository.save(existingUser);
        } else {
            return null;
        }

    }

    public User findUserById(Long userId) {
        return userRepository.findOne(userId);
    }

    @Transactional
    public ResponseDTO reportUser(User authUser, User existingUser) {
        logger.debug("reportUser");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            if (null == existingUser.getReports()) {
                existingUser.setReports(new HashSet<User>());
            }
            existingUser.getReports().add(authUser);
            if (YamoUtils.NUM_REPORTED_USERS_NEEDED <= existingUser.getReportsCount()) {
                // User must be deactivated.
                existingUser.setEnabled(false);
            }

            userRepository.save(existingUser);

        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;
    }

    public ResponseDTO followUser(User authUser, User existingUser) {
        logger.debug("followUser");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            if (null == existingUser.getFollowers()) {
                existingUser.setFollowers(new HashSet<User>());
            }
            existingUser.getFollowers().add(authUser);
            userRepository.save(existingUser);
        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;
    }

    public ResponseDTO unFollowUser(User authUser, User existingUser) {
        logger.debug("unFollowUser");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            if (null == existingUser.getFollowers()) {
                existingUser.setFollowers(new HashSet<User>());
            } else {
                existingUser.getFollowers().remove(authUser);
                userRepository.save(existingUser);
            }

        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;
    }

    @Transactional
    public User createNewUserFromGuestFacebook(User authUser, String firstName, String lastName, String email, String facebookId, String gender, byte[] profileImage) {
        logger.debug("createNewUserFromFacebookTransactional");

        User newUser = authUser;
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(new BCryptPasswordEncoder().encode(String.format("%s%s", facebookId, YamoUtils.USER_SOCIAL_SECRET_WORD)));
        newUser.setSecretCode(YamoUtils.generateRandomSecretCode());
        newUser.setFacebookId(facebookId);
        newUser.setProfileImageUrl(awsS3Service.storeFileDataForUser(profileImage, facebookId));

        newUser.setCity(YamoUtils.DEFAULT_CITY);
        newUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
        newUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
        newUser.setLocation(YamoUtils.DEFAULT_CITY);

        newUser.setEnabled(true);
        newUser.setVisible(true);
        newUser.setNickname(null);
        newUser.setNickNameEnabled(false);
        newUser.setSignUpCompleted(false);
        newUser.setReady(false);
        newUser.setUserType(UserType.USER);

        newUser = userRepository.save(newUser);

        // TODO: Send email through AWS SES.

        return newUser;
    }

    public User createNewUserFromFacebook(String firstName, String lastName, String email, String facebookId, String gender, byte[] profileImage, Facebook facebook) {
        logger.debug("createNewUserFromFacebook");

        User newUser = createNewUserFromFacebookTransactional(firstName, lastName, email, facebookId, gender, profileImage);

        try {
            // Find friends on facebook.
            PagedList<String> lstFriendFBIds = facebook.friendOperations().getFriendIds();
            if (null != lstFriendFBIds && !lstFriendFBIds.isEmpty()) {
                List<User> targetUsers = userRepository.findByFacebookIdIn(lstFriendFBIds);
                if (null != targetUsers && !targetUsers.isEmpty()) {
                    awsSNSService.sendNotification(
                            NotificationType.FACEBOOK_FRIEND_JOINED,
                            newUser,
                            targetUsers,
                            null,
                            true,
                            true,
                            ""
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Could not send notifications.");
        }

        return newUser;
    }

    @Transactional
    public User createNewUserFromFacebookTransactional(String firstName, String lastName, String email, String facebookId, String gender, byte[] profileImage) {
        logger.debug("createNewUserFromFacebookTransactional");

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(new BCryptPasswordEncoder().encode(String.format("%s%s", facebookId, YamoUtils.USER_SOCIAL_SECRET_WORD)));
        newUser.setSecretCode(YamoUtils.generateRandomSecretCode());
        newUser.setFacebookId(facebookId);
        newUser.setProfileImageUrl(awsS3Service.storeFileDataForUser(profileImage, facebookId));

        newUser.setCity(YamoUtils.DEFAULT_CITY);
        newUser.setLat(YamoUtils.DEFAULT_LOCATION_LAT);
        newUser.setLon(YamoUtils.DEFAULT_LOCATION_LON);
        newUser.setLocation(YamoUtils.DEFAULT_CITY);

        newUser.setEnabled(true);
        newUser.setVisible(true);
        newUser.setNickname(null);
        newUser.setNickNameEnabled(false);
        newUser.setSignUpCompleted(false);
        newUser.setReady(false);
        newUser.setUserType(UserType.USER);

        return userRepository.save(newUser);
    }

    public Device registerDevice(User existingUser, DeviceDTO deviceDTO) {

        logger.debug("registerDevice");

        // 1. Find if the device already exists (for user, platform and token).
        NotificationPlatform platform = NotificationPlatform.fromInt(deviceDTO.getPlatform());
        Device device = deviceRepository.findByUserAndPlatformAndToken(existingUser, platform, deviceDTO.getToken());

        // 2. If not, create and save.
        if (null == device) {
            device = new Device();
            device.setUser(existingUser);
            device.setPlatform(platform);
            device.setToken(deviceDTO.getToken());
            device = deviceRepository.save(device);
        }

        return device;
    }

    public void sendTestPush(User authUser) {
        awsSNSService.sendNotification(
                NotificationType.UNSPECIFIED,
                authUser,
                Collections.singletonList(authUser),
                null,
                false,
                true,
                null
        );
    }

    public CountersDTO getCounters(User authUser) {
        CountersDTO countersDTO = new CountersDTO();

        countersDTO.setNotificationCount((int) notificationUnseenRepository.countByUserId(authUser.getUuid()));

        return countersDTO;
    }

    public List<Notification> findNotificationsForUser(User authUser, Date from, boolean older) {
        logger.debug("findNotificationsForUser");
        List<Notification> lstNotifications;
        if (older) {
            lstNotifications = notificationRepository.findFirst10ByUserAndCreatedLessThanOrderByCreatedDesc(authUser, from);
        } else {
            lstNotifications = notificationRepository.findFirst10ByUserAndCreatedGreaterThanOrderByCreatedAsc(authUser, from);
            // order desc.
            Collections.sort(lstNotifications, new NotificationCreatedDescComparator());
        }

        // Add user info.
        if (null != lstNotifications && !lstNotifications.isEmpty()) {
            User referencedUser = null;
            for (Notification n : lstNotifications) {
                if (NotificationType.FACEBOOK_FRIEND_JOINED.equals(n.getType())) {
                    referencedUser = userRepository.findOne(n.getAssociatedUser());
                    n.setFollowing(null != referencedUser.getFollowers() && referencedUser.getFollowers().contains(authUser));
                }
            }
        }

        return lstNotifications;
    }

    public Notification findSingleNotificationForUser(User authUser, Long notificationId) {
        return notificationRepository.findByUuidAndUser(notificationId, authUser);
    }

    public ResponseDTO markNotificationAsRead(User authUser, Long notificationId) {

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            notificationUnseenRepository.deleteByUuidAndUserId(notificationId, authUser.getUuid());
        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;
    }

    @Transactional
    public ResponseDTO deleteNotificationForUser(User authUser, Notification existingNotification) {
        ResponseDTO responseDTO = ResponseDTO.statusOK();

        try {
            // 1. Delete possible unread status.
            notificationUnseenRepository.deleteByUuidAndUserId(existingNotification.getUuid(), authUser.getUuid());

            // 2. Delete notification.
            notificationRepository.delete(existingNotification);

        } catch (Exception e) {
            responseDTO = ResponseDTO.statusNoOK();
        }

        return responseDTO;
    }

    public List<UserLabelNormalisedValue> findNormalisedTagsForUser(Long userId) {
        return userLabelNormalisedValueRepository.findByUserId(userId);
    }

    public Page<User> pageUsers(int page, int count, Sort.Direction direction, String sortProperty) {
        return userRepository.findAll(new PageRequest(page, count, direction, sortProperty));
    }

    public List<User> pageUsers(Long timestamp, boolean older) {

        List<User> result;
        Date from = new Date(timestamp);

        if (older) {
            result = userRepository.findFirst10ByCreatedLessThanOrderByCreatedDesc(from);
        } else {
            result = userRepository.findFirst10ByCreatedGreaterThanOrderByCreatedAsc(from);
            // order desc.
            Collections.sort(result, new UserCreatedDescComparator());
        }

        return result;
    }

    public boolean proceedWithAppleSubscription(Long userId, String uniqueIdentifier) {
        UserSubscription userSubscription = userSubscriptionRepository.findSubscriptionByUniqueIdentifier(uniqueIdentifier);
        return ((null == userSubscription) || (userSubscription.getAssociatedUser().getUuid().equals(userId)));
    }

    @Transactional
    public UserSubscription upgradeAccountForApple(User signedInUser, Date subscriptionStart, Date subscriptionEnd, String updatedReceiptJson, String receiptUniqueIdentifier, String receiptData) {
        logger.debug("upgradeAccountForApple");

        // 1. Change user type.
        if (subscriptionEnd.after(YamoUtils.now())) {
            signedInUser.setUserType(UserType.USER);

            // 2. Find subscription and update / create
            UserSubscription subscription = userSubscriptionRepository.findByAssociatedUser(signedInUser);
            if (null == subscription) {
                subscription = new UserSubscription();
                subscription.setAssociatedUser(signedInUser);
            }

            subscription.setType(SubscriptionType.APPLE);
            subscription.setLatestAppleReceipt(updatedReceiptJson);
            subscription.setStartDate(subscriptionStart);
            subscription.setEndDate(subscriptionEnd);
            subscription.setUniqueIdentifier(receiptUniqueIdentifier);
            subscription.setReceiptData(receiptData);

            return userSubscriptionRepository.save(subscription);
        } else {
            return null;
        }

    }

}
