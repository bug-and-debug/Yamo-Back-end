package com.locassa.yamo.repository;

import com.locassa.yamo.model.summary.UserSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSummaryRepository extends JpaRepository<UserSummary, Long> {

    String SQL_FOLLOWERS = "" +
            "SELECT \n" +
            "    (SELECT \n" +
            "            COALESCE((SELECT \n" +
            "                                followers_uuid\n" +
            "                            FROM\n" +
            "                                user_followers\n" +
            "                            WHERE\n" +
            "                                followers_uuid = ?1 AND user_uuid = uuid\n" +
            "                            LIMIT 1),\n" +
            "                        0)\n" +
            "        ) AS following,\n" +
            "    email,\n" +
            "    CASE nick_name_enabled\n" +
            "        WHEN '1' THEN nickname\n" +
            "        ELSE RTRIM(first_name || ' ' || last_name)\n" +
            "    END AS username,\n" +
            "    uuid,\n" +
            "    profile_image_url,\n" +
            "    nickname,\n" +
            "    nick_name_enabled,\n" +
            "    first_name,\n" +
            "    last_name,\n" +
            "    facebook_id\n" +
            "FROM\n" +
            "    user\n" +
            "WHERE\n" +
            "    uuid IN (SELECT \n" +
            "            followers_uuid\n" +
            "        FROM\n" +
            "            user_followers\n" +
            "        WHERE\n" +
            "            user_uuid = ?2)\n" +
            "ORDER BY 1 ASC" +
            "";

    String SQL_FOLLOWING = "" +
            "SELECT \n" +
            "    (SELECT \n" +
            "            COALESCE((SELECT \n" +
            "                                followers_uuid\n" +
            "                            FROM\n" +
            "                                user_followers\n" +
            "                            WHERE\n" +
            "                                followers_uuid = ?1 AND user_uuid = uuid\n" +
            "                            LIMIT 1),\n" +
            "                        0)\n" +
            "        ) AS following,\n" +
            "    email,\n" +
            "    CASE nick_name_enabled\n" +
            "        WHEN '1' THEN nickname\n" +
            "        ELSE RTRIM(first_name || ' ' || last_name)\n" +
            "    END AS username,\n" +
            "    uuid,\n" +
            "    profile_image_url,\n" +
            "    nickname,\n" +
            "    nick_name_enabled,\n" +
            "    first_name,\n" +
            "    last_name,\n" +
            "    facebook_id\n" +
            "FROM\n" +
            "    user\n" +
            "WHERE\n" +
            "    uuid IN (SELECT \n" +
            "            user_uuid\n" +
            "        FROM\n" +
            "            user_followers\n" +
            "        WHERE\n" +
            "            followers_uuid = ?2)\n" +
            "ORDER BY 1 ASC" +
            "";

    @Query(value = SQL_FOLLOWERS, nativeQuery = true)
    List<UserSummary> findFollowersForProfile(Long authUserId, Long userId);

    @Query(value = SQL_FOLLOWING, nativeQuery = true)
    List<UserSummary> findFollowingForProfile(Long authUserId, Long userId);

}
