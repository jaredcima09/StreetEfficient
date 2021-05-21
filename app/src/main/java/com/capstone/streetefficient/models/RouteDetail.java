package com.capstone.streetefficient.models;

import java.io.Serializable;
import java.util.Date;

public class RouteDetail implements Serializable {

    private Date date;
    private String route_detail_id;
    private String route_header_id;
    private String origin_item_number;
    private String destination_item_number;
    private double score;
    private double speed;
    private double distance;
    private double est_duration;
    private double act_duration;

    public RouteDetail() {

    }

    public RouteDetail(Date date, String route_detail_id, String route_header_id, String origin_item_number,
                       String destination_item_number, double score, double speed, double est_duration, double act_duration, double distance) {

        this.date = date;
        this.score = score;
        this.speed = speed;
        this.distance = distance;
        this.est_duration = est_duration;
        this.act_duration = act_duration;
        this.route_detail_id = route_detail_id;
        this.route_header_id = route_header_id;
        this.origin_item_number = origin_item_number;
        this.destination_item_number = destination_item_number;

    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getRoute_detail_id() {
        return route_detail_id;
    }

    public void setRoute_detail_id(String route_detail_id) {
        this.route_detail_id = route_detail_id;
    }

    public String getRoute_header_id() {
        return route_header_id;
    }

    public void setRoute_header_id(String route_header_id) {
        this.route_header_id = route_header_id;
    }

    public String getOrigin_item_number() {
        return origin_item_number;
    }

    public void setOrigin_item_number(String origin_item_number) {
        this.origin_item_number = origin_item_number;
    }

    public String getDestination_item_number() {
        return destination_item_number;
    }

    public void setDestination_item_number(String destination_item_number) {
        this.destination_item_number = destination_item_number;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getEst_duration() {
        return est_duration;
    }

    public void setEst_duration(double est_duration) {
        this.est_duration = est_duration;
    }

    public double getAct_duration() {
        return act_duration;
    }

    public void setAct_duration(double act_duration) {
        this.act_duration = act_duration;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
