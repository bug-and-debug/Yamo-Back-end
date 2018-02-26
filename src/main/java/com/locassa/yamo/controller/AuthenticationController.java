package com.locassa.yamo.controller;

import com.locassa.yamo.OAuth2SecurityConfig;
import com.locassa.yamo.exception.*;
import com.locassa.yamo.model.User;
import com.locassa.yamo.model.dto.*;
import com.locassa.yamo.service.UserService;
import com.locassa.yamo.service.aws.AwsS3Service;
import com.locassa.yamo.util.YamoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.ImageType;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/authentication")
@Api(description = "Endpoints to manage authentication.")
@Order(99)
public class AuthenticationController {

    private static final Logger logger = Logger.getLogger(AuthenticationController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OAuth2SecurityConfig.AuthorizationServerConfig authorizationServerConfig;

    @Autowired
    private AwsS3Service awsS3Service;

    @ApiOperation(value = "Sign out", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It signs the current user out. You must specify the REFRESH token for the server to revoke them.")
    @RequestMapping(value = "/sign-out", method = RequestMethod.PUT)
    public ResponseDTO signOut(HttpServletRequest request, @RequestBody StringDTO stringDTO) {

        logger.debug("signOut");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            userService.deleteUserCredentials(request);
            TokenStore tokenStore = authorizationServerConfig.getTokenStore();
            OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(stringDTO.getValue());
            tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
            tokenStore.removeRefreshToken(refreshToken);
        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;
    }

    @ApiOperation(value = "Sign in", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It signs the user in with the specified credentials.")
    @RequestMapping(value = "/sign-in", method = RequestMethod.POST)
    public User signIn(@RequestBody CredentialsDTO credentialsDTO) {

        logger.debug("signIn");
        String userEmail = credentialsDTO.getEmail();
        String userPassword = credentialsDTO.getPassword();

        // 1. Validate the email.
        if (!YamoUtils.isEmailValid(userEmail)) {
            throw new ValidationException("email", userEmail);
        }

        // 2. Find the user.
        User existingUser = userService.findUserByEmail(userEmail);
        if (null == existingUser) {
            throw new UserNotFoundException(userEmail);
        }

        // 3. Authenticate.
        if (!userService.authenticateUser(existingUser.getPassword(), userPassword)) {
            throw new WrongCredentialsException();
        }

        return existingUser;
    }

    @ApiOperation(value = "Sign up", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It creates a new user with the specified parameters.")
    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public User signUp(@RequestBody SignUpDTO signUpDTO) {

        logger.debug("signUp");
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
        return userService.createNewUser(signUpDTO);

    }

    @ApiOperation(value = "Complete pending sign up (URL)", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It completes a pending sign up process with the specified secret code.")
    @RequestMapping(value = "/verify/{secretCode}", method = RequestMethod.GET)
    public ResponseDTO completePendingSignUp(@PathVariable(value = "secretCode") String secretCode) {

        logger.debug("completePendingSignUp");

        // 1. Find the user.
        User userToVerify = userService.findBySecretCode(secretCode);

        // 2. Verify user.
        boolean success = false;
        if (null != userToVerify) {
            success = userService.verifyUser(userToVerify);
        }

        return success ? ResponseDTO.statusOK() : ResponseDTO.statusNoOK();

    }

    @ApiOperation(value = "Complete pending sign up (email)", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It completes a pending sign up process with the specified email and secret code.")
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ResponseDTO completePendingSignUp(@RequestBody VerificationDTO verificationDTO) {

        logger.debug("completePendingSignUp");
        String userEmail = verificationDTO.getEmail();

        // 1. Validate the email.
        if (!YamoUtils.isEmailValid(userEmail)) {
            throw new ValidationException("email", userEmail);
        }

        // 2. Find the user.
        User userToVerify = userService.findByEmailAndSecretCode(userEmail, verificationDTO.getSecretCode());

        // 3. Verify user.
        boolean success = false;
        if (null != userToVerify) {
            success = userService.verifyUser(userToVerify);
        }

        return success ? ResponseDTO.statusOK() : ResponseDTO.statusNoOK();

    }

    @ApiOperation(value = "Recover password", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It prepares a user to recover the password.")
    @RequestMapping(value = "/password/recover", method = RequestMethod.POST)
    public ResponseDTO recoverPassword(@RequestBody StringDTO stringDTO) {

        logger.debug("recoverPassword");
        String userEmail = stringDTO.getValue();

        // 1. Validate the email.
        if (!YamoUtils.isEmailValid(userEmail)) {
            throw new ValidationException("email", userEmail);
        }

        // 2. Find the user.
        User user = userService.findUserByEmail(userEmail);
        if (null == user) {
            throw new UserNotFoundException(userEmail);
        }

        // 3. Recover the password.
        return userService.recoverPassword(user);

    }

    @ApiOperation(value = "Reset password", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It sets a new password for the specified user.")
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    public User resetPassword(@RequestBody RecoverPasswordDTO recoverPasswordDTO) {

        logger.debug("resetPassword");
        String userEmail = recoverPasswordDTO.getEmail();
        String userPassword = recoverPasswordDTO.getPassword();
        String secretCode = recoverPasswordDTO.getSecretCode();

        // 1. Validate the email.
        if (!YamoUtils.isEmailValid(userEmail)) {
            throw new ValidationException("email", userEmail);
        }

        // 2. Validate password.
        if (!YamoUtils.isPasswordValid(userPassword)) {
            throw new ValidationException("password", "***");
        }

        // 3. Find the user.
        User user = userService.findByEmailAndSecretCode(userEmail, secretCode);
        if (null == user) {
            throw new UserNotFoundException(userEmail);
        }

        // 4. Reset password.
        return userService.resetPassword(user, userPassword);

    }

    @ApiOperation(value = "Check verification code", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It checks whether the specified code and email are correct or not.")
    @RequestMapping(value = "/code/check", method = RequestMethod.POST)
    public BooleanDTO checkVerificationCode(@RequestBody VerifyCodeDTO verifyCodeDTO) {

        logger.debug("checkVerificationCode");
        String userEmail = verifyCodeDTO.getEmail();
        String secretCode = verifyCodeDTO.getSecretCode();

        // 1. Validate the email.
        if (!YamoUtils.isEmailValid(userEmail)) {
            throw new ValidationException("email", userEmail);
        }

        // 2. Find the user.
        User user = userService.findByEmailAndSecretCode(userEmail, secretCode);

        // 3. Return result.
        return new BooleanDTO(null != user);
    }

    @ApiOperation(value = "Re-send confirmation email", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It changes the secret code and re-sends an email to the user.")
    @RequestMapping(value = "/verify/re-send", method = RequestMethod.POST)
    public ResponseDTO resendConfirmationEmail(@RequestBody StringDTO stringDTO) {

        logger.debug("resendConfirmationEmail");
        String userEmail = stringDTO.getValue();

        // 1. Find the user.
        User existingUser = userService.findUserByEmail(userEmail);
        if (null == existingUser) {
            throw new UserNotFoundException(userEmail);
        }

        // 2. The user must not be verified.
        if (existingUser.isSignUpCompleted()) {
            throw new UserAlreadyVerifiedException(userEmail);
        }

        return userService.recoverPassword(existingUser);
    }

    @ApiOperation(value = "Check user's report status", notes = "It checks if the specified user has been reported (by 3 different people or more) or not.")
    @RequestMapping(value = "/report/status", method = RequestMethod.PUT)
    public BooleanDTO checkReportedStatus(@RequestBody StringDTO stringDTO) {
        logger.debug("checkReportedStatus");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing user.
        User existingUser = userService.findUserByEmail(stringDTO.getValue());
        if (null == existingUser) {
            throw new UserNotFoundException(stringDTO.getValue());
        }

        return new BooleanDTO(YamoUtils.NUM_REPORTED_USERS_NEEDED <= existingUser.getReportsCount());
    }

    @ApiOperation(value = "Connect with Facebook", notes = "It authenticates a user using their FB account. The access token provided by Facebook is needed.")
    @RequestMapping(value = "/connect/facebook", method = RequestMethod.POST)
    public User connectWithFacebook(@RequestBody StringDTO stringDTO) {
        logger.debug("connectWithFacebook");
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
                    return userService.createNewUserFromFacebook(firstName, lastName, email, facebookId, gender, userProfilePicture, facebook);

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

    @ApiOperation(value = "Create guest account", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It creates a new user with the specified parameters.")
    @RequestMapping(value = "/guest", method = RequestMethod.POST)
    public User createGuestAccount() {

        logger.debug("createGuestAccount");

        // 4. Create guest user.
        return userService.createGuestUser();

    }

}
