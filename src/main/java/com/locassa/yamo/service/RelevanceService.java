package com.locassa.yamo.service;

import com.locassa.yamo.model.Tag;
import com.locassa.yamo.model.User;
import com.locassa.yamo.model.UserTag;
import com.locassa.yamo.model.UserTagRelevance;
import com.locassa.yamo.repository.*;
import com.locassa.yamo.util.YamoUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RelevanceService {

    private static final Logger logger = Logger.getLogger(RelevanceService.class);

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private UserTagRelevanceRepository userTagRelevanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    public double findRelevanceForUserAndVenue(Long userId, Long venueId) {
        return venueRepository.findRelevanceForVenueAndUser(venueId, userId).doubleValue();
    }

    public void updateRelevanceForUser(Long userId) {

        try {

            // 1. Find venues which contain user tags.
            List<BigInteger> lstVenueIds = venueRepository.findVenuesByUserTags(userId);

            // 2. For each venue, update relevance.
            if (null != lstVenueIds) {
                for (BigInteger venueId : lstVenueIds) {
                    updateRelevance(userId, venueId.longValue());
                }
            }

        } catch (Exception e) {
            logger.error("Could not update relevance value for user " + userId + ".", e);
        }

    }

    public void updateRelevanceForVenue(Long venueId) {

        try {

            // 1. Find users which contain venue tags.
            List<BigInteger> lstUserIds = venueRepository.findUsersByVenueTags(venueId);

            // 2. For each user, update relevance.
            if (null != lstUserIds) {
                for (BigInteger userId : lstUserIds) {
                    updateRelevance(userId.longValue(), venueId);
                }
            }

        } catch (Exception e) {
            logger.error("Could not update relevance value for venue " + venueId + ".", e);
        }

    }

    public void updateRelevance(Long userId, Long venueId) {

        try {

            // 1. Find user tags.
            List<UserTagRelevance> lstUserTags = userTagRelevanceRepository.findByUserId(userId);
            if (null == lstUserTags) {
                lstUserTags = new ArrayList<>();
            }

            // 2. Find venue tags.
            List<Tag> lstVenueTags = tagRepository.findTagsByVenueUuid(venueId);

            // 3. Find tags in common.
            List<Long> lstCommonTagValues = new ArrayList<>();
            for (UserTagRelevance ut : lstUserTags) {
                for (Tag t : lstVenueTags) {
                    if (ut.getTagId().equals(t.getUuid())) {
                        lstCommonTagValues.add(ut.getWeight());
                    }
                }
            }

            // 4. Normalise user tag values, excluding value 0.
            List<BigDecimal> lstNormalisedValues = new ArrayList<>();
            BigDecimal min = BigDecimal.valueOf(Collections.min(lstCommonTagValues));
            BigDecimal max = BigDecimal.valueOf(Collections.max(lstCommonTagValues));
            BigDecimal down = max.subtract(min);

            if (0 != BigDecimal.ZERO.compareTo(down)) {
                BigDecimal up;
                BigDecimal normalisedValue;
                for (Long x : lstCommonTagValues) {
                    up = BigDecimal.valueOf(x).subtract(min);
                    normalisedValue = up.divide(down, YamoUtils.DEFAULT_DECIMAL_POSITIONS_TO_DIVIDE, RoundingMode.HALF_UP);
                    if (0 != BigDecimal.ZERO.compareTo(normalisedValue)) {
                        lstNormalisedValues.add(normalisedValue);
                    }
                }

                // 5. Calculate arithmetic mean.
                BigDecimal sum = BigDecimal.ZERO;
                for (BigDecimal value : lstNormalisedValues) {
                    sum = sum.add(value);
                }

                if (!lstNormalisedValues.isEmpty()) {
                    double meanValue = sum.divide(BigDecimal.valueOf(lstNormalisedValues.size()), YamoUtils.DEFAULT_DECIMAL_POSITIONS_TO_DIVIDE, RoundingMode.HALF_UP).doubleValue();
                    meanValue = 100.0 * meanValue;

                    // 6. Save value, along with the userId and venueId on the database.
                    BigInteger existsResult = venueRepository.findIfRelevanceExists(venueId, userId);
                    boolean exists = 0 == BigInteger.ONE.compareTo(existsResult);

                    if (exists) {
                        // Update
                        venueRepository.updateRelevanceValue(userId, venueId, meanValue);
                    } else {
                        // Create
                        venueRepository.insertRelevanceValue(userId, venueId, meanValue);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Could not update relevance value for user " + userId + " and venue " + venueId + ".", e);
        }

    }

}
