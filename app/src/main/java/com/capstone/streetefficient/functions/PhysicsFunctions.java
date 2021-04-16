package com.capstone.streetefficient.functions;

import android.util.Log;

import com.capstone.streetefficient.models.RouteDetail;
import com.capstone.streetefficient.models.RouteHeader;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

public class PhysicsFunctions {

    public static double getPercentError(double distance, double actualValue, double expectedValue) {
        if ((distance * 1000) < 30) return 100;
        if (actualValue < expectedValue) return 100;
        double percentErrorScore = (((actualValue - expectedValue) / expectedValue) * 100);
        if (percentErrorScore > 100) return 0;
        return 100 - percentErrorScore;
    }

    public static double getTotalScore(double scoreSpeed, double scoreTime) {
        return ((scoreSpeed / 100) * 50) + ((scoreTime / 100) * 50);
    }

    public static double getEST(double distance) {
        return (distance / 40) * 3600000;
    }

    public static double getSpeed(double distance, double time) {
        if (time == 0 && distance == 0) return 0;
        return distance / (time / 3600000);
    }

    public static String decimalToHour(double tripTime) {
        tripTime /= 3600000;
        int hours = (int) tripTime;
        double minutesDec = ((tripTime - hours) * 60);
        int minutes = (int) minutesDec;
        if ((int) ((minutesDec - minutes) * 60) > 30) minutes++;
        if (hours < 1) return " " + minutes + " minute(s)";
        else return " " + hours + " hour(s) and" + minutes + " minute(s)";
    }

//    public static RouteHeader getRouteHeader(String routeHeaderID, double totalTime, ArrayList<RouteDetail> routeDetails) {
//        double averageSpeed = 0;
//        double totalDistance = 0;
//        double averageTotalScore = 0;
//        Log.d("routeDetails ",""+routeDetails.size());
//        for (RouteDetail routeDetail : routeDetails) {
//            Log.d("averageSpeed ",""+routeDetail.getSpeed());
//            averageSpeed += routeDetail.getSpeed();
//            totalDistance += routeDetail.getDistance();
//            averageTotalScore += routeDetail.getScore();
//        }
//        averageTotalScore /= routeDetails.size();
//        averageSpeed /= routeDetails.size();
//
//    }

    public static double getDistance(LatLng latlng1, LatLng latlng2) {
        double lat1 = latlng1.latitude;
        double lon1 = latlng1.longitude;

        double lat2 = latlng2.latitude;
        double lon2 = latlng2.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double rad = 6371;
        return rad * c;
    }
}
