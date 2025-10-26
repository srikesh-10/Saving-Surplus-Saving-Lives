package com.ngo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DONATIONS")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "donor_id", nullable = false)
    private int donorId;
    
    @Column(name = "food_type", nullable = false)
    private String foodType;
    
    @Column(nullable = false)
    private int quantity;
    
    private String description;
    
    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;
    
    private double latitude;
    private double longitude;
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    private String status; // "AVAILABLE", "ACCEPTED", "COMPLETED"
    
    @Column(name = "volunteer_id")
    private Integer volunteerId; // null if not accepted
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Donation() {}

    public Donation(int donorId, String foodType, int quantity, String description, 
                   String pickupAddress, double latitude, double longitude, LocalDateTime expiryDate) {
        this.donorId = donorId;
        this.foodType = foodType;
        this.quantity = quantity;
        this.description = description;
        this.pickupAddress = pickupAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expiryDate = expiryDate;
        this.status = "AVAILABLE";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getVolunteerId() { return volunteerId; }
    public void setVolunteerId(Integer volunteerId) { this.volunteerId = volunteerId; }

    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
