package com.locassa.yamo.repository;

import com.locassa.yamo.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    String SQL_GALLERY_NAME_GIVEN_EXHIBITION_ID = "" +
            "SELECT \n" +
            "    name\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "    venue_type = 2\n" +
            "        AND uuid IN (SELECT \n" +
            "            venue_uuid\n" +
            "        FROM\n" +
            "            venue_children\n" +
            "        WHERE\n" +
            "            children_uuid = ?1)" +
            "";

    String SQL_EXHIBITION_ENDING_SOON = "" +
            "select * from venue where venue_type = 1 and end_date is not null and ?1 = DATEDIFF(SYSDATE(), end_date)" +
            "";

    String SQL_USER_VENUE_RELEVANCE = "" +
            "SELECT \n" +
            "    relevance\n" +
            "FROM\n" +
            "    t_user_venue_relevance\n" +
            "WHERE\n" +
            "    user_uuid = ?2 AND venue_uuid = ?1 \n" +
            "UNION SELECT 0 LIMIT 1" +
            "";

    String SQL_USER_VENUE_RELEVANCE_EXISTS = "" +
            "SELECT \n" +
            "    IF(EXISTS( SELECT \n" +
            "                *\n" +
            "            FROM\n" +
            "                t_user_venue_relevance\n" +
            "            WHERE\n" +
            "                user_uuid = ?2 AND venue_uuid = ?1),\n" +
            "        1,\n" +
            "        0)" +
            "";

    String SQL_INSERT_RELEVANCE_VALUE = "" +
            "insert into t_user_venue_relevance (user_uuid, venue_uuid, relevance) values (?1, ?2, ?3)" +
            "";

    String SQL_UPDATE_RELEVANCE_VALUE = "" +
            "UPDATE t_user_venue_relevance \n" +
            "SET \n" +
            "    relevance = ?3\n" +
            "WHERE\n" +
            "    user_uuid = ?1 AND venue_uuid = ?2" +
            "";

    String SQL_FIND_VENUES_BY_USER_TAGS = "" +
            "SELECT DISTINCT\n" +
            "    venue_uuid\n" +
            "FROM\n" +
            "    venue_tags\n" +
            "WHERE\n" +
            "    tags_uuid IN (SELECT \n" +
            "            tag_uuid\n" +
            "        FROM\n" +
            "            user_tag\n" +
            "        WHERE\n" +
            "            user_uuid = ?1)" +
            "";

    String SQL_FIND_USERS_BY_VENUE_TAGS = "" +
            "SELECT DISTINCT\n" +
            "    user_uuid\n" +
            "FROM\n" +
            "    user_tag\n" +
            "WHERE\n" +
            "    tag_uuid IN (SELECT \n" +
            "            tags_uuid\n" +
            "        FROM\n" +
            "            venue_tags\n" +
            "        WHERE\n" +
            "            venue_uuid = ?1)" +
            "";

    String SQL_FIND_OTHER_EXHIBITIONS_SAME_GALLERY = "" +
            "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "    venue_type = 1\n" +
            "        AND uuid IN (SELECT \n" +
            "            children_uuid\n" +
            "        FROM\n" +
            "            venue_children\n" +
            "        WHERE\n" +
            "            venue_uuid IN (SELECT \n" +
            "                    venue_uuid\n" +
            "                FROM\n" +
            "                    venue_children\n" +
            "                WHERE\n" +
            "                    children_uuid = ?1)\n" +
            "                AND children_uuid != ?1)" +
            "";

    @Query(value = SQL_USER_VENUE_RELEVANCE, nativeQuery = true)
    BigDecimal findRelevanceForVenueAndUser(Long venueId, Long userId);

    @Query(value = SQL_USER_VENUE_RELEVANCE_EXISTS, nativeQuery = true)
    BigInteger findIfRelevanceExists(Long venueId, Long userId);

    @Query(value = SQL_INSERT_RELEVANCE_VALUE, nativeQuery = true)
    @Transactional
    @Modifying
    void insertRelevanceValue(Long userId, Long venueId, double relevance);

    @Query(value = SQL_UPDATE_RELEVANCE_VALUE, nativeQuery = true)
    @Transactional
    @Modifying
    void updateRelevanceValue(Long userId, Long venueId, double relevance);

    @Query(value = SQL_FIND_VENUES_BY_USER_TAGS, nativeQuery = true)
    List<BigInteger> findVenuesByUserTags(Long userId);

    @Query(value = SQL_FIND_USERS_BY_VENUE_TAGS, nativeQuery = true)
    List<BigInteger> findUsersByVenueTags(Long venueId);

    @Query(value = SQL_EXHIBITION_ENDING_SOON, nativeQuery = true)
    List<Venue> findExhibitionsEndingSoon(int days);

    @Query(value = SQL_FIND_OTHER_EXHIBITIONS_SAME_GALLERY, nativeQuery = true)
    List<Venue> findOtherExhibitionsForTheSameGallery(Long exhibitionId);

    @Query(value = SQL_GALLERY_NAME_GIVEN_EXHIBITION_ID, nativeQuery = true)
    String findGalleryNameFromExhibitionId(Long exhibitionId);

}
