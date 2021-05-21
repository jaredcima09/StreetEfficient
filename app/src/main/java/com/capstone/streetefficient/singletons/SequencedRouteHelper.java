package com.capstone.streetefficient.singletons;

import com.capstone.streetefficient.models.ItemLocationMarker;
import com.capstone.streetefficient.models.RouteDetail;
import com.capstone.streetefficient.models.ScoreBreakdown;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SequencedRouteHelper {

    private static SequencedRouteHelper instance;

    private final ArrayList<ScoreBreakdown> scoreBreakdowns = new ArrayList<>();
    private ArrayList<ItemLocationMarker> mClusterMarkers = new ArrayList<>();
    private ArrayList<RouteDetail> routeDetails = new ArrayList<>();
    private ArrayList<Integer> sequencedRoute = new ArrayList<>();
    private ArrayList<Integer> routeTaken = new ArrayList<>();
    private ArrayList<Integer> routeCheck = new ArrayList<>();
    private final HashMap<String, LatLng> getSpeed = new HashMap<>();

    private boolean routeStarted = false;
    private boolean isInitial = true;

    private double initialDeliveryTime = 0;
    private double stopDeliveryTime;
    private double destinationTime;

    private String routeHeaderID = "";
    private LatLng RIDER_LATLNG;
    private LatLng marker;
    private Date date;

    private SequencedRouteHelper() {

    }

    public static SequencedRouteHelper getInstance() {
        if (instance == null) {
            instance = new SequencedRouteHelper();
        }
        return instance;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        if(date == null) return new Date();
        return date;
    }

    public HashMap<String, LatLng> getGetSpeed() {
        return getSpeed;
    }

    public void addGetSpeed(String time, LatLng latLng) {
        getSpeed.put(time, latLng);
    }

    public double getInitialDeliveryTime() {
        return initialDeliveryTime;
    }

    public void setStopDeliveryTime() {
        this.stopDeliveryTime = System.currentTimeMillis();
    }

    public double getStopDeliveryTime() {
        return stopDeliveryTime;
    }

    public void setRiderLatlng(LatLng mMarker) {
        marker = mMarker;
    }

    public LatLng getMarker() {
        return marker;
    }

    public void setRIDER_LATLNG(LatLng RIDER_LATLNG) {
        this.RIDER_LATLNG = RIDER_LATLNG;
    }

    public LatLng getRIDER_LATLNG() {
        return RIDER_LATLNG;
    }

    public boolean isRouteStarted() {
        return routeStarted;
    }

    public void setRouteStarted(boolean routeStarted) {
        this.routeStarted = routeStarted;
    }

    public void setDestinationTime(double destinationTime) {
        this.destinationTime = destinationTime;
    }

    public double getDestinationTime() {
        return destinationTime;
    }

    public double getTotalDeliveryTime() {
        return stopDeliveryTime - initialDeliveryTime;
    }

    public void setInitialDeliveryTime() {
        if (initialDeliveryTime == 0)
            initialDeliveryTime = System.currentTimeMillis();
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public String getRouteHeaderID() {
        if (routeHeaderID.isEmpty())
            routeHeaderID = FirebaseFirestore.getInstance().collection("Route_Header").document().getId();
        return routeHeaderID;
    }

    public void setRouteCheck(ArrayList<Integer> fromActivity) {
        routeCheck = fromActivity;
    }

    public ArrayList<Integer> getRouteCheck() {
        return routeCheck;
    }

    public ArrayList<ItemLocationMarker> getmClusterMarkers() {
        return mClusterMarkers;
    }

    public void setmClusterMarkers(ArrayList<ItemLocationMarker> mClusterMarkers) {
        this.mClusterMarkers = mClusterMarkers;
    }

    public ArrayList<Integer> getSequencedRoute() {
        return sequencedRoute;
    }

    public void setSequencedRoute(ArrayList<Integer> sequencedRoute) {
        this.sequencedRoute = new ArrayList<>(sequencedRoute);
    }

    public void setInstance(SequencedRouteHelper instance) {
        SequencedRouteHelper.instance = instance;
    }

    public ArrayList<RouteDetail> getRouteDetails() {
        return routeDetails;
    }

    public void setRouteDetails(ArrayList<RouteDetail> routeDetails) {
        this.routeDetails = routeDetails;
    }

    public void addRouteDetail(RouteDetail routeDetail) {
        routeDetails.add(routeDetail);
    }

    public ArrayList<Integer> getRouteTaken() {
        return routeTaken;
    }

    public void setRouteTaken(ArrayList<Integer> routeTaken) {
        this.routeTaken = routeTaken;
    }

    public void addScoreBreakdown(ScoreBreakdown scoreBreakdown) {
        scoreBreakdowns.add(scoreBreakdown);
    }

    public ArrayList<ScoreBreakdown> getScoreBreakdown() {
        return scoreBreakdowns;
    }

    public void reset() {
        instance = null;
    }

}
