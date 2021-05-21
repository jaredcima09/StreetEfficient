package com.capstone.streetefficient.models;

import java.io.Serializable;

public class ScoreBreakdown implements Serializable {
    private String destination, distanceTravelled, origin, routeID;
    private double speed, scoreTime, scoreSpeed, actualTime, totalScore, estimatedTime;

    public ScoreBreakdown(double actualTime, String destination, String distanceTravelled, double estimatedTime, String origin,
                             String routeID, double scoreSpeed, double scoreTime, double speed, double totalScore) {
        this.speed = speed;
        this.origin = origin;
        this.routeID = routeID;
        this.scoreTime = scoreTime;
        this.totalScore = totalScore;
        this.scoreSpeed = scoreSpeed;
        this.actualTime = actualTime;
        this.destination = destination;
        this.estimatedTime = estimatedTime;
        this.distanceTravelled = distanceTravelled;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(String distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getScoreTime() {
        return scoreTime;
    }

    public void setScoreTime(double scoreTime) {
        this.scoreTime = scoreTime;
    }

    public double getScoreSpeed() {
        return scoreSpeed;
    }

    public void setScoreSpeed(double scoreSpeed) {
        this.scoreSpeed = scoreSpeed;
    }

    public double getActualTime() {
        return actualTime;
    }

    public void setActualTime(double actualTime) {
        this.actualTime = actualTime;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
