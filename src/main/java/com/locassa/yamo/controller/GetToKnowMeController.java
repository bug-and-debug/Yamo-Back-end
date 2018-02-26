package com.locassa.yamo.controller;

import com.locassa.yamo.exception.ArtWorkNotFoundException;
import com.locassa.yamo.exception.UserNotFoundException;
import com.locassa.yamo.exception.ValidationException;
import com.locassa.yamo.model.ArtWork;
import com.locassa.yamo.model.User;
import com.locassa.yamo.model.dto.CardReplyDTO;
import com.locassa.yamo.model.dto.ProfileDTO;
import com.locassa.yamo.model.dto.ResponseDTO;
import com.locassa.yamo.service.GetToKnowMeService;
import com.locassa.yamo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card")
@Api(description = "Endpoints to manage cards.")
public class GetToKnowMeController {

    private static final Logger logger = Logger.getLogger(GetToKnowMeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private GetToKnowMeService getToKnowMeService;

    @ApiOperation(value = "Get card suggestions", notes = "It returns a list of cards for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/suggestions", method = RequestMethod.GET)
    public List<ArtWork> getSuggestionsForUser(@RequestParam(value = "timestamp", required = false, defaultValue = "0") Long timestamp) {

        logger.debug("getSuggestionsForUser");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Get suggestions.
        if (0 == timestamp) {
            return getToKnowMeService.getSuggestionsForUser(authUser);
        } else {
            return getToKnowMeService.getSuggestionsForUserPaged(authUser, timestamp);
        }

    }

    @ApiOperation(value = "Rate card", notes = "It rates the specified card for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/rate", method = RequestMethod.POST)
    public ResponseDTO rateCard(@RequestBody CardReplyDTO cardReplyDTO) {

        logger.debug("getSuggestionsForUser");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Check the rating.
        int rating = cardReplyDTO.getRating();
        if (rating < 1 || rating > 5) {
            throw new ValidationException("rating", String.valueOf(rating));
        }

        // 3. Check the artwork.
        Long artWorkId = cardReplyDTO.getArtWorkId();
        ArtWork existingArtWork = getToKnowMeService.getArtWorkById(artWorkId);
        if (null == existingArtWork) {
            throw new ArtWorkNotFoundException(artWorkId);
        }

        // 4. Rate artwork.
        return getToKnowMeService.rateArtWork(authUser, existingArtWork, rating);
    }


}
