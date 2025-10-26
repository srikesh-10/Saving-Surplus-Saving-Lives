package com.ngo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "user_type", nullable = false)
    private String userType; // "DONOR", "VOLUNTEER", or "ADMIN"
    
    @Column(nullable = false)
    private String name;
    
    private String phone;
    private String address;
    private double latitude;
    private double longitude;
    
    // Volunteer-specific fields
    @Column(name = "max_distance")
    private Double maxDistance; // Maximum distance volunteer willing to travel
    
    @Column(name = "current_latitude")
    private Double currentLatitude; // Current location for volunteers
    
    @Column(name = "current_longitude")
    private Double currentLongitude; // Current location for volunteers
    
    @Column(name = "total_deliveries")
    private int totalDeliveries = 0;
    
    @Column(name = "people_helped")
    private int peopleHelped = 0;

    public User() {}

    public User(String email, String password, String userType, String name, String phone, String address) {
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.latitude = 0.0; // Default value
        this.longitude = 0.0; // Default value
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public Double getMaxDistance() { return maxDistance; }
    public void setMaxDistance(Double maxDistance) { this.maxDistance = maxDistance; }
    
    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }
    
    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }
    
    public int getTotalDeliveries() { return totalDeliveries; }
    public void setTotalDeliveries(int totalDeliveries) { this.totalDeliveries = totalDeliveries; }
    
    public int getPeopleHelped() { return peopleHelped; }
    public void setPeopleHelped(int peopleHelped) { this.peopleHelped = peopleHelped; }
}
