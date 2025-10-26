package com.ngo.service;

import com.ngo.model.Donation;
import com.ngo.repository.DonationRepository;
import com.ngo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;
    
    @Autowired
    private UserRepository userRepository;

    public boolean createDonation(Donation donation) {
        try {
            donation.setStatus("AVAILABLE");
            donation.setCreatedAt(LocalDateTime.now());
            donationRepository.save(donation);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Donation> getAvailableDonations() {
        return donationRepository.getAvailableDonations();
    }

    public List<Donation> getDonationsByDonorId(int donorId) {
        return donationRepository.getDonationsByDonorId(donorId);
    }

    public List<Donation> getDonationsByVolunteerId(int volunteerId) {
        return donationRepository.getDonationsByVolunteerId(volunteerId);
    }

    public Donation getDonationById(int id) {
        return donationRepository.findById(id).orElse(null);
    }

    public synchronized boolean acceptDonation(int donationId, int volunteerId) {
        try {
            // Check if donation is still available (prevents multiple volunteers accepting same donation)
            if (!donationRepository.isAvailableForAcceptance(donationId)) {
                return false;
            }
            
            Donation donation = donationRepository.findById(donationId).orElse(null);
            if (donation != null && "AVAILABLE".equals(donation.getStatus())) {
                donationRepository.acceptDonation(donationId, volunteerId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean completeDonation(int donationId, int volunteerId) {
        try {
            donationRepository.completeDonation(donationId);
            // Update volunteer statistics
            userRepository.updateVolunteerStats(volunteerId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Donation> getNearbyDonations(double latitude, double longitude, double maxDistance) {
        return donationRepository.getNearbyDonations(latitude, longitude, maxDistance);
    }

    public int getTotalDonationsByDonor(int donorId) {
        return donationRepository.getTotalDonationsByDonor(donorId);
    }

    public int getTotalQuantityDonatedByDonor(int donorId) {
        return donationRepository.getTotalQuantityDonatedByDonor(donorId);
    }
    
    public int getPeopleHelpedByDonor(int donorId) {
        return donationRepository.getPeopleHelpedByDonor(donorId);
    }
    
    public List<Donation> getNearbyDonationsForVolunteer(int volunteerId, double maxDistance) {
        // Get volunteer's current location or default location
        // This method can be enhanced with volunteer's actual location
        return donationRepository.getAvailableDonations();
    }
    
    // Admin methods
    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }
    
    public void deleteDonation(int donationId) {
        donationRepository.deleteById(donationId);
    }
}
