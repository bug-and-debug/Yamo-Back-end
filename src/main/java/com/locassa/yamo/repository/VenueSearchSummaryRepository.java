package com.locassa.yamo.repository;

import com.locassa.yamo.model.summary.VenueSearchSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface VenueSearchSummaryRepository extends JpaRepository<VenueSearchSummary, Long> {

    String SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS = "" +
            "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        uuid, name, description, venue_type, address, lat, lon\n" +
            "    FROM\n" +
            "        (SELECT \n" +
            "        z.*,\n" +
            "            p.radius,\n" +
            "            p.distance_unit * DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.lat)) * COS(RADIANS(p.longpoint - z.lon)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.lat)))) AS distance\n" +
            "    FROM\n" +
            "        venue AS z\n" +
            "    JOIN (SELECT \n" +
            "            ?1 AS latpoint,\n" +
            "            ?2 AS longpoint,\n" +
            "            ?3 AS radius,\n" +
            "            ?4 AS distance_unit\n" +
            "    ) AS p\n" +
            "    WHERE\n" +
            "        z.lat BETWEEN p.latpoint - (p.radius / p.distance_unit) AND p.latpoint + (p.radius / p.distance_unit)\n" +
            "            AND z.lon BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d\n" +
            "    WHERE\n" +
            "        distance <= radius AND venue_type = 1\n" +
            "            AND (start_date IS NULL\n" +
            "              OR start_date < FROM_UNIXTIME(?5 / 1000))\n" +
            "            AND (end_date IS NULL " +
            "              OR end_date > FROM_UNIXTIME(?5 / 1000))\n" +
            "    ORDER BY uuid ASC) t\n" +
            //"GROUP BY t.lat , t.lon " +
            "";

    String SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_2 = "" +
            "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        uuid, name, description, venue_type, address, lat, lon\n" +
            "    FROM\n" +
            "        (SELECT \n" +
            "        z.*,\n" +
            "            p.radius,\n" +
            "            p.distance_unit * DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.lat)) * COS(RADIANS(p.longpoint - z.lon)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.lat)))) AS distance\n" +
            "    FROM\n" +
            "        venue AS z\n" +
            "    JOIN (SELECT \n" +
            "            ?1 AS latpoint,\n" +
            "            ?2 AS longpoint,\n" +
            "            ?3 AS radius,\n" +
            "            ?4 AS distance_unit\n" +
            "    ) AS p\n" +
            "    WHERE\n" +
            "        z.lat BETWEEN p.latpoint - (p.radius / p.distance_unit) AND p.latpoint + (p.radius / p.distance_unit)\n" +
            "            AND z.lon BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d\n" +
            "    WHERE\n" +
            "        distance <= radius AND venue_type = 1\n" +
            "            AND (start_date IS NULL\n" +
            "              OR start_date < FROM_UNIXTIME(?5 / 1000))\n" +
            "            AND (end_date IS NULL " +
            "              OR end_date > FROM_UNIXTIME(?5 / 1000))\n" +
            "    ORDER BY uuid ASC) t\n" +
            "";

    String SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_NAME = "" +
            "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        uuid, name, description, venue_type, address, lat, lon\n" +
            "    FROM\n" +
            "        (SELECT \n" +
            "        z.*,\n" +
            "            p.radius,\n" +
            "            p.distance_unit * DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.lat)) * COS(RADIANS(p.longpoint - z.lon)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.lat)))) AS distance\n" +
            "    FROM\n" +
            "        venue AS z\n" +
            "    JOIN (SELECT \n" +
            "            ?2 AS latpoint,\n" +
            "            ?3 AS longpoint,\n" +
            "            ?4 AS radius,\n" +
            "            ?5 AS distance_unit\n" +
            "    ) AS p\n" +
            "    WHERE\n" +
            "        z.lat BETWEEN p.latpoint - (p.radius / p.distance_unit) AND p.latpoint + (p.radius / p.distance_unit)\n" +
            "            AND z.lon BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d\n" +
            "    WHERE\n" +
            "    LCASE(name) LIKE ?1 AND \n" +
            "        distance <= radius AND venue_type = 1\n" +
            "            AND (start_date IS NULL\n" +
            "              OR start_date < FROM_UNIXTIME(?6 / 1000))\n" +
            "            AND (end_date IS NULL " +
            "              OR end_date > FROM_UNIXTIME(?6 / 1000))\n" +
            "    ORDER BY uuid ASC) t\n" +
            //"GROUP BY t.lat , t.lon " +
            "";

    String SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_NAME_V2 = "SELECT * FROM exhibition_view as e inner join \n" +
			"(SELECT  * FROM  \n" +
			"(SELECT   uuid, name, description, venue_type, address, lat, lon  FROM \n" +
			"(SELECT   z.*, p.radius, p.distance_unit * \n" +
			"DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.lat)) * COS(RADIANS(p.longpoint - z.lon)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.lat)))) AS distance \n"+
			"FROM  venue AS z   JOIN (SELECT ?2 AS latpoint, ?3 AS longpoint, ?4 AS radius, ?5 AS distance_unit) AS p  \n" +
			"WHERE  z.lat BETWEEN p.latpoint - (p.radius / p.distance_unit) AND \n" + 
			"p.latpoint + (p.radius / p.distance_unit) AND z.lon BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d  WHERE \n" + 
			"name LIKE ?1 AND distance <= radius AND (start_date IS NULL OR start_date < FROM_UNIXTIME(?6 / 1000))  \n" + 
			"AND (end_date IS NULL OR end_date > FROM_UNIXTIME(?6 / 1000))  ORDER BY uuid ASC) as t) as v \n" + 
			"on e.gallery_id=v.uuid";
    
    String SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_UUID = "" +
            "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        uuid, name, description, venue_type, address, lat, lon\n" +
            "    FROM\n" +
            "        (SELECT \n" +
            "        z.*,\n" +
            "            p.radius,\n" +
            "            p.distance_unit * DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.lat)) * COS(RADIANS(p.longpoint - z.lon)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.lat)))) AS distance\n" +
            "    FROM\n" +
            "        venue AS z\n" +
            "    JOIN (SELECT \n" +
            "            ?2 AS latpoint,\n" +
            "            ?3 AS longpoint,\n" +
            "            ?4 AS radius,\n" +
            "            ?5 AS distance_unit\n" +
            "    ) AS p\n" +
            "    WHERE\n" +
            "        z.lat BETWEEN p.latpoint - (p.radius / p.distance_unit) AND p.latpoint + (p.radius / p.distance_unit)\n" +
            "            AND z.lon BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d\n" +
            "    WHERE\n" +
            "    uuid in (?1) AND \n" +
            "        distance <= radius AND venue_type = 1\n" +
            "            AND (start_date IS NULL\n" +
            "              OR start_date < FROM_UNIXTIME(?6 / 1000))\n" +
            "            AND (end_date IS NULL " +
            "              OR end_date > FROM_UNIXTIME(?6 / 1000))\n" +
            "    ORDER BY uuid ASC) t\n" +
            //"GROUP BY t.lat , t.lon " +
            "";
    
    /*String SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_UUID = "SELECT * FROM exhibition_view as e inner join \n" +
    					"(SELECT  * FROM  \n" +
    					"(SELECT   uuid, name, description, venue_type, address, lat, lon  FROM \n" +
    					"(SELECT   z.*, p.radius, p.distance_unit * \n" +
    					"DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.lat)) * COS(RADIANS(p.longpoint - z.lon)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.lat)))) AS distance \n"+
    					"FROM  venue AS z   JOIN (SELECT ?2 AS latpoint, ?3 AS longpoint, ?4 AS radius, ?5 AS distance_unit) AS p  \n" +
    					"WHERE  z.lat BETWEEN p.latpoint - (p.radius / p.distance_unit) AND \n" + 
    					"p.latpoint + (p.radius / p.distance_unit) AND z.lon BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d  WHERE  uuid in (?1) AND  \n" + 
    					"distance <= radius AND (start_date IS NULL OR start_date < FROM_UNIXTIME(?6 / 1000))  \n" + 
    					"AND (end_date IS NULL OR end_date > FROM_UNIXTIME(?6 / 1000))  ORDER BY uuid ASC) as t) as v \n" + 
    					"on e.gallery_id=v.uuid";*/

    
    /*String SQL_HAVERSINE_FORMULA_FOR_FAVOURTE_EXHIBITIONS_BY_UUID = "SELECT * FROM venue_favourite_users as e inner join \n" +
            "(SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        uuid, name, description, venue_type, address, lat, lon\n" +
            "    FROM\n" +
            "        (SELECT \n" +
            "        z.*,\n" +
            "            p.radius,\n" +
            "            p.distance_unit * DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.lat)) * COS(RADIANS(p.longpoint - z.lon)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.lat)))) AS distance\n" +
            "    FROM\n" +
            "        venue AS z\n" +
            "    JOIN (SELECT \n" +
            "            ?2 AS latpoint,\n" +
            "            ?3 AS longpoint,\n" +
            "            ?4 AS radius,\n" +
            "            ?5 AS distance_unit\n" +
            "    ) AS p\n" +
            "    WHERE\n" +
            "        z.lat BETWEEN p.latpoint - (p.radius / p.distance_unit) AND p.latpoint + (p.radius / p.distance_unit)\n" +
            "            AND z.lon BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d\n" +
            "    WHERE\n" +
            "    uuid in (?1) AND \n" +
            "        distance <= radius AND venue_type = 1\n" +
            "            AND (start_date IS NULL\n" +
            "              OR start_date < FROM_UNIXTIME(?6 / 1000))\n" +
            "            AND (end_date IS NULL " +
            "              OR end_date > FROM_UNIXTIME(?6 / 1000))\n" +
            "    ORDER BY uuid ASC) t) as v \n" +
            "on e.venue_uuid=v.uuid where e.favourite_users_uuid = ?7";*/

    String SQL_HAVERSINE_FORMULA_FOR_FAVOURTE_EXHIBITIONS_BY_UUID = "SELECT * FROM venue_favourite_users as e inner join \n" +
    		"venue as v on e.venue_uuid = v.uuid where  e.favourite_users_uuid=?1";
    
    String SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_UUID_2 = "" +
            "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        uuid, name, description, venue_type, address, lat, lon\n" +
            "    FROM\n" +
            "        (SELECT \n" +
            "        z.*,\n" +
            "            p.radius,\n" +
            "            p.distance_unit * DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.lat)) * COS(RADIANS(p.longpoint - z.lon)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.lat)))) AS distance\n" +
            "    FROM\n" +
            "        venue AS z\n" +
            "    JOIN (SELECT \n" +
            "            ?2 AS latpoint,\n" +
            "            ?3 AS longpoint,\n" +
            "            ?4 AS radius,\n" +
            "            ?5 AS distance_unit\n" +
            "    ) AS p\n" +
            "    WHERE\n" +
            "        z.lat BETWEEN p.latpoint - (p.radius / p.distance_unit) AND p.latpoint + (p.radius / p.distance_unit)\n" +
            "            AND z.lon BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d\n" +
            "    WHERE\n" +
            "    uuid in (?1) AND \n" +
            "        distance <= radius AND venue_type = 1\n" +
            "            AND (start_date IS NULL\n" +
            "              OR start_date < FROM_UNIXTIME(?6 / 1000))\n" +
            "            AND (end_date IS NULL " +
            "              OR end_date > FROM_UNIXTIME(?6 / 1000))\n" +
            "    ORDER BY uuid ASC) t\n" +
            "";

    String SQL_FILTER_ONLY_TAGS = "" + // 5
            "SELECT \n" +
            "    uuid\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "    venue_type = 1 AND uuid IN (SELECT \n" +
            "            venue_uuid\n" +
            "        FROM\n" +
            "            venue_tags\n" +
            "        WHERE\n" +
            "            tags_uuid IN (?1))" +
            "";

    String SQL_FILTER_TAGS_PRICE = "" + // 6
            "SELECT \n" +
            "    uuid\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "    venue_type = 1 AND uuid IN (SELECT \n" +
            "            venue_uuid\n" +
            "        FROM\n" +
            "            venue_tags\n" +
            "        WHERE\n" +
            "            tags_uuid IN (?1))\n" +
            "        AND ?2 <= fee\n" +
            "        AND fee <= ?3 " +
            "";

    String SQL_FILTER_TAGS_PRICE_POPULAR = "" + // 4
            "SELECT \n" +
            "    uuid\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "    venue_type = 1 AND uuid IN (SELECT \n" +
            "            venue_uuid\n" +
            "        FROM\n" +
            "            venue_tags\n" +
            "        WHERE\n" +
            "            tags_uuid IN (?1))\n" +
            "        AND ?2 <= fee\n" +
            "        AND fee <= ?3" +
            "        AND uuid in (?4) " +
            "";

    String SQL_FILTER_ONLY_POPULAR = "" + // 1
            "SELECT \n" +
            "    uuid\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "      venue_type = 1 AND uuid in (?1) " +
            "";

    String SQL_FILTER_PRICE_POPULAR = "" + // 3
            "SELECT \n" +
            "    uuid\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "     venue_type = 1 AND ?1 <= fee\n" +
            "        AND fee <= ?2" +
            "        AND uuid in (?3) " +
            "";

    String SQL_FILTER_TAGS_POPULAR = "" + // 2
            "SELECT \n" +
            "    uuid\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "    venue_type = 1 AND uuid IN (SELECT \n" +
            "            venue_uuid\n" +
            "        FROM\n" +
            "            venue_tags\n" +
            "        WHERE\n" +
            "            tags_uuid IN (?1))\n" +
            "        AND uuid in (?2) " +
            "";

    /*String SQL_FILTER_ONLY_PRICE = "" + // 7
            "SELECT \n" +
            "    uuid\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "    venue_type = 1 AND ?1 <= fee AND fee <= ?2 " +
            "";*/
    String SQL_FILTER_ONLY_PRICE = "SELECT uuid FROM  venue  WHERE ?1 <= fee AND fee <= ?2 ";  

    String SQL_FILTER_POPULAR_VENUE_IDS = "" +
            "SELECT \n" +
            "    venue_uuid\n" +
            "FROM\n" +
            "    venue_favourite_users\n" +
            "GROUP BY venue_uuid\n" +
            "HAVING COUNT(*)\n" +
            "ORDER BY COUNT(*) DESC\n" +
            "LIMIT 20" +
            "";

    /* Filters */

    @Query(value = SQL_FILTER_ONLY_PRICE, nativeQuery = true)
    List<BigInteger> filterByOnlyPrice(double priceMin, double priceMax);

    @Query(value = SQL_FILTER_TAGS_PRICE, nativeQuery = true)
    List<BigInteger> filterByTagsAndPrice(List<Long> lstTagIds, double priceMin, double priceMax);

    @Query(value = SQL_FILTER_TAGS_PRICE_POPULAR, nativeQuery = true)
    List<BigInteger> filterByTagsAndPriceAndPopular(List<Long> lstTagIds, double priceMin, double priceMax, List<Long> lstVenueIds);

    @Query(value = SQL_FILTER_TAGS_POPULAR, nativeQuery = true)
    List<BigInteger> filterByTagsAndPopular(List<Long> lstTagIds, List<Long> lstVenueIds);

    @Query(value = SQL_FILTER_PRICE_POPULAR, nativeQuery = true)
    List<BigInteger> filterByPriceAndPopular(double priceMin, double priceMax, List<Long> lstVenueIds);

    @Query(value = SQL_FILTER_ONLY_POPULAR, nativeQuery = true)
    List<BigInteger> filterByOnlyPopular(List<Long> lstVenueIds);

    @Query(value = SQL_FILTER_ONLY_TAGS, nativeQuery = true)
    List<BigInteger> filterByOnlyTags(List<Long> lstTagIds);

    /* Venue endpoints */

    @Query(value = SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS, nativeQuery = true)
    List<VenueSearchSummary> findNearbyExhibitions(double latitude, double longitude, double radius, float distanceUnitValue, Long now);

    @Query(value = SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_2, nativeQuery = true)
    List<VenueSearchSummary> findNearbyExhibitions2(double latitude, double longitude, double radius, float distanceUnitValue, Long now);

    @Query(value = SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_NAME, nativeQuery = true)
    List<VenueSearchSummary> findExhibitionsByNameContains(String search, double latitude, double longitude, double radius, float distanceUnitValue, Long now);

    @Query(value = SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_NAME_V2, nativeQuery = true)
    List<VenueSearchSummary> findExhibitionsByNameContains_V2(String search, double latitude, double longitude, double radius, float distanceUnitValue, Long now);

    
    
    @Query(value = SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_UUID, nativeQuery = true)
    List<VenueSearchSummary> findExhibitionsByUuidIn(List<Long> lstExhibitionIds, double latitude, double longitude, double radius, float distanceUnitValue, Long now);

    @Query(value = SQL_HAVERSINE_FORMULA_FOR_FAVOURTE_EXHIBITIONS_BY_UUID, nativeQuery = true)
//    List<VenueSearchSummary> findFavouriteExhibitionsByUuidIn(List<Long> lstExhibitionIds, double latitude, double longitude, double radius, float distanceUnitValue, Long now, Long userid);
    List<VenueSearchSummary> findFavouriteExhibitionsByUuidIn(Long userid);

    
    @Query(value = SQL_HAVERSINE_FORMULA_FOR_EXHIBITIONS_BY_UUID_2, nativeQuery = true)
    List<VenueSearchSummary> findExhibitionsByUuidIn2(List<Long> lstExhibitionIds, double latitude, double longitude, double radius, float distanceUnitValue, Long now);


    /* Other endpoints */

    @Query(value = SQL_FILTER_POPULAR_VENUE_IDS, nativeQuery = true)
    List<BigInteger> findMostPopularExhibitions();


}
