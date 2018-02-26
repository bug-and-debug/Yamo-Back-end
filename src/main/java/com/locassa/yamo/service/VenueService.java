package com.locassa.yamo.service;

import com.locassa.yamo.model.*;
import com.locassa.yamo.model.dto.*;
import com.locassa.yamo.model.enums.PlaceType;
import com.locassa.yamo.model.enums.VenueType;
import com.locassa.yamo.model.summary.VenueSearchSummary;
import com.locassa.yamo.repository.*;
import com.locassa.yamo.util.VenueSearchSummaryRelevanceComparator;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Service
public class VenueService {

    private static final Logger logger = Logger.getLogger(VenueService.class);

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteStepRepository routeStepRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private VenueSearchSummaryRepository venueSearchSummaryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagGroupRepository tagGroupRepository;

    @Autowired
    private RelevanceService relevanceService;

    public Venue findVenueById(Long venueId) {
        return venueRepository.findOne(venueId);
    }

    public ResponseDTO markVenueAsFavourite(User authUser, Venue existingVenue) {

        logger.debug("markVenueAsFavourite");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            if (null == existingVenue.getFavouriteUsers()) {
                existingVenue.setFavouriteUsers(new HashSet<User>());
            }
            existingVenue.getFavouriteUsers().add(authUser);
            venueRepository.save(existingVenue);
        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;

    }

    public ResponseDTO unMarkVenueAsFavourite(User authUser, Venue existingVenue) {

        logger.debug("unMarkVenueAsFavourite");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            if (null == existingVenue.getFavouriteUsers()) {
                existingVenue.setFavouriteUsers(new HashSet<User>());
            } else {
                existingVenue.getFavouriteUsers().remove(authUser);
                venueRepository.save(existingVenue);
            }

        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;

    }

    public ResponseDTO markVenueAsInteresting(User authUser, Venue existingVenue) {

        logger.debug("markVenueAsInteresting");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            if (null == existingVenue.getLikeUsers()) {
                existingVenue.setLikeUsers(new HashSet<User>());
            }
            existingVenue.getLikeUsers().add(authUser);
            venueRepository.save(existingVenue);
        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;

    }

    public ResponseDTO unMarkVenueAsInteresting(User authUser, Venue existingVenue) {
        logger.debug("unMarkVenueAsInteresting");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            if (null == existingVenue.getLikeUsers()) {
                existingVenue.setLikeUsers(new HashSet<User>());
            } else {
                existingVenue.getLikeUsers().remove(authUser);
                venueRepository.save(existingVenue);
            }

        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;

    }

    @Transactional
    public Route saveRoute(User authUser, String routeName, List<RouteStep> steps) {
        logger.debug("saveRoute");

        Route newRoute = new Route();

        newRoute.setName(routeName);
        newRoute.setUser(authUser);
        for (RouteStep step : steps) {
            step.setParent(newRoute);
        }
        newRoute.setSteps(new TreeSet<>(steps));

        return routeRepository.save(newRoute);
    }

    @Transactional
    public Route modifyRoute(Route existingRoute, String routeName, List<RouteStep> steps) {
        logger.debug("modifyRoute");

        if (YamoUtils.stringIsNotNullAndNotEmpty(routeName)) {
            existingRoute.setName(routeName.trim());
        }

        SortedSet<RouteStep> stepsToDelete = existingRoute.getSteps();

        for (RouteStep step : steps) {
            step.setParent(existingRoute);
        }
        existingRoute.setSteps(new TreeSet<>(steps));

        Route modifiedRoute = routeRepository.save(existingRoute);

        if (null != stepsToDelete) {
            for (RouteStep rs : stepsToDelete) {
                routeStepRepository.delete(rs.getUuid());
            }
        }

        return modifiedRoute;
    }

    public Route findRouteById(Long routeId) {
        Route existingRoute = routeRepository.findOne(routeId);

//        if (null != existingRoute && null != existingRoute.getSteps()) {
//            Venue tempVenue;
//            for (RouteStep step : existingRoute.getSteps()) {
//                tempVenue = step.getVenue();
//                if (null != tempVenue && null != tempVenue.getTags()) {
//                    Collections.sort(tempVenue.getTags(), null);
//                }
//            }
//        }

        return existingRoute;
    }

    public Route findRouteByIdAndUser(Long routeId, User user) {
        return routeRepository.findByUuidAndUser(routeId, user);
    }

    public ResponseDTO removeRoute(Route existingRoute) {
        logger.debug("removeRoute");

        ResponseDTO response = ResponseDTO.statusOK();
        try {
            routeRepository.delete(existingRoute);
        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;
    }

    public ResponseDTO increaseRouteCounter(Route existingRoute) {
        logger.debug("increaseRouteCounter");

        ResponseDTO response = ResponseDTO.statusOK();

        try {
            existingRoute.setCounter(existingRoute.getCounter() + 1);
            routeRepository.save(existingRoute);
        } catch (Exception e) {
            response = ResponseDTO.statusNoOK();
        }

        return response;
    }

    @Transactional
    public Place savePlace(User authUser, SavePlaceDTO savePlaceDTO) {

        // 1. First find if place already exists.
        // Find by user and place type.
        PlaceType placeType = PlaceType.fromInt(savePlaceDTO.getPlaceType());
        List<Place> userPlaces = placeRepository.findByUserAndPlaceType(authUser, placeType);
        Place existingPlace = null;
        if (null != userPlaces && !userPlaces.isEmpty()) {
            existingPlace = userPlaces.get(0);
        }

        if (null == existingPlace) {
            existingPlace = new Place();
            existingPlace.setUser(authUser);
            existingPlace.setPlaceType(placeType);
        }

        existingPlace.setLat(savePlaceDTO.getLat());
        existingPlace.setLon(savePlaceDTO.getLon());
        existingPlace.setLocation(savePlaceDTO.getLocation());

        return placeRepository.save(existingPlace);
    }

    public List<Place> listPlacesForUser(User authUser) {
        List<Place> places = placeRepository.findByUser(authUser);
        return null == places ? new ArrayList<Place>() : places;
    }

    public List<VenueSearchSummary> searchExhibitions(User authUser, SearchDTO searchDTO) {

        if (0 == searchDTO.getMiles()) {
            searchDTO.setMiles(YamoUtils.DEFAULT_MILES_FOR_FILTER);
        }

        // 1. List venues.
        List<VenueSearchSummary> lstVenues = venueSearchSummaryRepository.findExhibitionsByNameContains(
                "%" + searchDTO.getSearch() + "%",
                searchDTO.getLat(),
                searchDTO.getLon(),
                searchDTO.getMiles(),
                YamoUtils.DISTANCE_UNIT_FACTOR_MILES,
                YamoUtils.now().getTime()
        );

        // 2. Add user data.
        addUserInfoToVenueList(lstVenues, searchDTO.getLat(), searchDTO.getLon(), authUser);

        // 3. Order
        orderVenuesByRelevance(lstVenues);

        return lstVenues;
    }

    public List<VenueSearchSummary> searchExhibitions_V2(User authUser, FilterSearchDTO searchDTO) {

        if (0 == searchDTO.getMiles()) {
            searchDTO.setMiles(YamoUtils.DEFAULT_MILES_FOR_FILTER);
        }

        // 1. List venues.
        List<VenueSearchSummary> lstVenues = venueSearchSummaryRepository.findExhibitionsByNameContains_V2(
                "%" + searchDTO.getSearch() + "%",
                searchDTO.getLat(),
                searchDTO.getLon(),
                searchDTO.getMiles(),
                YamoUtils.DISTANCE_UNIT_FACTOR_MILES,
                YamoUtils.now().getTime()
        );

        // 2. Add user data.
        addUserInfoToVenueList(lstVenues, searchDTO.getLat(), searchDTO.getLon(), authUser);

        // 3. Order
        orderVenuesByRelevance(lstVenues);

        return lstVenues;
    }
 
    public List<VenueSearchSummary> listExhibitions(User authUser, LocationRadiusDTO locationRadiusDTO) {

        // 1. List venues.
        List<VenueSearchSummary> lstVenues = venueSearchSummaryRepository.findNearbyExhibitions(
                locationRadiusDTO.getLat(),
                locationRadiusDTO.getLon(),
                locationRadiusDTO.getMiles(),
                YamoUtils.DISTANCE_UNIT_FACTOR_MILES,
                YamoUtils.now().getTime());

        // 2. Add user data.
        addUserInfoToVenueList(lstVenues, locationRadiusDTO.getLat(), locationRadiusDTO.getLon(), authUser);

        // 3. Order
        orderVenuesByRelevance(lstVenues);

        return lstVenues;
    }

    private void addUserInfoToVenueList(List<VenueSearchSummary> lstVenues, double lat, double lon, User authUser) {
        if (null != lstVenues && !lstVenues.isEmpty()) {
            for (VenueSearchSummary vss : lstVenues) {
                try {
                    String galleryName = venueRepository.findGalleryNameFromExhibitionId(vss.getUuid());
                    vss.setGalleryName(null == galleryName ? "" : galleryName);
                } catch (Exception e) {
                    vss.setGalleryName("");
                }
                vss.setDistance(YamoUtils.distFromInMiles(lat, lon, vss.getLat(), vss.getLon()));
                try {
                    vss.setRelevance(relevanceService.findRelevanceForUserAndVenue(authUser.getUuid(), vss.getUuid()));
                } catch (Exception e) {
                    vss.setRelevance(0);
                }
                vss.setRelevant(vss.getRelevance() >= YamoUtils.DEFAULT_RESULTS_FOR_RELEVANCE);

                // Set tags.
                vss.setTags(tagRepository.findTagsByVenueUuid(vss.getUuid()));
            }
        }
    }

    private void orderVenuesByRelevance(List<VenueSearchSummary> lstVenues) {
        if (null != lstVenues) {
            Collections.sort(lstVenues, new VenueSearchSummaryRelevanceComparator());
        }
    }

    public List<VenueSearchSummary> filterAndSearchVenues(User authUser, FilterSearchDTO filterSearchDTO) {

        // 1. Filter
        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setLat(filterSearchDTO.getLat());
        filterDTO.setLon(filterSearchDTO.getLon());
        filterDTO.setMostPopular(filterSearchDTO.isMostPopular());
        filterDTO.setPriceFilter(filterSearchDTO.getPriceFilter());
        filterDTO.setTagIds(filterSearchDTO.getTagIds());
        filterDTO.setMiles(filterSearchDTO.getMiles());
        List<VenueSearchSummary> lstFilters = filterExhibitions(authUser, filterDTO);

        // 2. Search
        List<VenueSearchSummary> lstFinalResults = new ArrayList<>();
        if (null != lstFilters && !lstFilters.isEmpty()) {
            for (VenueSearchSummary vss : lstFilters) {
                if (vss.getName().toLowerCase().contains(filterSearchDTO.getSearch().toLowerCase())) {
                    lstFinalResults.add(vss);
                }
            }
        }

        // 3. Order
        orderVenuesByRelevance(lstFinalResults);

        return lstFinalResults;

    }

    public List<VenueSearchSummary> filterExhibitions(User authUser, FilterDTO filterDTO) {

        List<VenueSearchSummary> lstResults = new ArrayList<>();

        // 1. Set flags for different filters.
        boolean filterPopular = filterDTO.isMostPopular();
        boolean filterTags = null != filterDTO.getTagIds() && !filterDTO.getTagIds().isEmpty();
        boolean filterPrice = 0 != filterDTO.getPriceFilter();

        double filterPriceMin = 0;
        double filterPriceMax = 0;

        switch (filterDTO.getPriceFilter()) {
            case 2:
                filterPriceMin = 0.01;
                filterPriceMax = 5.00;
                break;
            case 3:
                filterPriceMin = 5.01;
                filterPriceMax = 15.00;
                break;
            case 4:
                filterPriceMin = 15.01;
                filterPriceMax = 500.00;
                break;
        }

        // 2. Find most popular exhibitions.
        List<BigInteger> lstMostPopularVenueIds = null;
        if (filterPopular) {
            lstMostPopularVenueIds = venueSearchSummaryRepository.findMostPopularExhibitions();
            if (null == lstMostPopularVenueIds || lstMostPopularVenueIds.isEmpty()) {
                filterPopular = false;
            }
        }

        List<BigInteger> lstResultIds = new ArrayList<>();

        // 3. Query the database depending on the specified filters.
        if (filterPopular) {
            if (filterTags) {
                if (filterPrice) {
                    // 4: TAGS, PRICE, POPULAR
                    lstResultIds.addAll(venueSearchSummaryRepository.filterByTagsAndPriceAndPopular(filterDTO.getTagIds(), filterPriceMin, filterPriceMax, YamoUtils.fromBigIntegerListToLongList(lstMostPopularVenueIds)));
                } else {
                    // 2: TAGS, POPULAR
                    lstResultIds.addAll(venueSearchSummaryRepository.filterByTagsAndPopular(filterDTO.getTagIds(), YamoUtils.fromBigIntegerListToLongList(lstMostPopularVenueIds)));
                }
            } else if (filterPrice) {
                // 3: PRICE, POPULAR
                lstResultIds.addAll(venueSearchSummaryRepository.filterByPriceAndPopular(filterPriceMin, filterPriceMax, YamoUtils.fromBigIntegerListToLongList(lstMostPopularVenueIds)));
            } else {
                // 1: POPULAR
                lstResultIds.addAll(venueSearchSummaryRepository.filterByOnlyPopular(YamoUtils.fromBigIntegerListToLongList(lstMostPopularVenueIds)));
            }
        } else if (filterTags) {
            if (filterPrice) {
                // 6: TAGS, PRICE
                lstResultIds.addAll(venueSearchSummaryRepository.filterByTagsAndPrice(filterDTO.getTagIds(), filterPriceMin, filterPriceMax));
            } else {
                // 5: TAGS
                lstResultIds.addAll(venueSearchSummaryRepository.filterByOnlyTags(filterDTO.getTagIds()));
            }
        } else if (filterPrice) {
            // 7: PRICE
            lstResultIds.addAll(venueSearchSummaryRepository.filterByOnlyPrice(filterPriceMin, filterPriceMax));
        }

        // 4. Retrieve whole objects.
        if (!lstResultIds.isEmpty()) {
            lstResults = venueSearchSummaryRepository.findExhibitionsByUuidIn(
                    YamoUtils.fromBigIntegerListToLongList(lstResultIds),
                    filterDTO.getLat(),
                    filterDTO.getLon(),
                    filterDTO.getMiles(),
                    YamoUtils.DISTANCE_UNIT_FACTOR_MILES,
                    YamoUtils.now().getTime()
            );
        }

        // 5. Return some values in case of empty list. Nearby exhibitions.
        if (null != lstResults && lstResults.isEmpty() && !filterPopular && !filterPrice && !filterTags) {
            lstResults = venueSearchSummaryRepository.findNearbyExhibitions(
                    filterDTO.getLat(),
                    filterDTO.getLon(),
                    filterDTO.getMiles(),
                    YamoUtils.DISTANCE_UNIT_FACTOR_MILES,
                    YamoUtils.now().getTime()
            );
        }

        // 6. Add user data.
        addUserInfoToVenueList(lstResults, filterDTO.getLat(), filterDTO.getLon(), authUser);

        // 7. Order.
        orderVenuesByRelevance(lstResults);

        return lstResults;
    }
    
    public List<VenueSearchSummary> filterFavouriteExhibitions(User authUser, FilterDTO filterDTO) {

        List<VenueSearchSummary> lstResults = new ArrayList<>();

        // 1. Set flags for different filters.
        boolean filterPopular = filterDTO.isMostPopular();
        double filterPriceMin = 0;
        double filterPriceMax = 0;

        switch (filterDTO.getPriceFilter()) {
            case 2:
                filterPriceMin = 0.01;
                filterPriceMax = 5.00;
                break;
            case 3:
                filterPriceMin = 5.01;
                filterPriceMax = 15.00;
                break;
            case 4:
                filterPriceMin = 15.01;
                filterPriceMax = 500.00;
                break;
        }

        // 2. Find most popular exhibitions.
        List<BigInteger> lstMostPopularVenueIds = null;
        if (filterPopular) {
            lstMostPopularVenueIds = venueSearchSummaryRepository.findMostPopularExhibitions();
            if (null == lstMostPopularVenueIds || lstMostPopularVenueIds.isEmpty()) {
                filterPopular = false;
            }
        }

        List<BigInteger> lstResultIds = new ArrayList<>();
        
        lstResultIds.addAll(venueSearchSummaryRepository.filterByOnlyPrice(filterPriceMin, filterPriceMax));

        // 4. Retrieve whole objects.
        if (!lstResultIds.isEmpty()) {
            /*lstResults = venueSearchSummaryRepository.findFavouriteExhibitionsByUuidIn(            		
                    YamoUtils.fromBigIntegerListToLongList(lstResultIds),
                    filterDTO.getLat(),
                    filterDTO.getLon(),
                    filterDTO.getMiles(),
                    YamoUtils.DISTANCE_UNIT_FACTOR_MILES,
                    YamoUtils.now().getTime(),
                    authUser.getUuid()
            );*/
            lstResults = venueSearchSummaryRepository.findFavouriteExhibitionsByUuidIn(            		
                    authUser.getUuid()
            );
        }


        // 7. Order.
        orderVenuesByRelevance(lstResults);

        return lstResults;
    }    

    public List<VenueSearchSummary> filterExhibitions2(User authUser, FilterDTO filterDTO) {

        List<VenueSearchSummary> lstResults = new ArrayList<>();

        // 1. Set flags for different filters.
        boolean filterPopular = filterDTO.isMostPopular();
        boolean filterTags = null != filterDTO.getTagIds() && !filterDTO.getTagIds().isEmpty();
        boolean filterPrice = 0 != filterDTO.getPriceFilter();

        double filterPriceMin = 0;
        double filterPriceMax = 0;

        switch (filterDTO.getPriceFilter()) {
            case 2:
                filterPriceMin = 0.01;
                filterPriceMax = 5.00;
                break;
            case 3:
                filterPriceMin = 5.01;
                filterPriceMax = 15.00;
                break;
            case 4:
                filterPriceMin = 15.01;
                filterPriceMax = 500.00;
                break;
        }

        // 2. Find most popular exhibitions.
        List<BigInteger> lstMostPopularVenueIds = null;
        if (filterPopular) {
            lstMostPopularVenueIds = venueSearchSummaryRepository.findMostPopularExhibitions();
            if (null == lstMostPopularVenueIds || lstMostPopularVenueIds.isEmpty()) {
                filterPopular = false;
            }
        }

        List<BigInteger> lstResultIds = new ArrayList<>();

        // 3. Query the database depending on the specified filters.
        if (filterPopular) {
            if (filterTags) {
                if (filterPrice) {
                    // 4: TAGS, PRICE, POPULAR
                    lstResultIds.addAll(venueSearchSummaryRepository.filterByTagsAndPriceAndPopular(filterDTO.getTagIds(), filterPriceMin, filterPriceMax, YamoUtils.fromBigIntegerListToLongList(lstMostPopularVenueIds)));
                } else {
                    // 2: TAGS, POPULAR
                    lstResultIds.addAll(venueSearchSummaryRepository.filterByTagsAndPopular(filterDTO.getTagIds(), YamoUtils.fromBigIntegerListToLongList(lstMostPopularVenueIds)));
                }
            } else if (filterPrice) {
                // 3: PRICE, POPULAR
                lstResultIds.addAll(venueSearchSummaryRepository.filterByPriceAndPopular(filterPriceMin, filterPriceMax, YamoUtils.fromBigIntegerListToLongList(lstMostPopularVenueIds)));
            } else {
                // 1: POPULAR
                lstResultIds.addAll(venueSearchSummaryRepository.filterByOnlyPopular(YamoUtils.fromBigIntegerListToLongList(lstMostPopularVenueIds)));
            }
        } else if (filterTags) {
            if (filterPrice) {
                // 6: TAGS, PRICE
                lstResultIds.addAll(venueSearchSummaryRepository.filterByTagsAndPrice(filterDTO.getTagIds(), filterPriceMin, filterPriceMax));
            } else {
                // 5: TAGS
                lstResultIds.addAll(venueSearchSummaryRepository.filterByOnlyTags(filterDTO.getTagIds()));
            }
        } else if (filterPrice) {
            // 7: PRICE
            lstResultIds.addAll(venueSearchSummaryRepository.filterByOnlyPrice(filterPriceMin, filterPriceMax));
        }

        // 4. Retrieve whole objects.
        if (!lstResultIds.isEmpty()) {
            lstResults = venueSearchSummaryRepository.findExhibitionsByUuidIn2(
                    YamoUtils.fromBigIntegerListToLongList(lstResultIds),
                    filterDTO.getLat(),
                    filterDTO.getLon(),
                    filterDTO.getMiles(),
                    YamoUtils.DISTANCE_UNIT_FACTOR_MILES,
                    YamoUtils.now().getTime()
            );
        }

        // 5. Return some values in case of empty list. Nearby exhibitions.
        if (null != lstResults && lstResults.isEmpty() && !filterPopular && !filterPrice && !filterTags) {
            lstResults = venueSearchSummaryRepository.findNearbyExhibitions2(
                    filterDTO.getLat(),
                    filterDTO.getLon(),
                    filterDTO.getMiles(),
                    YamoUtils.DISTANCE_UNIT_FACTOR_MILES,
                    YamoUtils.now().getTime()
            );
        }

        // 6. Add user data.
        addUserInfoToVenueList(lstResults, filterDTO.getLat(), filterDTO.getLon(), authUser);

        // 7. Order.
        orderVenuesByRelevance(lstResults);

        return lstResults;
    }

    public void addUserInfoForSingleVenue(User authUser, Venue existingVenue) {
        existingVenue.setFavouriteUsersTapped(null != existingVenue.getFavouriteUsers() && existingVenue.getFavouriteUsers().contains(authUser));

        // Fill existing exhibitions.
        try {
            if (VenueType.EXHIBITION.equals(existingVenue.getVenueType())) {

                try {
                    String galleryName = venueRepository.findGalleryNameFromExhibitionId(existingVenue.getUuid());
                    existingVenue.setGalleryName(null == galleryName ? "" : galleryName);
                } catch (Exception e) {
                    existingVenue.setGalleryName("");
                }

                List<Venue> lstRelatedExhibitions = venueRepository.findOtherExhibitionsForTheSameGallery(existingVenue.getUuid());
                if (null != lstRelatedExhibitions && !lstRelatedExhibitions.isEmpty()) {
                    List<VenueSearchSummary> lstVenueSearchSummary = new ArrayList<>();
                    VenueSearchSummary vss;
                    for (Venue e : lstRelatedExhibitions) {
                        vss = new VenueSearchSummary();
                        vss.setAddress(e.getAddress());
                        vss.setDescription(e.getDescription());
                        vss.setLat(e.getLat());
                        vss.setLon(e.getLon());
                        vss.setName(e.getName());
                        vss.setUuid(e.getUuid());
                        vss.setVenueType(e.getVenueType());
                        vss.setImageUrl(null == e.getSpaceImageUrls() || e.getSpaceImageUrls().isEmpty() ? "" : new ArrayList<>(e.getSpaceImageUrls()).get(0));

                        lstVenueSearchSummary.add(vss);
                    }

                    addUserInfoToVenueList(lstVenueSearchSummary, 0, 0, authUser);
                    existingVenue.setRecommended(new HashSet<>(lstVenueSearchSummary));
                }
            }
        } catch (Exception e) {
            logger.error("Could not find recommended exhibitions.", e);
        }

    }

    public List<TagGroup> listTagGroups() {
        // Only specific mediums.

        List<TagGroup> lstTagGroups = tagGroupRepository.findAll();

        if (null != lstTagGroups) {
            SortedSet<Tag> tempTags = new TreeSet<>();
            for (TagGroup tg : lstTagGroups) {
                if ("Mediums".equals(tg.getName())) {
                    for (Tag t : tg.getTags()) {
                        if (YamoUtils.MEDIUMS_TO_SHOW_IN_FILTERS.contains(t.getName())) {
                            tempTags.add(t);
                        }
                    }
                    tg.setTags(tempTags);
                }

            }
        }

        return lstTagGroups;
    }


    public List<VenueSearchSummary> filterAndSearchVenuesV2(User authUser, FilterSearchDTO filterSearchDTO) {

        // 1. Filter
        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setLat(filterSearchDTO.getLat());
        filterDTO.setLon(filterSearchDTO.getLon());
        filterDTO.setMostPopular(filterSearchDTO.isMostPopular());
        filterDTO.setPriceFilter(filterSearchDTO.getPriceFilter());
        filterDTO.setTagIds(filterSearchDTO.getTagIds());
        filterDTO.setMiles(filterSearchDTO.getMiles());
        
        logger.debug(filterDTO);
        List<VenueSearchSummary> lstFilters = filterExhibitions(authUser, filterDTO);
        
        logger.debug(lstFilters);
        // 2. Search
        List<VenueSearchSummary> lstFinalResults = new ArrayList<>();
        if (null != lstFilters && !lstFilters.isEmpty()) {
            for (VenueSearchSummary vss : lstFilters) {
            	logger.debug(vss.getGalleryName());
                if (vss.getName().toLowerCase().contains(filterSearchDTO.getSearch().toLowerCase())) {
                    lstFinalResults.add(vss);
                }// Returning exhibitions also in case gallery name matches with search pattern.
                else if(vss.getGalleryName().toLowerCase().contains(filterSearchDTO.getSearch().toLowerCase())){
                    lstFinalResults.add(vss);
                }
            }
        }

        // 3. Order
        orderVenuesByRelevance(lstFinalResults);

        return lstFinalResults;

    }

    public List<VenueSearchSummary> filterAndSearchVenuesV3(User authUser, FilterSearchDTO filterSearchDTO) {

        // 1. Filter
        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setLat(filterSearchDTO.getLat());
        filterDTO.setLon(filterSearchDTO.getLon());
        filterDTO.setMostPopular(filterSearchDTO.isMostPopular());
        filterDTO.setPriceFilter(filterSearchDTO.getPriceFilter());
        filterDTO.setTagIds(filterSearchDTO.getTagIds());
        filterDTO.setMiles(filterSearchDTO.getMiles());
        List<VenueSearchSummary> lstFilters = filterExhibitions2(authUser, filterDTO);

        // 2. Search
        List<VenueSearchSummary> lstFinalResults = new ArrayList<>();
        if (null != lstFilters && !lstFilters.isEmpty()) {
            for (VenueSearchSummary vss : lstFilters) {
                if (vss.getName().toLowerCase().contains(filterSearchDTO.getSearch().toLowerCase())) {
                    lstFinalResults.add(vss);
                }// Returning exhibitions also in case gallery name matches with search pattern.
                else if(vss.getGalleryName().toLowerCase().contains(filterSearchDTO.getSearch().toLowerCase())){
                    lstFinalResults.add(vss);
                }
            }
        }

        // 3. Order
        orderVenuesByRelevance(lstFinalResults);

        return lstFinalResults;

    }

}


