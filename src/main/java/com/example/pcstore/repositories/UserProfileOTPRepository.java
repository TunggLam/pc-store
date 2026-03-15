package com.example.pcstore.repositories;

import com.example.pcstore.entity.UserProfileOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserProfileOTPRepository extends JpaRepository<UserProfileOTP, String> {
    @Query(value = "select  * from user_profile_otp where username = :username and type =:type order by created_at desc limit 1", nativeQuery = true)
    UserProfileOTP getLatestOTP(String username, String type);

    @Transactional
    @Modifying
    @Query(value = "update user_profile_otp set status = false where username =:username and type =:type",nativeQuery = true)
    void inactiveAllStatus(String username, String type);

    @Query(value = "select count(1) from user_profile_otp where username = :username and type =:type and to_char(created_at, 'ddMMyyyy') = to_char(now(),'ddMMyyyy') limit 10", nativeQuery = true)
    Integer getTotalOTPToday(String username, String type);
}
