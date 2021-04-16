package com.capstone.streetefficient.models;

import java.io.Serializable;
import java.util.Date;

public class RouteHeader implements Serializable {

    private Date date;
    private String rider_id;
    private String date_string;
    private String route_header;

    private double total_score;
    private double average_speed;
    private double total_distance;
    private double total_delivery_time;

    private boolean followed_route;

    public RouteHeader() {
    }

    public RouteHeader(String date_string, Date date, String rider_id, String route_header, double total_score,
                       double average_speed, double total_delivery_time, double total_distance, boolean followed_route) {
        this.date = date;
        this.rider_id = rider_id;
        this.date_string = date_string;
        this.total_score = total_score;
        this.route_header = route_header;
        this.average_speed = average_speed;
        this.followed_route = followed_route;
        this.total_distance = total_distance;
        this.total_delivery_time = total_delivery_time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRider_id() {
        return rider_id;
    }

    public void setRider_id(String rider_id) {
        this.rider_id = rider_id;
    }

    public String getRoute_header() {
        return route_header;
    }

    public void setRoute_header(String route_header) {
        this.route_header = route_header;
    }

    public double getTotal_score() {
        return total_score;
    }

    public void setTotal_score(double total_score) {
        this.total_score = total_score;
    }

    public double getAverage_speed() {
        return average_speed;
    }

    public void setAverage_speed(double average_speed) {
        this.average_speed = average_speed;
    }

    public double getTotal_delivery_time() {
        return total_delivery_time;
    }

    public void setTotal_delivery_time(double total_delivery_time) {
        this.total_delivery_time = total_delivery_time;
    }

    public double getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(double total_distance) {
        this.total_distance = total_distance;
    }

    public String getDate_string() {
        return date_string;
    }

    public void setDate_string(String date_string) {
        this.date_string = date_string;
    }

    public boolean isFollowed_route() {
        return followed_route;
    }

    public void setFollowed_route(boolean followed_route) {
        this.followed_route = followed_route;
    }
}
