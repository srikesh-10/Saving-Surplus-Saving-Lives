// Maps functionality for NGO Food Donation App using Leaflet.js

let donorMap, volunteerMap, currentLocationMarker, donationsLayer, routeLayer;

// Initialize donor dashboard map
function initDonorMap() {
    if (document.getElementById('donor-map')) {
        donorMap = L.map('donor-map').setView([40.7128, -74.0060], 13);
        
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(donorMap);
        
        // Add click event to set donation location
        donorMap.on('click', function(e) {
            setDonationLocation(e.latlng.lat, e.latlng.lng);
        });
        
        // Get user's current location
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                donorMap.setView([lat, lng], 15);
                setDonationLocation(lat, lng);
            });
        }
    }
}

// Initialize volunteer dashboard map
function initVolunteerMap() {
    if (document.getElementById('volunteer-map')) {
        volunteerMap = L.map('volunteer-map').setView([40.7128, -74.0060], 13);
        
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(volunteerMap);
        
        // Initialize donations layer
        donationsLayer = L.layerGroup().addTo(volunteerMap);
        routeLayer = L.layerGroup().addTo(volunteerMap);
        
        // Get volunteer's current location
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                volunteerMap.setView([lat, lng], 13);
                
                // Update volunteer location on server
                updateVolunteerLocation(lat, lng);
                
                // Set current location marker
                currentLocationMarker = L.marker([lat, lng], {
                    icon: L.divIcon({
                        className: 'current-location-marker',
                        html: '<div style="background: #4285f4; width: 15px; height: 15px; border-radius: 50%; border: 3px solid white;"></div>',
                        iconSize: [21, 21]
                    })
                }).addTo(volunteerMap);
                
                // Load nearby donations
                loadNearbyDonations(lat, lng);
            });
        }
    }
}

// Set donation location for donor
function setDonationLocation(lat, lng) {
    if (donorMap.donationMarker) {
        donorMap.removeLayer(donorMap.donationMarker);
    }
    
    donorMap.donationMarker = L.marker([lat, lng]).addTo(donorMap);
    
    // Update form fields
    document.getElementById('donation-latitude').value = lat;
    document.getElementById('donation-longitude').value = lng;
    
    // Reverse geocoding to get address
    fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`)
        .then(response => response.json())
        .then(data => {
            if (data.display_name) {
                document.getElementById('pickup-address').value = data.display_name;
            }
        });
}

// Update volunteer location on server
function updateVolunteerLocation(lat, lng) {
    fetch('/update-volunteer-location', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `latitude=${lat}&longitude=${lng}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.status !== 'success') {
            console.error('Failed to update location:', data.message);
        }
    });
}

// Load nearby donations for volunteer
function loadNearbyDonations(volunteerLat, volunteerLng) {
    const maxDistance = parseFloat(document.getElementById('max-distance')?.value) || 10;
    
    fetch(`/nearby-donations?latitude=${volunteerLat}&longitude=${volunteerLng}&maxDistance=${maxDistance}`)
        .then(response => response.json())
        .then(donations => {
            donationsLayer.clearLayers();
            
            donations.forEach(donation => {
                const marker = L.marker([donation.latitude, donation.longitude], {
                    icon: L.divIcon({
                        className: 'donation-marker',
                        html: `<div class="donation-pin">
                                <i class="fas fa-utensils"></i>
                                <span class="quantity">${donation.quantity}</span>
                               </div>`,
                        iconSize: [40, 40]
                    })
                }).addTo(donationsLayer);
                
                marker.bindPopup(`
                    <div class="donation-popup">
                        <h4>${donation.foodType}</h4>
                        <p><strong>Quantity:</strong> ${donation.quantity}</p>
                        <p><strong>Address:</strong> ${donation.pickupAddress}</p>
                        <p><strong>Expires:</strong> ${new Date(donation.expiryDate).toLocaleDateString()}</p>
                        <button onclick="acceptDonation(${donation.id})" class="btn btn-accept">Accept Donation</button>
                        <button onclick="showRoute(${donation.latitude}, ${donation.longitude})" class="btn btn-route">Show Route</button>
                    </div>
                `);
            });
        });
}

// Show route to donation location
function showRoute(donationLat, donationLng) {
    if (currentLocationMarker) {
        const volunteerLat = currentLocationMarker.getLatLng().lat;
        const volunteerLng = currentLocationMarker.getLatLng().lng;
        
        // Clear existing route
        routeLayer.clearLayers();
        
        // Add route using OSRM routing service
        fetch(`https://router.project-osrm.org/route/v1/driving/${volunteerLng},${volunteerLat};${donationLng},${donationLat}?geometries=geojson`)
            .then(response => response.json())
            .then(data => {
                if (data.routes && data.routes.length > 0) {
                    const route = data.routes[0];
                    const routeLine = L.geoJSON(route.geometry, {
                        style: {
                            color: '#4285f4',
                            weight: 5,
                            opacity: 0.7
                        }
                    }).addTo(routeLayer);
                    
                    // Fit map to show the route
                    volunteerMap.fitBounds(routeLine.getBounds(), { padding: [20, 20] });
                    
                    // Show route info
                    const distance = (route.distance / 1000).toFixed(1);
                    const duration = Math.round(route.duration / 60);
                    
                    L.popup()
                        .setLatLng([donationLat, donationLng])
                        .setContent(`
                            <div class="route-info">
                                <h4>Route Information</h4>
                                <p><strong>Distance:</strong> ${distance} km</p>
                                <p><strong>Duration:</strong> ${duration} minutes</p>
                            </div>
                        `)
                        .openOn(volunteerMap);
                }
            })
            .catch(error => {
                console.error('Route calculation failed:', error);
                // Fallback: draw straight line
                const straightLine = L.polyline([
                    [volunteerLat, volunteerLng],
                    [donationLat, donationLng]
                ], {
                    color: '#f44336',
                    weight: 3,
                    opacity: 0.7,
                    dashArray: '5, 10'
                }).addTo(routeLayer);
                
                volunteerMap.fitBounds(straightLine.getBounds(), { padding: [20, 20] });
            });
    }
}

// Accept donation
function acceptDonation(donationId) {
    if (confirm('Are you sure you want to accept this donation?')) {
        fetch('/accept-donation', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `donationId=${donationId}`
        })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('Donation accepted successfully!');
                location.reload(); // Refresh to update the UI
            } else {
                alert('Failed to accept donation. It may have been accepted by another volunteer.');
            }
        });
    }
}

// Mark donation as completed
function completeDonation(donationId) {
    if (confirm('Are you sure you have completed this delivery?')) {
        fetch('/complete-donation', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `donationId=${donationId}`
        })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                alert('Donation marked as completed!');
                location.reload(); // Refresh to update the UI
            } else {
                alert('Failed to mark donation as completed.');
            }
        });
    }
}

// Update max distance setting
function updateMaxDistance() {
    const maxDistance = parseFloat(document.getElementById('max-distance-input').value);
    
    if (maxDistance > 0) {
        fetch('/update-max-distance', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `maxDistance=${maxDistance}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                alert('Max distance updated successfully!');
                // Reload nearby donations with new distance
                if (currentLocationMarker && volunteerMap) {
                    const lat = currentLocationMarker.getLatLng().lat;
                    const lng = currentLocationMarker.getLatLng().lng;
                    loadNearbyDonations(lat, lng);
                }
            } else {
                alert('Failed to update max distance.');
            }
        });
    }
}

// Initialize geolocation tracking for volunteers
function startLocationTracking() {
    if (navigator.geolocation) {
        navigator.geolocation.watchPosition(function(position) {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;
            
            // Update volunteer location on server
            updateVolunteerLocation(lat, lng);
            
            // Update marker position
            if (currentLocationMarker) {
                currentLocationMarker.setLatLng([lat, lng]);
            }
        }, function(error) {
            console.error('Geolocation error:', error);
        }, {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 300000 // 5 minutes
        });
    }
}

// Initialize maps when page loads
document.addEventListener('DOMContentLoaded', function() {
    // Initialize appropriate map based on page
    if (document.getElementById('donor-map')) {
        initDonorMap();
    } else if (document.getElementById('volunteer-map')) {
        initVolunteerMap();
        startLocationTracking();
    }
});