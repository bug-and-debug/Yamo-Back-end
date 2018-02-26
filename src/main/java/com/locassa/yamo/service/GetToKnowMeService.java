package com.locassa.yamo.service;

import com.locassa.yamo.model.*;
import com.locassa.yamo.model.dto.ResponseDTO;
import com.locassa.yamo.repository.*;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class GetToKnowMeService {

    private static final Logger logger = Logger.getLogger(GetToKnowMeService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private ArtWorkRepository artWorkRepository;

    @Autowired
    private ArtWorkReplyRepository artWorkReplyRepository;

    @Autowired
    private UserTagRepository userTagRepository;

    @Autowired
    private RelevanceService relevanceService;

    public List<ArtWork> getSuggestionsForUserPaged(User authUser, Long timestamp) {
        List<ArtWork> lstArtWork = new ArrayList<>();
        Date from = new Date(timestamp);

        // 1. Find replies.
        List<BigInteger> lstReplies = artWorkReplyRepository.findByUserUuid(authUser.getUuid());

        // 2. If there are no replies, find normally.
        if (null == lstReplies || lstReplies.isEmpty()) {
            lstArtWork = artWorkRepository.findFirst10ByCreatedLessThanOrderByCreatedDesc(from);
        } else {
            // 3. If there are replies, find but those replies.
            lstArtWork = artWorkRepository.findFirst10ByUuidNotInAndCreatedLessThanOrderByCreatedDesc(
                    YamoUtils.fromBigIntegerListToLongList(lstReplies),
                    from
            );

            // 4. If the result is null, find replies that have been answered less times.
            if (null == lstArtWork || lstArtWork.isEmpty()) {
                lstReplies = artWorkReplyRepository.findByUserUuidAndLessReplied(authUser.getUuid());
                if (null != lstReplies && !lstReplies.isEmpty()) {
                    lstArtWork = artWorkRepository.findFirst10ByUuidInAndCreatedLessThanOrderByCreatedDesc(
                            YamoUtils.fromBigIntegerListToLongList(lstReplies),
                            from
                    );
                } else {
                    lstArtWork = artWorkRepository.findFirst10ByCreatedLessThanOrderByCreatedDesc(from);
                }
            }
        }

        return lstArtWork;
    }

    public List<ArtWork> getSuggestionsForUser(User authUser) {

        List<ArtWork> lstArtWork = new ArrayList<>();

        // 1. Find replies.
        List<BigInteger> lstReplies = artWorkReplyRepository.findByUserUuid(authUser.getUuid());

        // 2. If there are no replies, find normally.
        if (null == lstReplies || lstReplies.isEmpty()) {
            lstArtWork = artWorkRepository.findFirst10ByOrderByCreatedDesc();
        } else {
            // 3. If there are replies, find but those replies.
            lstArtWork = artWorkRepository.findFirst10ByUuidNotInOrderByCreatedDesc(YamoUtils.fromBigIntegerListToLongList(lstReplies));

            // 4. If the result is null, find replies that have been answered less times.
            if (null == lstArtWork || lstArtWork.isEmpty()) {
                lstReplies = artWorkReplyRepository.findByUserUuidAndLessReplied(authUser.getUuid());
                if (null != lstReplies && !lstReplies.isEmpty()) {
                    lstArtWork = artWorkRepository.findFirst10ByUuidInOrderByCreatedDesc(YamoUtils.fromBigIntegerListToLongList(lstReplies));
                } else {
                    lstArtWork = artWorkRepository.findFirst10ByOrderByCreatedDesc();
                }
            }
        }

        return lstArtWork;
    }

    public ArtWork getArtWorkById(Long id) {
        return artWorkRepository.findOne(id);
    }

    public ResponseDTO rateArtWork(final User authUser, ArtWork artWork, int rating) {

        ResponseDTO result = rateArtWorkTransactional(authUser, artWork, rating);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                relevanceService.updateRelevanceForUser(authUser.getUuid());
            }
        };
        Thread updateRelevance = new Thread(runnable);
        updateRelevance.start();

        return result;
    }

    @Transactional
    public ResponseDTO rateArtWorkTransactional(User authUser, ArtWork artWork, int rating) {

        ResponseDTO result = ResponseDTO.statusOK();

        try {

            // 1. Save reply reference.
            ArtWorkReply existingReply = artWorkReplyRepository.findByUserUuidAndArtWorkUuid(authUser.getUuid(), artWork.getUuid());
            if (null == existingReply) {
                existingReply = new ArtWorkReply();
                existingReply.setUserUuid(authUser.getUuid());
                existingReply.setArtWorkUuid(artWork.getUuid());
                existingReply.setCounter(0);
            }
            existingReply.setCounter(existingReply.getCounter() + 1);
            artWorkReplyRepository.save(existingReply);

            //1.1 Changes made by SharmaNeh for YMS-60 for user sign up history
            if (null != authUser && !authUser.isReady()){
                authUser.setReady(true);
            }

            // 2. Save label weights for the authenticated user.

            // 2.1. Retrieve list of labels associated to this art work.
            Set<Tag> artWorkTags = artWork.getTags();
            if (null != artWorkTags && !artWorkTags.isEmpty()) {

                // 2.2. Find replies for the auth user and list of tags.
                UserTag tempUserTag;
                for (Tag t : artWorkTags) {
                    tempUserTag = userTagRepository.findByUserAndTag(authUser, t);
                    if (null == tempUserTag) {
                        tempUserTag = new UserTag();
                        tempUserTag.setUser(authUser);
                        tempUserTag.setTag(t);
                        tempUserTag.setWeight(YamoUtils.DEFAULT_INITIAL_TAG_WEIGHT);
                    }
                    tempUserTag.setWeight(tempUserTag.getWeight() + calculateScoreDependingOnRating(rating));
                    userTagRepository.save(tempUserTag);
                }
            }

        } catch (Exception e) {
            result = ResponseDTO.statusNoOK();
        }

        return result;
    }

    private long calculateScoreDependingOnRating(int rating) {

        long score;

        switch (rating) {
            case 1:
                score = -3;
                break;
            case 2:
                score = -1;
                break;
            case 4:
                score = 1;
                break;
            case 5:
                score = 3;
                break;
            default:
                score = 0;
        }

        return score;
    }

}
