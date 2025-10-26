package com.ngo.service;

import com.ngo.model.User;
import com.ngo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean registerUser(User user) {
        try {
            System.out.println("DEBUG: Attempting to register user: " + user.getEmail());
            System.out.println("DEBUG: User details - Name: " + user.getName() + ", Phone: " + user.getPhone() + ", UserType: " + user.getUserType());
            System.out.println("DEBUG: User location - Lat: " + user.getLatitude() + ", Lng: " + user.getLongitude());
            
            // Check if user already exists
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null) {
                System.out.println("DEBUG: User already exists with email: " + user.getEmail());
                return false;
            }
            
            System.out.println("DEBUG: Creating new user...");
            userRepository.save(user);
            System.out.println("DEBUG: User created successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("DEBUG: Error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUserLocation(int userId, double latitude, double longitude) {
        userRepository.updateUserLocation(userId, latitude, longitude);
    }

    public List<User> getAllVolunteers() {
        return userRepository.findByUserType("VOLUNTEER");
    }
    
    public void updateVolunteerLocation(int userId, double currentLatitude, double currentLongitude) {
        userRepository.updateVolunteerLocation(userId, currentLatitude, currentLongitude);
    }
    
    public void updateVolunteerMaxDistance(int userId, double maxDistance) {
        userRepository.updateVolunteerMaxDistance(userId, maxDistance);
    }
    
    public void updateVolunteerStats(int userId) {
        userRepository.updateVolunteerStats(userId);
    }
    
    public List<User> getVolunteersWithinRange(double latitude, double longitude, double maxDistance) {
        return userRepository.getVolunteersWithinRange(maxDistance);
    }
    
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    
    // Admin methods
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }
}
