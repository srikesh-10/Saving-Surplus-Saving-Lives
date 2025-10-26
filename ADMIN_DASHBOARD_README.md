# Admin Dashboard - NGO Food Donation System

## Overview
The Admin Dashboard provides comprehensive management and monitoring capabilities for the NGO Food Donation platform. Administrators can view statistics, manage users, and oversee all donations.

## Features

### 1. **Dashboard Statistics**
- **Total Users**: View the total number of registered users
- **Total Donations**: Track all donations made through the platform
- **Total Food Items**: Monitor the total quantity of food items donated
- **People Helped**: Estimate of people benefited from the donations
- **Donors Count**: Number of registered donors
- **Volunteers Count**: Number of active volunteers
- **Completed Donations**: Track successful food distribution

### 2. **User Management**
- View all registered users (Donors and Volunteers)
- User details including:
  - ID, Name, Email, Phone
  - User Type (Donor/Volunteer)
  - Address
  - Statistics (for volunteers: deliveries and people helped)
- **Delete Users**: Remove user accounts when necessary

### 3. **Donation Management**
- View all donations in the system
- Donation details including:
  - ID, Food Type, Quantity
  - Pickup Address
  - Status (Available, Accepted, Completed)
  - Associated Donor and Volunteer IDs
  - Creation timestamp
- **Delete Donations**: Remove donation records

### 4. **Tabbed Interface**
- **Users Management Tab**: Manage all system users
- **Donations Management Tab**: Oversee all donation activities

## Default Admin Account

A default admin account is automatically created when the application starts:

```
Email: admin@ngo.com
Password: admin123
```

**⚠️ IMPORTANT**: Change the default password after first login for security purposes!

## Creating Additional Admin Accounts

### Option 1: Using Signup Page
1. Navigate to `/signup`
2. Fill in the registration form
3. Select "Admin - System Administrator" from the user type dropdown
4. Complete registration

### Option 2: Manual Database Entry
1. Access H2 Console at `http://localhost:8083/h2-console`
2. Connection settings:
   - JDBC URL: `jdbc:h2:file:./data/ngo_food_donation`
   - User: `sa`
   - Password: (leave empty)
3. Execute SQL:
```sql
INSERT INTO USERS (email, password, user_type, name, phone, address, latitude, longitude, total_deliveries, people_helped) 
VALUES ('your-email@example.com', 'your-password', 'ADMIN', 'Your Name', '1234567890', 'Your Address', 0.0, 0.0, 0, 0);
```

## Accessing the Admin Dashboard

1. Start the application:
   ```bash
   mvn spring-boot:run
   ```

2. Open your browser and navigate to:
   ```
   http://localhost:8083
   ```

3. Click on **Login**

4. Enter admin credentials:
   - Email: `admin@ngo.com`
   - Password: `admin123`

5. You will be automatically redirected to `/admin-dashboard`

## Admin Dashboard Sections

### Header
- Displays welcome message with admin name
- Logout button to end session

### Statistics Cards (Top Section)
Seven colorful cards displaying key metrics:
1. Total Users
2. Total Donations
3. Total Food Items
4. People Helped
5. Total Donors
6. Total Volunteers
7. Completed Donations

### Management Tables

#### Users Management Table
| Column | Description |
|--------|-------------|
| ID | User's unique identifier |
| Name | User's full name |
| Email | User's email address |
| Phone | Contact number |
| Type | User role (DONOR/VOLUNTEER) |
| Address | User's address |
| Stats | Relevant statistics (for volunteers) |
| Actions | Delete button |

#### Donations Management Table
| Column | Description |
|--------|-------------|
| ID | Donation's unique identifier |
| Food Type | Type of food donated |
| Quantity | Amount of food items |
| Pickup Address | Location for pickup |
| Status | Current status (AVAILABLE/ACCEPTED/COMPLETED) |
| Donor ID | ID of the donor |
| Volunteer ID | ID of assigned volunteer (if any) |
| Created At | Timestamp of donation creation |
| Actions | Delete button |

## Security Features

- Admin routes are protected and require authentication
- Only users with `ADMIN` user type can access admin dashboard
- Unauthorized access attempts redirect to login page
- Delete operations require confirmation

## Technical Details

### Backend Components

#### Controller
- `MainController.java`: Contains admin dashboard endpoint at `/admin-dashboard`
- Admin delete endpoints: `/admin/delete-user` and `/admin/delete-donation`

#### Service Layer
- `UserService.java`: Methods for user management (`getAllUsers()`, `deleteUser()`)
- `DonationService.java`: Methods for donation management (`getAllDonations()`, `deleteDonation()`)

#### Model
- `User.java`: Updated to support "ADMIN" user type
- `Donation.java`: Existing model for donations

### Frontend
- `admin-dashboard.html`: Responsive Thymeleaf template
- Beautiful gradient design with purple/violet theme
- Bootstrap 5 for responsive layout
- Font Awesome icons for visual appeal
- Interactive tables with hover effects
- Animated statistics cards

## Color Scheme
The admin dashboard uses a professional purple gradient theme:
- Primary: `#667eea` to `#764ba2`
- Various gradient combinations for different cards
- White content cards with subtle shadows
- Status badges with semantic colors (green for available, orange for accepted, blue for completed)

## API Endpoints

### GET Endpoints
- `/admin-dashboard` - Main admin dashboard page

### POST Endpoints
- `/admin/delete-user` - Delete a user by ID
  - Parameters: `userId` (int)
  - Returns: JSON response with status

- `/admin/delete-donation` - Delete a donation by ID
  - Parameters: `donationId` (int)
  - Returns: JSON response with status

## Future Enhancements

Potential features to add:
1. User editing capabilities
2. Donation status updates
3. Analytics and charts
4. Export data to CSV/Excel
5. Activity logs and audit trail
6. Email notifications to users
7. Role-based permissions (super admin, admin, moderator)
8. Dashboard widgets customization
9. Real-time updates using WebSocket
10. Advanced filtering and search

## Troubleshooting

### Cannot access admin dashboard
- Verify you're logged in with an admin account
- Check user type is set to "ADMIN" in database
- Clear browser cache and cookies

### Delete operations not working
- Ensure you're logged in as admin
- Check browser console for JavaScript errors
- Verify database connection

### Statistics not showing correctly
- Check if database has data
- Verify JPA queries are working
- Check application logs for errors

## Support

For issues or questions about the admin dashboard, please check:
1. Application logs in console
2. H2 Console for database inspection
3. Browser developer tools for frontend issues

---

**Note**: This admin dashboard is designed for managing the NGO Food Donation platform effectively. Always ensure proper authentication and authorization before performing any administrative actions.
