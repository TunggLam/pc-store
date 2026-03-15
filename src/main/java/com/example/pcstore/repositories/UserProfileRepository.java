package com.example.pcstore.repositories;

import com.example.pcstore.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByUsername(String username);

    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findByKeycloakId(String keycloakId);

    @Query(value = "select keycloak_id from user_profile where username = :username", nativeQuery = true)
    String getKeycloakIdByUsername(@Param("username") String username);

    @Query(value = "select * from user_profile order by created_at desc", nativeQuery = true)
    List<UserProfile> findUserProfilesOrderByCreatedAtDesc();

    @Query(value = "select address from user_profile where username = :username", nativeQuery = true)
    String getAddressByUsername(@Param("username") String username);

    @Query(value = "select username from user_profile where email = :email", nativeQuery = true)
    String getUsernameByEmail(@Param("email") String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    Boolean existsByEmail(String email);
}
