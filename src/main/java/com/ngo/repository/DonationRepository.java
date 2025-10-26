package com.ngo.repository;

import com.ngo.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Integer> {

    @Query("SELECT d FROM Donation d WHERE d.status = 'AVAILABLE' AND d.expiryDate > CURRENT_TIMESTAMP ORDER BY d.createdAt DESC")
    List<Donation> getAvailableDonations();

    @Query("SELECT d FROM Donation d WHERE d.donorId = :donorId ORDER BY d.createdAt DESC")
    List<Donation> getDonationsByDonorId(@Param("donorId") int donorId);

    @Query("SELECT d FROM Donation d WHERE d.volunteerId = :volunteerId ORDER BY d.acceptedAt DESC")
    List<Donation> getDonationsByVolunteerId(@Param("volunteerId") int volunteerId);

    @Modifying
    @Transactional
    @Query("UPDATE Donation d SET d.status = 'ACCEPTED', d.volunteerId = :volunteerId, d.acceptedAt = CURRENT_TIMESTAMP WHERE d.id = :donationId AND d.status = 'AVAILABLE'")
    void acceptDonation(@Param("donationId") int donationId, @Param("volunteerId") int volunteerId);

    @Modifying
    @Transactional
    @Query("UPDATE Donation d SET d.status = 'COMPLETED', d.completedAt = CURRENT_TIMESTAMP WHERE d.id = :donationId")
    void completeDonation(@Param("donationId") int donationId);

    @Query("SELECT d FROM Donation d WHERE d.status = 'AVAILABLE' AND d.expiryDate > CURRENT_TIMESTAMP ORDER BY d.createdAt DESC")
    List<Donation> getNearbyDonations(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("maxDistance") double maxDistance);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.donorId = :donorId")
    int getTotalDonationsByDonor(@Param("donorId") int donorId);

    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM Donation d WHERE d.donorId = :donorId AND d.status = 'COMPLETED'")
    int getTotalQuantityDonatedByDonor(@Param("donorId") int donorId);
    
    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM Donation d WHERE d.donorId = :donorId AND d.status = 'COMPLETED'")
    int getPeopleHelpedByDonor(@Param("donorId") int donorId);
    
    @Query("SELECT COUNT(d) > 0 FROM Donation d WHERE d.id = :donationId AND d.status = 'AVAILABLE'")
    boolean isAvailableForAcceptance(@Param("donationId") int donationId);
}
