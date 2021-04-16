package com.capstone.streetefficient.models;

public class Performance {

    private String rider_id;
    private double average_speed;
    private double average_time;
    private double average_score;

    public Performance() {
    }

    public Performance(String rider_id, double average_speed, double average_time, double average_score) {
        this.rider_id = rider_id;
        this.average_speed = average_speed;
        this.average_time = average_time;
        this.average_score = average_score;
    }

    public String getRider_id() {
        return rider_id;
    }

    public void setRider_id(String rider_id) {
        this.rider_id = rider_id;
    }

    public double getAverage_speed() {
        return average_speed;
    }

    public void setAverage_speed(double average_speed) {
        this.average_speed = average_speed;
    }

    public double getAverage_time() {
        return average_time;
    }

    public void setAverage_time(double average_time) {
        this.average_time = average_time;
    }

    public double getAverage_score() {
        return average_score;
    }

    public void setAverage_score(double average_score) {
        this.average_score = average_score;
    }
}
