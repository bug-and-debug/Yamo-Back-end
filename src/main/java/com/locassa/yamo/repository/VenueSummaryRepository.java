package com.locassa.yamo.repository;

import com.locassa.yamo.model.summary.VenueSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueSummaryRepository extends JpaRepository<VenueSummary, Long> {

    String SQL_POPULAR_VENUES = "" +
            "SELECT \n" +
            "    v.uuid, v.name, v.description, v.venue_type\n" +
            "FROM\n" +
            "    venue v\n" +
            "WHERE\n" +
            "    v.uuid IN (SELECT \n" +
            "            t.venue_uuid\n" +
            "        FROM\n" +
            "            (SELECT \n" +
            "                rs.venue_uuid, COUNT(*)\n" +
            "            FROM\n" +
            "                route r, route_step rs\n" +
            "            WHERE\n" +
            "                r.uuid = rs.parent_uuid\n" +
            "                    AND r.user_uuid = ?1\n" +
            "            GROUP BY rs.venue_uuid\n" +
            "            HAVING COUNT(*) >= 3\n" +
            "            ORDER BY 2 DESC\n" +
            "            LIMIT 2) t)" +
            "";

    String SQL_VENUES_FOR_PROFILE = "" +
            "SELECT \n" +
            "    uuid, name, description, venue_type\n" +
            "FROM\n" +
            "    venue\n" +
            "WHERE\n" +
            "    uuid IN (SELECT \n" +
            "            rs.venue_uuid\n" +
            "        FROM\n" +
            "            route r,\n" +
            "            route_step rs\n" +
            "        WHERE\n" +
            "            r.uuid = rs.parent_uuid\n" +
            "                AND r.user_uuid = ?1)\n" +
            "ORDER BY name ASC" +
            "";

    @Query(value = SQL_POPULAR_VENUES, nativeQuery = true)
    List<VenueSummary> findPopularVenuesForProfile(Long userId);

    @Query(value = SQL_VENUES_FOR_PROFILE, nativeQuery = true)
    List<VenueSummary> findVenuesForProfile(Long userId);

}
