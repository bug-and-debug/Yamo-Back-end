package com.locassa.yamo.controller;

import com.locassa.yamo.exception.*;
import com.locassa.yamo.model.*;
import com.locassa.yamo.model.dto.*;
import com.locassa.yamo.model.enums.PlaceType;
import com.locassa.yamo.model.summary.VenueSearchSummary;
import com.locassa.yamo.service.UserService;
import com.locassa.yamo.service.VenueService;
import com.locassa.yamo.util.YamoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/venue")
@Api(description = "Endpoints to manage venues.")
public class VenueController {

    private static final Logger logger = Logger.getLogger(VenueController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VenueService venueService;

    @ApiOperation(value = "Add venue to favourites", notes = "It adds the specified venue to the authenticated user's favourites.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{venueId}/favourite", method = RequestMethod.POST)
    public ResponseDTO addVenueToFavourites(@PathVariable("venueId") Long venueId) {

        logger.debug("addVenueToFavourites");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing venue.
        Venue existingVenue = venueService.findVenueById(venueId);
        if (null == existingVenue) {
            throw new VenueNotFoundException(venueId);
        }

        // 3. Check that the auth user did not mark the existing venue as favourite yet.
        if (null != existingVenue.getFavouriteUsers() && existingVenue.getFavouriteUsers().contains(authUser)) {
            throw new UserMarkedVenueAsFavouriteException(authUser.getEmail(), existingVenue.getName());
        }

        // 4. Mark venue as favourite.
        return venueService.markVenueAsFavourite(authUser, existingVenue);
    }

    @ApiOperation(value = "Remove venue from favourites", notes = "It removes the specified venue from the authenticated user's favourites.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{venueId}/favourite", method = RequestMethod.DELETE)
    public ResponseDTO removeVenueFromFavourites(@PathVariable("venueId") Long venueId) {

        logger.debug("removeVenueFromFavourites");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing venue.
        Venue existingVenue = venueService.findVenueById(venueId);
        if (null == existingVenue) {
            throw new VenueNotFoundException(venueId);
        }

        // 3. Check that the auth user marked the existing venue as favourite before.
        if (null != existingVenue.getFavouriteUsers() && !existingVenue.getFavouriteUsers().contains(authUser)) {
            throw new UserMarkedVenueAsFavouriteException(authUser.getEmail(), existingVenue.getName(), null);
        }

        // 4. Mark venue as favourite.
        return venueService.unMarkVenueAsFavourite(authUser, existingVenue);
    }

    @ApiOperation(value = "Mark venue as interesting", notes = "It marks the specified venue as interesting for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{venueId}/interesting", method = RequestMethod.POST)
    public ResponseDTO markVenueAsInteresting(@PathVariable("venueId") Long venueId) {

        logger.debug("markVenueAsInteresting");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing venue.
        Venue existingVenue = venueService.findVenueById(venueId);
        if (null == existingVenue) {
            throw new VenueNotFoundException(venueId);
        }

        // 3. Check that the auth user did not mark the existing venue as interesting yet.
        if (null != existingVenue.getFavouriteUsers() && existingVenue.getLikeUsers().contains(authUser)) {
            throw new UserMarkedVenueAsInterestingException(authUser.getEmail(), existingVenue.getName());
        }

        // 4. Mark venue as favourite.
        return venueService.markVenueAsInteresting(authUser, existingVenue);
    }

    @ApiOperation(value = "Un-mark venue as interesting", notes = "It un-marks the specified venue as interesting for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{venueId}/interesting", method = RequestMethod.DELETE)
    public ResponseDTO unMarkVenueAsInteresting(@PathVariable("venueId") Long venueId) {

        logger.debug("unMarkVenueAsInteresting");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing venue.
        Venue existingVenue = venueService.findVenueById(venueId);
        if (null == existingVenue) {
            throw new VenueNotFoundException(venueId);
        }

        // 3. Check that the auth user marked the existing venue as interesting before.
        if (null != existingVenue.getFavouriteUsers() && !existingVenue.getLikeUsers().contains(authUser)) {
            throw new UserMarkedVenueAsInterestingException(authUser.getEmail(), existingVenue.getName(), null);
        }

        // 4. Mark venue as favourite.
        return venueService.unMarkVenueAsInteresting(authUser, existingVenue);
    }

    @ApiOperation(value = "Save route", notes = "It saves the specified route for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/route/save", method = RequestMethod.POST)
    public Route saveRoute(@RequestBody RouteDTO routeDTO) {

        logger.debug("saveRoute");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Check route name.
        if (YamoUtils.stringIsNullOrEmpty(routeDTO.getName())) {
            throw new ValidationException("name", routeDTO.getName());
        }

        // 3. Check step numbers are not duplicated.
        List<Integer> stepNumbers = new ArrayList<>();
        List<RouteStep> lstRealSteps = new ArrayList<>();

        if (null == routeDTO.getSteps() || routeDTO.getSteps().isEmpty()) {
            throw new ValidationException("steps", "(empty list)");
        } else {
            RouteStep tempRouteStep = null;
            Venue tempVenue = null;
            for (RouteStepDTO rsd : routeDTO.getSteps()) {
                if (stepNumbers.contains(rsd.getSequenceOrder())) {
                    throw new ValidationException("sequenceOrder", "(duplicate values)");
                } else {
                    stepNumbers.add(rsd.getSequenceOrder());

                    tempRouteStep = new RouteStep();
                    tempRouteStep.setSequenceOrder(rsd.getSequenceOrder());
                    tempVenue = venueService.findVenueById(rsd.getVenueId());
                    if (null == tempVenue) {
                        throw new VenueNotFoundException(rsd.getVenueId());
                    }
                    tempRouteStep.setVenue(tempVenue);
                    lstRealSteps.add(tempRouteStep);
                }
            }
        }

        return venueService.saveRoute(authUser, routeDTO.getName(), lstRealSteps);
    }

    @ApiOperation(value = "Edit route", notes = "It modifies the specified route for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/route/{routeId}", method = RequestMethod.PUT)
    public Route modifyRoute(@PathVariable("routeId") Long routeId, @RequestBody RouteDTO routeDTO) {

        logger.debug("modifyRoute");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing route.
        Route existingRoute = venueService.findRouteById(routeId);
        if (null == existingRoute) {
            throw new RouteNotFoundException(routeId);
        }

        // 3. Check that the existing route owner is the authenticated user.
        if (!authUser.equals(existingRoute.getUser())) {
            throw new OwnershipException(Route.class, String.valueOf(routeId), authUser);
        }

        // 4. Check step numbers are not duplicated.
        List<Integer> stepNumbers = new ArrayList<>();
        List<RouteStep> lstRealSteps = new ArrayList<>();

        if (null == routeDTO.getSteps() || routeDTO.getSteps().isEmpty()) {
            throw new ValidationException("steps", "(empty list)");
        } else {
            RouteStep tempRouteStep = null;
            Venue tempVenue = null;
            for (RouteStepDTO rsd : routeDTO.getSteps()) {
                if (stepNumbers.contains(rsd.getSequenceOrder())) {
                    throw new ValidationException("sequenceOrder", "(duplicate values)");
                } else {
                    stepNumbers.add(rsd.getSequenceOrder());

                    tempRouteStep = new RouteStep();
                    tempRouteStep.setSequenceOrder(rsd.getSequenceOrder());
                    tempVenue = venueService.findVenueById(rsd.getVenueId());
                    if (null == tempVenue) {
                        throw new VenueNotFoundException(rsd.getVenueId());
                    }
                    tempRouteStep.setVenue(tempVenue);
                    lstRealSteps.add(tempRouteStep);
                }
            }
        }

        return venueService.modifyRoute(existingRoute, routeDTO.getName(), lstRealSteps);
    }

    @ApiOperation(value = "Save place", notes = "It saves the specified place for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/place/save", method = RequestMethod.POST)
    public Place savePlace(@RequestBody SavePlaceDTO savePlaceDTO) {

        logger.debug("savePlace");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Check place type.
        PlaceType type = PlaceType.fromInt(savePlaceDTO.getPlaceType());
        if (PlaceType.UNSPECIFIED.equals(type)) {
            throw new ValidationException("placeType", String.valueOf(savePlaceDTO.getPlaceType()));
        }

        // 3. Check place location.
        if (!YamoUtils.isLocationValid(savePlaceDTO.getLat(), savePlaceDTO.getLon(), savePlaceDTO.getLocation())) {
            throw new ValidationException("location", String.format("(%s,%s) %s", savePlaceDTO.getLat(), savePlaceDTO.getLon(), savePlaceDTO.getLocation()));
        }

        return venueService.savePlace(authUser, savePlaceDTO);
    }

    @ApiOperation(value = "List places", notes = "It gets a list of places for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/place/list", method = RequestMethod.GET)
    public List<Place> listUserPlaces() {
        logger.debug("listUserPlaces");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find places.
        return venueService.listPlacesForUser(authUser);
    }

    @ApiOperation(value = "Get single route", notes = "It retrieves the specified route for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/route/{routeId}", method = RequestMethod.GET)
    public Route getSingleRoute(@PathVariable("routeId") Long routeId) {

        logger.debug("getSingleRoute");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing route.
        Route existingRoute = venueService.findRouteById(routeId);
        if (null == existingRoute) {
            throw new RouteNotFoundException(routeId);
        }

        return existingRoute;
    }

    @ApiOperation(value = "Remove route", notes = "It removes the specified route for the authenticated user.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/route/{routeId}", method = RequestMethod.DELETE)
    public ResponseDTO removeRoute(@PathVariable("routeId") Long routeId) {

        logger.debug("removeRoute");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing route.
        Route existingRoute = venueService.findRouteByIdAndUser(routeId, authUser);
        if (null == existingRoute) {
            throw new RouteNotFoundException(routeId);
        }

        // 3. Remove route.
        return venueService.removeRoute(existingRoute);
    }

    @ApiOperation(value = "Increase route counter", notes = "It increases the counter for the specified route and authenticated user. This endpoint must be called every time a user follows a route. This is needed to calculate popular routes.")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/route/{routeId}", method = RequestMethod.POST)
    public ResponseDTO increaseRouteCounter(@PathVariable("routeId") Long routeId) {

        logger.debug("increaseRouteCounter");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing route.
        Route existingRoute = venueService.findRouteByIdAndUser(routeId, authUser);
        if (null == existingRoute) {
            throw new RouteNotFoundException(routeId);
        }

        // 3. Increase route counter.
        return venueService.increaseRouteCounter(existingRoute);
    }

    @ApiOperation(value = "Get single gallery or exhibition", notes = "It gets the specified venue.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/{venueId}", method = RequestMethod.GET)
    public Venue getSingleVenue(@PathVariable("venueId") Long venueId) {

        logger.debug("getSingleVenue");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Find existing venue.
        Venue existingVenue = venueService.findVenueById(venueId);
        if (null == existingVenue) {
            throw new VenueNotFoundException(venueId);
        }

        // 3. Apply user info.
        venueService.addUserInfoForSingleVenue(authUser, existingVenue);

        return existingVenue;
    }

    @ApiOperation(value = "Search exhibitions", notes = "It gets a list of matching venues, ordered by relevance.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/search", method = RequestMethod.PUT)
    public List<VenueSearchSummary> searchExhibitions(@RequestBody SearchDTO searchDTO) {

        logger.debug("searchExhibitions");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Search
        return venueService.searchExhibitions(authUser, searchDTO);
    }

    @ApiOperation(value = "List exhibitions", notes = "It gets a list of nearby venues.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/list", method = RequestMethod.PUT)
    public List<VenueSearchSummary> listExhibitions(@RequestBody LocationRadiusDTO locationRadiusDTO) {

        logger.debug("listExhibitions");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Search
        return venueService.listExhibitions(authUser, locationRadiusDTO);
    }

    @ApiOperation(value = "Filter and search exhibitions", notes = "It gets a list of venues as a result of the filters provided and matching term.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/filter/search", method = RequestMethod.PUT)
    public List<VenueSearchSummary> filterAndSearchVenues(@RequestBody FilterSearchDTO filterSearchDTO) {

        logger.debug("filterAndSearchVenues");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Filter venues.
        if (null == filterSearchDTO.getSearch() || filterSearchDTO.getSearch().trim().isEmpty()) {
            return venueService.filterExhibitions(authUser, filterSearchDTO);
        } else {
            return venueService.filterAndSearchVenues(authUser, filterSearchDTO);
        }

    }

    @ApiOperation(value = "List tag groups", notes = "It gets a list of tag groups.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/tag/group/list", method = RequestMethod.PUT)
    public List<TagGroup> listTagGroups() {

        logger.debug("listTagGroups");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Filter venues.
        return venueService.listTagGroups();
    }

    @ApiOperation(value = "Filter and search exhibitions", notes = "It gets a list of venues as a result of the filters provided and matching term.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/filter/searchV2", method = RequestMethod.PUT)
    public List<VenueSearchSummary> filterAndSearchVenuesV2(@RequestBody FilterSearchDTO filterSearchDTO) {

        logger.debug("filterAndSearchVenues");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Filter venues.
        if (null == filterSearchDTO.getSearch() || filterSearchDTO.getSearch().trim().isEmpty()) {
            return venueService.filterExhibitions(authUser, filterSearchDTO);
        } else {
            return venueService.searchExhibitions_V2(authUser, filterSearchDTO);
        }

    }
    

    @ApiOperation(value = "Filter and search exhibitions", notes = "It gets a list of venues as a result of the filters provided and matching term.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/filter/favourite", method = RequestMethod.PUT)
    public List<VenueSearchSummary> filterAndFavouriteV2(@RequestBody FilterSearchDTO filterSearchDTO) {

        logger.debug("filterAndSearchVenues");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Filter venues.        
        return venueService.filterFavouriteExhibitions(authUser, filterSearchDTO);


    }

    @ApiOperation(value = "Filter and search exhibitions", notes = "It gets a list of venues as a result of the filters provided and matching term. This endpoint will return exhibitions at the same place.")
    @PreAuthorize("hasRole('GUEST')")
    @RequestMapping(value = "/filter/searchV3", method = RequestMethod.PUT)
    public List<VenueSearchSummary> filterAndSearchVenuesV3(@RequestBody FilterSearchDTO filterSearchDTO) {

        logger.debug("filterAndSearchVenuesV3");

        // 1. Find authenticated user.
        User authUser = userService.findAuthenticatedUser();
        if (null == authUser) {
            throw new UserNotFoundException();
        }

        // 2. Filter venues.
        if (null == filterSearchDTO.getSearch() || filterSearchDTO.getSearch().trim().isEmpty()) {
            return venueService.filterExhibitions2(authUser, filterSearchDTO);
        } else {
            return venueService.filterAndSearchVenuesV3(authUser, filterSearchDTO);
        }

    }

}
