package com.locassa.yamo.repository;

import com.locassa.yamo.model.view.UserLabelNormalisedValue;
import com.locassa.yamo.model.view.UserLabelNormalisedValueKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLabelNormalisedValueRepository extends JpaRepository<UserLabelNormalisedValue, UserLabelNormalisedValueKey> {

    @Query(value = "select * from v_user_label_n_values where user_uuid = ?1 and n_value > 0", nativeQuery = true)
    List<UserLabelNormalisedValue> findByUserId(Long userUuid);

}
