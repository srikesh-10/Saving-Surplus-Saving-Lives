package com.ngo.controller;

import com.ngo.model.Donation;
import com.ngo.model.User;
import com.ngo.service.DonationService;
import com.ngo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private DonationService donationService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, 
                       HttpSession session, Model model) {
        User user = userService.loginUser(email, password);
        if (user != null) {
            session.setAttribute("user", user);
            if ("ADMIN".equals(user.getUserType())) {
                return "redirect:/admin-dashboard";
            } else if ("DONOR".equals(user.getUserType())) {
                return "redirect:/donor-dashboard";
            } else {
                return "redirect:/volunteer-dashboard";
            }
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String email, @RequestParam String password,
                        @RequestParam String userType, @RequestParam String name,
                        @RequestParam String phone, @RequestParam String address,
                        @RequestParam(required = false) Double maxDistance,
                        Model model) {
        User user = new User(email, password, userType, name, phone, address);
        if ("VOLUNTEER".equals(userType) && maxDistance != null) {
            user.setMaxDistance(maxDistance);
        }
        if (userService.registerUser(user)) {
            model.addAttribute("success", "Registration successful! Please login.");
            return "login";
        } else {
            model.addAttribute("error", "Email already exists or registration failed");
            return "signup";
        }
    }

    @GetMapping("/donor-dashboard")
    public String donorDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"DONOR".equals(user.getUserType())) {
            return "redirect:/login";
        }

        List<Donation> donations = donationService.getDonationsByDonorId(user.getId());
        int totalDonations = donationService.getTotalDonationsByDonor(user.getId());
        int totalQuantity = donationService.getTotalQuantityDonatedByDonor(user.getId());
        int peopleHelped = donationService.getPeopleHelpedByDonor(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("donations", donations);
        model.addAttribute("totalDonations", totalDonations);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("peopleHelped", peopleHelped);
        return "donor-dashboard";
    }

    @GetMapping("/volunteer-dashboard")
    public String volunteerDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"VOLUNTEER".equals(user.getUserType())) {
            return "redirect:/login";
        }

        List<Donation> acceptedDonations = donationService.getDonationsByVolunteerId(user.getId());
        List<Donation> availableDonations = donationService.getAvailableDonations();
        
        model.addAttribute("user", user);
        model.addAttribute("acceptedDonations", acceptedDonations);
        model.addAttribute("availableDonations", availableDonations);
        return "volunteer-dashboard";
    }

    @PostMapping("/add-donation")
    public String addDonation(@RequestParam String foodType, @RequestParam int quantity,
                             @RequestParam String description, @RequestParam String pickupAddress,
                             @RequestParam double latitude, @RequestParam double longitude,
                             @RequestParam String expiryDate, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"DONOR".equals(user.getUserType())) {
            return "redirect:/login";
        }

        try {
            LocalDateTime expiry;
            // Check if expiryDate already contains time information
            if (expiryDate.contains("T")) {
                // If it already contains time, parse as is
                expiry = LocalDateTime.parse(expiryDate);
            } else {
                // If it's just a date, append end of day time
                expiry = LocalDateTime.parse(expiryDate + "T23:59:59");
            }
            
            Donation donation = new Donation(user.getId(), foodType, quantity, description, 
                                           pickupAddress, latitude, longitude, expiry);
            donationService.createDonation(donation);
            return "redirect:/donor-dashboard";
        } catch (Exception e) {
            // If date parsing fails, redirect back to dashboard with error
            System.err.println("Date parsing error: " + e.getMessage());
            return "redirect:/donor-dashboard?error=Invalid date format";
        }
    }

    @PostMapping("/accept-donation")
    @ResponseBody
    public String acceptDonation(@RequestParam int donationId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"VOLUNTEER".equals(user.getUserType())) {
            return "error";
        }

        if (donationService.acceptDonation(donationId, user.getId())) {
            return "success";
        } else {
            return "error";
        }
    }

    @PostMapping("/complete-donation")
    @ResponseBody
    public String completeDonation(@RequestParam int donationId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"VOLUNTEER".equals(user.getUserType())) {
            return "error";
        }

        if (donationService.completeDonation(donationId, user.getId())) {
            return "success";
        } else {
            return "error";
        }
    }

    @GetMapping("/nearby-donations")
    @ResponseBody
    public List<Donation> getNearbyDonations(@RequestParam double latitude, 
                                           @RequestParam double longitude,
                                           @RequestParam double maxDistance) {
        return donationService.getNearbyDonations(latitude, longitude, maxDistance);
    }

    @PostMapping("/update-volunteer-location")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateVolunteerLocation(
            @RequestParam double latitude, @RequestParam double longitude, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, String> response = new HashMap<>();
        
        if (user == null || !"VOLUNTEER".equals(user.getUserType())) {
            response.put("status", "error");
            response.put("message", "Unauthorized");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            userService.updateVolunteerLocation(user.getId(), latitude, longitude);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to update location");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/update-max-distance")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateMaxDistance(
            @RequestParam double maxDistance, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, String> response = new HashMap<>();
        
        if (user == null || !"VOLUNTEER".equals(user.getUserType())) {
            response.put("status", "error");
            response.put("message", "Unauthorized");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            userService.updateVolunteerMaxDistance(user.getId(), maxDistance);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to update max distance");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/volunteer-stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getVolunteerStats(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        
        if (user == null || !"VOLUNTEER".equals(user.getUserType())) {
            response.put("error", "Unauthorized");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("totalDeliveries", user.getTotalDeliveries());
        response.put("peopleHelped", user.getPeopleHelped());
        response.put("maxDistance", user.getMaxDistance());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/donor-analytics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDonorAnalytics(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        
        if (user == null || !"DONOR".equals(user.getUserType())) {
            response.put("error", "Unauthorized");
            return ResponseEntity.badRequest().body(response);
        }

        int totalDonations = donationService.getTotalDonationsByDonor(user.getId());
        int totalQuantity = donationService.getTotalQuantityDonatedByDonor(user.getId());
        int peopleHelped = donationService.getPeopleHelpedByDonor(user.getId());
        
        response.put("totalDonations", totalDonations);
        response.put("totalQuantity", totalQuantity);
        response.put("peopleHelped", peopleHelped);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getUserType())) {
            return "redirect:/login";
        }

        // Get all users
        List<User> allUsers = userService.getAllUsers();
        List<User> donors = allUsers.stream()
            .filter(u -> "DONOR".equals(u.getUserType()))
            .toList();
        List<User> volunteers = allUsers.stream()
            .filter(u -> "VOLUNTEER".equals(u.getUserType()))
            .toList();
        
        // Get all donations
        List<Donation> allDonations = donationService.getAllDonations();
        List<Donation> availableDonations = allDonations.stream()
            .filter(d -> "AVAILABLE".equals(d.getStatus()))
            .toList();
        List<Donation> completedDonations = allDonations.stream()
            .filter(d -> "COMPLETED".equals(d.getStatus()))
            .toList();
        
        // Calculate statistics
        int totalUsers = allUsers.size();
        int totalDonations = allDonations.size();
        int totalQuantity = allDonations.stream().mapToInt(Donation::getQuantity).sum();
        int totalPeopleHelped = completedDonations.stream().mapToInt(Donation::getQuantity).sum() * 3; // Estimate 3 people per quantity
        
        model.addAttribute("user", user);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalDonors", donors.size());
        model.addAttribute("totalVolunteers", volunteers.size());
        model.addAttribute("totalDonations", totalDonations);
        model.addAttribute("availableDonations", availableDonations.size());
        model.addAttribute("completedDonations", completedDonations.size());
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalPeopleHelped", totalPeopleHelped);
        model.addAttribute("donors", donors);
        model.addAttribute("volunteers", volunteers);
        model.addAttribute("allDonations", allDonations);
        
        return "admin-dashboard";
    }
    
    @PostMapping("/admin/delete-user")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteUser(@RequestParam int userId, HttpSession session) {
        User admin = (User) session.getAttribute("user");
        Map<String, String> response = new HashMap<>();
        
        if (admin == null || !"ADMIN".equals(admin.getUserType())) {
            response.put("status", "error");
            response.put("message", "Unauthorized");
            return ResponseEntity.status(403).body(response);
        }
        
        try {
            userService.deleteUser(userId);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to delete user");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/admin/delete-donation")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteDonation(@RequestParam int donationId, HttpSession session) {
        User admin = (User) session.getAttribute("user");
        Map<String, String> response = new HashMap<>();
        
        if (admin == null || !"ADMIN".equals(admin.getUserType())) {
            response.put("status", "error");
            response.put("message", "Unauthorized");
            return ResponseEntity.status(403).body(response);
        }
        
        try {
            donationService.deleteDonation(donationId);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to delete donation");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
