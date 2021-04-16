package com.capstone.streetefficient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.Manifest;
import android.os.Looper;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.graphics.Color;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.telephony.SmsManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;

import com.capstone.streetefficient.fragments.BreakdownFragment;
import com.capstone.streetefficient.fragments.dialogs.ItineraryDialog;
import com.capstone.streetefficient.fragments.dialogs.NotNextDialog;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.RouteDetail;
import com.google.gson.Gson;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.model.DirectionsRoute;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.model.DirectionsResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.internal.PolylineEncoding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.maps.android.clustering.ClusterManager;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.functions.TspAlgorithm;
import com.capstone.streetefficient.models.ItemLocationMarker;
import com.capstone.streetefficient.fragments.dialogs.PopUpDialog;
import com.capstone.streetefficient.fragments.dialogs.ReadyDialog;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.SequencedRouteHelper;
import com.capstone.streetefficient.functions.ClusterManagerRenderer;
import com.capstone.streetefficient.fragments.dialogs.BottomSheetDialog;
import com.google.maps.model.TravelMode;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;


public class SequencedRoute extends AppCompatActivity implements OnMapReadyCallback, ItineraryDialog.ItineraryDialogListener, NotNextDialog.NotNextListener {


    private static final int REQUEST_ARRIVED = 1;

    //GOOGLE MAPS VARIABLES
    private Marker mMarker;
    private GoogleMap googleMap;
    private GeoApiContext mGeoAPi;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    private ArrayList<ArrayList<Polyline>> arrayListOfPolyLines;
    private ClusterManager<ItemLocationMarker> manager;
    private ArrayList<ItemLocationMarker> mClusterMarkers;


    /*ACTIVITY VARIABLES*/
    private double startDeliveryTime;
    private double startDestinationTime;
    private double totalDestinationTime;

    private int ARRIVED_INDEX;
    private int NEXT_LOCATION;
    private int CURRENT_LOCATION;


    private boolean isInitial = true;
    private boolean routeStarted = false;
    private boolean isSequenced;

    private LatLng RIDER_LATLNG, MOVING_LATLNG;

    private LatLng[] latLngs;
    private ArrayList<Integer> routeTaken;
    private ArrayList<Integer> sequencedRoute;

    private SharedPreferences mprefs;
    private DocumentReference routeHeaderRef;
    private LinearLayout layout;
    private AssignedItemsHelper assignedItemsHelper;
    private SequencedRouteHelper sequencedRouteHelper;


    private String routeHeaderID;

    FloatingActionButton fabMain, fabNotNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequenced_route);

        fabMain = findViewById(R.id.fab_main);
        fabNotNext = findViewById(R.id.fab_not_next);
        layout = findViewById(R.id.sequenced_progress);


        arrayListOfPolyLines = new ArrayList<>();

        //routeHeaderRef = FirebaseFirestore.getInstance().collection("Route_Header").document();


        mprefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        isSequenced = false;
        //isSequenced = mprefs.getBoolean("isSequenced", false);


        if (isSequenced) {
            Gson gson = new Gson();
            sequencedRouteHelper = gson.fromJson(mprefs.getString("sequencedRouteHelper", null), SequencedRouteHelper.class);
            sequencedRouteHelper.setInstance(sequencedRouteHelper);
        }

        assignedItemsHelper = AssignedItemsHelper.getInstance();
        sequencedRouteHelper = SequencedRouteHelper.getInstance();

        latLngs = assignedItemsHelper.getLatLngs();
        routeHeaderID = sequencedRouteHelper.getRouteHeaderID();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mGeoAPi = new GeoApiContext.Builder()
                .apiKey("AIzaSyBix41E07hr6sjO3AkK2AKQewf4tP-SrmE")
                .build();


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                MOVING_LATLNG = new LatLng(locationResult.getLocations().get(0).getLatitude(), locationResult.getLocations().get(0).getLongitude());
                mMarker.setPosition(MOVING_LATLNG);
                //startDirections(MOVING_LATLNG, NEXT_LOCATION, false);

                if (!routeStarted) {

                    if (isInitial) {
                        isInitial = false;
                        startDeliveryTime = System.currentTimeMillis();
                        new ReadyDialog().show(getSupportFragmentManager(), "Ready");
                    }

                    startDestinationTime = System.currentTimeMillis();
                    RIDER_LATLNG = MOVING_LATLNG;
                    routeStarted = true;
                }

                fabNotNext.setVisibility(View.GONE);
                for (int i = 0; i < sequencedRoute.size(); i++) {

                    if ((haversineFormula(latLngs[sequencedRoute.get(i)], MOVING_LATLNG) * 1000) <= 30
                            && sequencedRoute.get(i) != CURRENT_LOCATION) {

                        totalDestinationTime = System.currentTimeMillis() - startDestinationTime;
                        ARRIVED_INDEX = sequencedRoute.get(i);


                        if (sequencedRoute.get(i) != NEXT_LOCATION) {
                            fabNotNext.setVisibility(View.VISIBLE);
                            return;
                        }

                        if (sequencedRoute.get(i) == latLngs.length - 1) {
                            intentArrived();
                            return;
                        }

                        intentArrived();
                    }
                }


            }
        };

        fabNotNext.setOnClickListener(v -> new NotNextDialog().show(getSupportFragmentManager(), "notNextDialog"));
        fabMain.setOnClickListener(fabMainClick);
    }


//    private void intentFinished() {
//        DocumentReference routeRef = FirebaseFirestore.getInstance().collection("Route_Detail").document();
//
//        double delivery_time = System.currentTimeMillis() - elapsedTime;
//        double distance = haversineFormula(RIDER_LATLNG, MOVING_LATLNG);
//        double est = PhysicsFunctions.getEST(distance);
//        double speed = PhysicsFunctions.getSpeed(distance, totalTripTime);
//        double scoreTime = PhysicsFunctions.getPercentError(distance, totalTripTime, est);
//        double scoreSpeed = PhysicsFunctions.getPercentError(distance, speed, 60);
//        double totalScore = PhysicsFunctions.getTotalScore(scoreSpeed, scoreTime);
//
//        RouteDetail routeDetail = new RouteDetail(new Date(), routeRef.getId(), routeHeaderRef.getId(), assignedItemsHelper.getItemAtPosition(CURRENT_LOCATION).getItem_id(), "BRANCH WAREHOUSE", totalScore, speed, est, totalTripTime, distance);
//        sequencedRouteHelper.addRouteDetail(routeDetail);
//
//        RouteHeader routeHeader = PhysicsFunctions.getRouteHeader(routeHeaderRef.getId(), delivery_time, sequencedRouteHelper.getRouteDetails());
//
//        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
//        writeBatch.set(routeHeaderRef, routeHeader);
//        writeBatch.set(routeRef, routeDetail);
//        writeBatch.commit().addOnSuccessListener(aVoid -> {
//
//            //sequencedRouteHelper.addBreakdownFragment(new BreakdownFragment(totalTripTime, Utilities.getAddress(ARRIVED_INDEX), Utilities.roundOff(distance) + "KM", est, Utilities.getAddress(CURRENT_LOCATION), routeRef.getId(), scoreSpeed, scoreTime, (speed), totalScore));
//            routeTaken.add(CURRENT_LOCATION);
//            routeTaken.add(NEXT_LOCATION);
//
//            Intent intent = new Intent(SequencedRoute.this, FinishedRoute.class);
//            intent.putExtra("routeHeader", routeHeader);
//            intent.putExtra("routeTaken", routeTaken);
//            startActivity(intent);
//            finish();
//
//        });
//    }

    private void intentArrived() {
        DocumentReference routeRef = FirebaseFirestore.getInstance().collection("Route_Detail").document();

        double distance = haversineFormula(RIDER_LATLNG, MOVING_LATLNG);
        double est = PhysicsFunctions.getEST(distance);
        double speed = PhysicsFunctions.getSpeed(distance, totalDestinationTime);
        double scoreTime = PhysicsFunctions.getPercentError(distance, totalDestinationTime, est);
        double scoreSpeed = PhysicsFunctions.getPercentError(distance, speed, 60);
        double totalScore = PhysicsFunctions.getTotalScore(scoreSpeed, scoreTime);

        sequencedRouteHelper.addRouteDetail(new RouteDetail(new Date(), routeRef.getId(), routeHeaderID, Utilities.getItemID(CURRENT_LOCATION),
                Utilities.getItemID(ARRIVED_INDEX), totalScore, speed, est, totalDestinationTime, distance));

        sequencedRouteHelper.addBreakdownFragment(new BreakdownFragment(totalDestinationTime, Utilities.getAddress(ARRIVED_INDEX), Utilities.roundOff(distance) + "KM", est,
                Utilities.getAddress(CURRENT_LOCATION), routeRef.getId(), scoreSpeed, scoreTime, speed, totalScore));


        Intent intent;
        if (ARRIVED_INDEX != latLngs.length - 1) {

            intent = new Intent(SequencedRoute.this, ArrivedLocation.class);
            intent.putExtra("destination", ARRIVED_INDEX);
            startActivityForResult(intent, REQUEST_ARRIVED);
            return;

        }


        routeTaken.add(CURRENT_LOCATION);
        routeTaken.add(ARRIVED_INDEX);

        sequencedRoute.remove((Integer) ARRIVED_INDEX);
        sequencedRoute.remove((Integer) CURRENT_LOCATION);

        System.out.println("NANAY ROUTE TAKEN: " + routeTaken);
        System.out.println("NANAY ROUTE CHECK: " + sequencedRoute);
        sequencedRouteHelper.setTotalDeliveryTime(startDeliveryTime);
        startActivity(new Intent(this, FinishedRoute.class));
        finish();
    }

    private void startTrip() {
        removePolyLines();
        routeStarted = false;
        startLocationUpdates();
        //startDirections(latLngs[CURRENT_LOCATION], NEXT_LOCATION, false);
    }

    private final View.OnClickListener fabMainClick = v -> {
        fabMain.setEnabled(false);
        if (isInitial) new ItineraryDialog().show(getSupportFragmentManager(), "ItineraryDialog");
        else
            new PopUpDialog(haversineFormula(latLngs[CURRENT_LOCATION], latLngs[NEXT_LOCATION]), NEXT_LOCATION).show(getSupportFragmentManager(), "popup");
        fabMain.setEnabled(true);
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMarker = googleMap.addMarker(new MarkerOptions().position(latLngs[latLngs.length - 1]).icon(setMarkerIcon(R.drawable.helmet)).zIndex(1));
        this.googleMap.addMarker(new MarkerOptions().position(latLngs[latLngs.length - 1]).icon(setMarkerIcon(R.drawable.ic_warehouse)));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(mMarker.getPosition()));
        manager = new ClusterManager<>(this, this.googleMap);
        ClusterManagerRenderer mClusterRenderer = new ClusterManagerRenderer(this, this.googleMap, manager);
        manager.setRenderer(mClusterRenderer);

        this.googleMap.setOnMarkerClickListener(manager);
        manager.setOnClusterItemClickListener(clusterItem -> {
            Item item = assignedItemsHelper.getItemAtPosition(Integer.parseInt(clusterItem.getSnippet()));
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(item);
            bottomSheetDialog.show(getSupportFragmentManager(), "bottom sheet");
            return true;
        });

        routeTaken = sequencedRouteHelper.getRouteTaken();
        sequencedRoute = sequencedRouteHelper.getRouteCheck();
        mClusterMarkers = sequencedRouteHelper.getmClusterMarkers();


        if (!isSequenced) getSequencedRoute();
        else {
            for (ItemLocationMarker marker : mClusterMarkers) {
                manager.addItem(marker);
                manager.cluster();
            }
        }


        CURRENT_LOCATION = mprefs.getInt("CURRENT_SEQUENCE_INDEX", latLngs.length - 1);
        NEXT_LOCATION = sequencedRoute.get(0);

        System.out.println("NANAY ROUTE TAKEN: " + routeTaken);
        System.out.println("NANAY ROUTE CHECK: " + sequencedRoute);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ARRIVED) {
            if (resultCode == RESULT_OK && data != null) {


                routeTaken.add(CURRENT_LOCATION);
                CURRENT_LOCATION = data.getIntExtra("current_location", 0);
                sequencedRoute.remove((Integer) CURRENT_LOCATION);

                NEXT_LOCATION = sequencedRoute.get(0);

                manager.removeItem(mClusterMarkers.get(CURRENT_LOCATION));
                manager.cluster();

                startTrip();

                System.out.println("NANAY ROUTE TAKEN: " + routeTaken);
                System.out.println("NANAY ROUTE CHECK: " + sequencedRoute);
                //zoomRoute(new ArrayList<>(Arrays.asList(CURRENT_LOCATION, NEXT_LOCATION)));
            } //else if (resultCode == RESULT_CANCELED) intentArrived();

        }


    }

    private void sendNotif() {
        SmsManager smsManager = SmsManager.getDefault();
        for (Item item : assignedItemsHelper.getAllItems()) {
            smsManager.sendTextMessage(item.getItemRecipientContactNumber(),
                    null,
                    "WE ARE GOING TO DELIVER YOUR PACKAGE TODAY",
                    null,
                    null);
        }
    }

//    private LatLng getBounds(LatLng[] LatLngs) {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (int i = 0; i < latLngs.length; i++) {
//            builder.include(LatLngs[i]);
//        }
//        return builder.build().getCenter();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);

        SharedPreferences.Editor prefsEditor = mprefs.edit();
        Gson gson = new Gson();
        //sequencedRouteHelper.setTotalDeliveryTime(totalDeliveryTime);
        String assignedItemsHelper = gson.toJson(AssignedItemsHelper.getInstance());
        //String sequencedItemsHelper = gson.toJson(SequencedRouteHelper.getInstance());
        prefsEditor.putString("assignedItemsHelper", assignedItemsHelper);
        //prefsEditor.putString("sequencedItemsHelper", sequencedItemsHelper);
        prefsEditor.apply();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setSmallestDisplacement(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

    }

    private void startDirections(LatLng first, int second, boolean zoomAll) {
        //LatLng currentIndex = latLngs[first];
        LatLng nextIndex = latLngs[second];

        com.google.maps.model.LatLng Origin = new com.google.maps.model.LatLng(first.latitude, first.longitude);
        com.google.maps.model.LatLng Destination = new com.google.maps.model.LatLng(nextIndex.latitude, nextIndex.longitude);

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoAPi);
        directions.mode(TravelMode.DRIVING);
        directions.alternatives(false);
        directions.origin(Origin);
        directions.destination(Destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                new Handler(Looper.getMainLooper()).post(() -> {


                    ArrayList<Polyline> mPolyLines = new ArrayList<>();
                    for (DirectionsRoute route : result.routes) {
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                        List<LatLng> newDecodedPath = new ArrayList<>();

                        for (com.google.maps.model.LatLng latLng : decodedPath) {
                            newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
                        }
                        PolylineOptions po = new PolylineOptions().addAll(newDecodedPath);
                        po.color(Color.BLACK).width(16);
                        Polyline polyline = googleMap.addPolyline(po);
                        po.color(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)).width(10);
                        Polyline polyline2 = googleMap.addPolyline(po);
                        mPolyLines.add(polyline);
                        mPolyLines.add(polyline2);
                        if (!zoomAll) {
                            removePolyLines();
                            zoomRoute(po.getPoints());
                        }
                    }

                    arrayListOfPolyLines.add(mPolyLines);

                });
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d(SequencedRoute.this.toString(), e.getLocalizedMessage());
            }
        });


    }

    public void zoomRoute(List<LatLng> latLngs) {

        if (googleMap == null || latLngs == null || latLngs.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs)
            boundsBuilder.include(latLng);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }


    private void removePolyLines() {
        if (arrayListOfPolyLines.isEmpty()) return;

        for (ArrayList<Polyline> polylines : arrayListOfPolyLines) {
            for (Polyline polyline : polylines) {
                polyline.remove();
            }
        }
        arrayListOfPolyLines.clear();
    }

    private void getSequencedRoute() {

        double[][] distanceMatrix = new double[latLngs.length][latLngs.length];

        for (int i = 0; i < latLngs.length; i++) {
            for (int j = 0; j < latLngs.length; j++) {
                if (distanceMatrix[i][j] == 0) {
                    if (i == j) {
                        continue;
                    }
                    double ans = haversineFormula(latLngs[i], latLngs[j]);
                    distanceMatrix[i][j] = ans;
                    distanceMatrix[j][i] = ans;

                }
            }
        }

        TspAlgorithm tspAlgorithm = new TspAlgorithm(latLngs.length - 1, distanceMatrix);
        sequencedRoute = tspAlgorithm.getSequence();
        sequencedRouteHelper.setSequencedRoute(sequencedRoute);
        layout.setVisibility(View.GONE);
        isSequenced = true;
        sendNotif();
        for (int i = 0; i < latLngs.length - 1; i++) {
            int indexSequencedRoute = sequencedRoute.indexOf(i);
            ItemLocationMarker marker = new ItemLocationMarker(latLngs[i], indexSequencedRoute, i);
            mClusterMarkers.add(marker);
            manager.addItem(marker);
            manager.cluster();
        }
        sequencedRoute.remove(0);
        googleMap.setOnMapLoadedCallback(() -> {

            zoomRoute(Arrays.asList(assignedItemsHelper.getLatLngs()));
//            for (int i = 0; i < sequencedRoute.size() - 1; i++) {
//                //startDirections(latLngs[sequencedRoute.get(i)], sequencedRoute.get(i + 1), true);
//            }
        });


    }

    private static double haversineFormula(LatLng latlng1, LatLng latlng2) {
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

    private BitmapDescriptor setMarkerIcon(int drawable) {
        IconGenerator iconGenerator = new IconGenerator(this);
        int markerWidth = (int) getResources().getDimension(R.dimen.custom_marker_image);
        int markerHeight = (int) getResources().getDimension(R.dimen.custom_marker_image);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        imageView.setImageResource(drawable);
        iconGenerator.setContentView(imageView);
        iconGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = iconGenerator.makeIcon();
        return BitmapDescriptorFactory.fromBitmap(icon);
    }

    @Override
    public void startRoute() {
        startTrip();
    }

    @Override
    public void notNext(boolean proceed) {
        if (proceed) intentArrived();
    }


}