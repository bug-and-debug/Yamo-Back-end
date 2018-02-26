package com.locassa.yamo.repository;

import com.locassa.yamo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    String SQL_GET_TO_KNOW_ME_REMINDER_USERS = "" +
            "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    user\n" +
            "WHERE\n" +
            "    uuid IN (SELECT DISTINCT\n" +
            "            user_uuid\n" +
            "        FROM\n" +
            "            art_work_reply\n" +
            "        WHERE\n" +
            "            ?1 = DATEDIFF(SYSDATE(), created));" +
            "";

    String SQL_USERS_WHO_FAVOURITED_EXHIBITION = "" +
            "SELECT \n" +
            "    *\n" +
            "FROM\n" +
            "    user\n" +
            "WHERE\n" +
            "    uuid IN (SELECT DISTINCT\n" +
            "            favourite_users_uuid\n" +
            "        FROM\n" +
            "            venue_favourite_users\n" +
            "        WHERE\n" +
            "            venue_uuid = ?1)" +
            "";

    User findByEmailAndEnabledTrue(String email);

    User findByEmail(String email);

    User findBySecretCode(String secretCode);

    User findByEmailAndSecretCode(String email, String secretCode);

    List<User> findFirst10ByCreatedLessThanOrderByCreatedDesc(Date created);

    List<User> findFirst10ByCreatedGreaterThanOrderByCreatedAsc(Date created);

    @Transactional
    @Modifying
    @Query(value = "update user set user_type = ?3 where user_type = ?2 and uuid in (?1)", nativeQuery = true)
    void updateUserTypeFromTo(List<Long> userIds, int from, int to);

    List<User> findByFacebookIdIn(List<String> facebookIds);

    @Query(value = SQL_GET_TO_KNOW_ME_REMINDER_USERS, nativeQuery = true)
    List<User> findUsersForGetToKnowMeReminder(int days);

    @Query(value = SQL_USERS_WHO_FAVOURITED_EXHIBITION, nativeQuery = true)
    List<User> findUsersWhoFavouritedExhibition(Long exhibitionId);

}
