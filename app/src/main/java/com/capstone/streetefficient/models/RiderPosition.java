package com.capstone.streetefficient.models;

import com.google.android.gms.maps.model.LatLng;

public class RiderPosition {
    private String courier_id;
    private double latitude;
    private double longitude;
    private String rider_contactNumber;
    private String rider_id;
    private String rider_name;
    private String vehicle_type;
    private boolean status;
    private String destination;
    private String branch;

    public RiderPosition(String courier_id, double latitude, double longitude, String rider_contactNumber, String rider_id, String rider_name, String vehicle_type, boolean status, String destination, String branch) {
        this.courier_id = courier_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rider_contactNumber = rider_contactNumber;
        this.rider_id = rider_id;
        this.rider_name = rider_name;
        this.vehicle_type = vehicle_type;
        this.status = status;
        this.destination = destination;
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCourier_id() {
        return courier_id;
    }

    public void setCourier_id(String courier_id) {
        this.courier_id = courier_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getRider_contactNumber() {
        return rider_contactNumber;
    }

    public void setRider_contactNumber(String rider_contactNumber) {
        this.rider_contactNumber = rider_contactNumber;
    }

    public String getRider_id() {
        return rider_id;
    }

    public void setRider_id(String rider_id) {
        this.rider_id = rider_id;
    }

    public String getRider_name() {
        return rider_name;
    }

    public void setRider_name(String rider_name) {
        this.rider_name = rider_name;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
}
