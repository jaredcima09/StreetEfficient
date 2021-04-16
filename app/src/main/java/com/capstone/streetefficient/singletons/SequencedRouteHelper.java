package com.capstone.streetefficient.singletons;

import com.capstone.streetefficient.fragments.BreakdownFragment;
import com.capstone.streetefficient.models.ItemLocationMarker;
import com.capstone.streetefficient.models.RouteDetail;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SequencedRouteHelper {


    private static SequencedRouteHelper instance;
    private ArrayList<Integer> routeTaken = new ArrayList<>();
    private ArrayList<Integer> sequencedRoute = new ArrayList<>();
    private ArrayList<Integer> routeCheck = new ArrayList<>();
    private ArrayList<RouteDetail> routeDetails = new ArrayList<>();
    private final ArrayList<BreakdownFragment> breakdownFragments = new ArrayList<>();
    private ArrayList<ItemLocationMarker> mClusterMarkers = new ArrayList<>();
    private String routeHeaderID = "";
    private double totalDeliveryTime;

    private SequencedRouteHelper() {

    }

    public double getTotalDeliveryTime() {
        return totalDeliveryTime;
    }

    public void setTotalDeliveryTime(double fromActivityTimeStarted) {
        totalDeliveryTime = System.currentTimeMillis() - fromActivityTimeStarted;
    }

    public String getRouteHeaderID() {
        if (routeHeaderID.isEmpty())
            routeHeaderID = FirebaseFirestore.getInstance().collection("Route_Header").document().getId();

        return routeHeaderID;
    }

    public static SequencedRouteHelper getInstance() {
        if (instance == null) {
            instance = new SequencedRouteHelper();
        }
        return instance;
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

    public void addBreakdownFragment(BreakdownFragment breakdownFragment) {
        breakdownFragments.add(breakdownFragment);
    }

    public ArrayList<BreakdownFragment> getBreakdownFragments() {
        return breakdownFragments;
    }
}
