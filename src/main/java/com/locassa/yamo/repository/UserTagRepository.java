package com.locassa.yamo.repository;

import com.locassa.yamo.model.Tag;
import com.locassa.yamo.model.User;
import com.locassa.yamo.model.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    UserTag findByUserAndTag(User user, Tag tag);

    List<UserTag> findByUser(User user);

}
