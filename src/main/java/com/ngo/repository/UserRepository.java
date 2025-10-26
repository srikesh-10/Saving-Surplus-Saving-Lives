package com.ngo.repository;

import com.ngo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
    
    User findByEmailAndPassword(String email, String password);
    
    List<User> findByUserType(String userType);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.latitude = :latitude, u.longitude = :longitude WHERE u.id = :userId")
    void updateUserLocation(@Param("userId") int userId, @Param("latitude") double latitude, @Param("longitude") double longitude);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.currentLatitude = :currentLatitude, u.currentLongitude = :currentLongitude WHERE u.id = :userId")
    void updateVolunteerLocation(@Param("userId") int userId, @Param("currentLatitude") double currentLatitude, @Param("currentLongitude") double currentLongitude);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.maxDistance = :maxDistance WHERE u.id = :userId")
    void updateVolunteerMaxDistance(@Param("userId") int userId, @Param("maxDistance") double maxDistance);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.totalDeliveries = u.totalDeliveries + 1, u.peopleHelped = u.peopleHelped + 1 WHERE u.id = :userId")
    void updateVolunteerStats(@Param("userId") int userId);
    
    @Query("SELECT u FROM User u WHERE u.userType = 'VOLUNTEER' AND (u.maxDistance IS NULL OR u.maxDistance >= :maxDistance)")
    List<User> getVolunteersWithinRange(@Param("maxDistance") double maxDistance);
}
