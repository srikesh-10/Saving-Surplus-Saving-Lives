-- Create default admin account
-- Email: admin@ngo.com
-- Password: admin123
INSERT INTO USERS (email, password, user_type, name, phone, address, latitude, longitude, total_deliveries, people_helped) 
VALUES ('admin@ngo.com', 'admin123', 'ADMIN', 'System Administrator', '0000000000', 'NGO Headquarters', 0.0, 0.0, 0, 0)
ON CONFLICT DO NOTHING;
