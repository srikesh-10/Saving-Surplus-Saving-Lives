-- NGO Food Donation Database Schema
-- Run this script to create the database and tables

CREATE DATABASE IF NOT EXISTS ngo_food_donation;
USE ngo_food_donation;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('DONOR', 'VOLUNTEER') NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    latitude DECIMAL(10, 8) DEFAULT 0.00000000,
    longitude DECIMAL(11, 8) DEFAULT 0.00000000,
    max_distance DECIMAL(8, 2) DEFAULT NULL,
    current_latitude DECIMAL(10, 8) DEFAULT NULL,
    current_longitude DECIMAL(11, 8) DEFAULT NULL,
    total_deliveries INT DEFAULT 0,
    people_helped INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Donations table
CREATE TABLE IF NOT EXISTS donations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    food_type VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    description TEXT,
    pickup_address TEXT NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    expiry_date DATETIME NOT NULL,
    status ENUM('AVAILABLE', 'ACCEPTED', 'COMPLETED') DEFAULT 'AVAILABLE',
    volunteer_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (volunteer_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_type ON users(user_type);
CREATE INDEX idx_donations_status ON donations(status);
CREATE INDEX idx_donations_donor ON donations(donor_id);
CREATE INDEX idx_donations_volunteer ON donations(volunteer_id);
CREATE INDEX idx_donations_location ON donations(latitude, longitude);
CREATE INDEX idx_donations_expiry ON donations(expiry_date);

-- Insert sample data (optional)
INSERT INTO users (email, password, user_type, name, phone, address, latitude, longitude) VALUES
('donor@example.com', 'password123', 'DONOR', 'John Doe', '123-456-7890', '123 Main St, City, State', 40.7128, -74.0060),
('volunteer@example.com', 'password123', 'VOLUNTEER', 'Jane Smith', '098-765-4321', '456 Oak Ave, City, State', 40.7589, -73.9851);

INSERT INTO donations (donor_id, food_type, quantity, description, pickup_address, latitude, longitude, expiry_date) VALUES
(1, 'Vegetables', 10, 'Fresh vegetables from local farm', '123 Main St, City, State', 40.7128, -74.0060, DATE_ADD(NOW(), INTERVAL 2 DAY)),
(1, 'Fruits', 15, 'Mixed fruits basket', '123 Main St, City, State', 40.7128, -74.0060, DATE_ADD(NOW(), INTERVAL 1 DAY));
