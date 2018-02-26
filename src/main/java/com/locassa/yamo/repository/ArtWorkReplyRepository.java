package com.locassa.yamo.repository;

import com.locassa.yamo.model.ArtWorkReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ArtWorkReplyRepository extends JpaRepository<ArtWorkReply, Long> {

    String SQL_FIND_LESS_REPLIED = "" +
            "SELECT \n" +
            "    art_work_uuid\n" +
            "FROM\n" +
            "    art_work_reply\n" +
            "WHERE\n" +
            "    user_uuid = ?1\n" +
            "        AND counter < (SELECT \n" +
            "            COALESCE(MAX(counter), 0)\n" +
            "        FROM\n" +
            "            art_work_reply\n" +
            "        WHERE\n" +
            "            user_uuid = ?1)" +
            "";

    @Query(value = "select art_work_uuid from art_work_reply where user_uuid = ?1", nativeQuery = true)
    List<BigInteger> findByUserUuid(Long userUuid);

    @Query(value = SQL_FIND_LESS_REPLIED, nativeQuery = true)
    List<BigInteger> findByUserUuidAndLessReplied(Long userUuid);

    ArtWorkReply findByUserUuidAndArtWorkUuid(Long userUuid, Long artWorkUuid);

}
