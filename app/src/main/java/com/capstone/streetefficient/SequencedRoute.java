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
import android.widget.Toast;

import com.capstone.streetefficient.fragments.dialogs.GettingLocationDialog;
import com.capstone.streetefficient.fragments.dialogs.ItineraryDialog;
import com.capstone.streetefficient.fragments.dialogs.NotNextDialog;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.DispatchRider;
import com.capstone.streetefficient.models.RiderPosition;
import com.capstone.streetefficient.models.RouteDetail;
import com.capstone.streetefficient.models.ScoreBreakdown;
import com.capstone.streetefficient.singletons.DriverDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
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

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class SequencedRoute extends AppCompatActivity implements OnMapReadyCallback, ItineraryDialog.ItineraryDialogListener, NotNextDialog.NotNextListener {


    private static final int REQUEST_ARRIVED = 1;
    private static final int REQUEST_ADD_ITEM = 2;

    //GOOGLE MAPS VARIABLES
    private Marker mMarker;
    private GoogleMap mGoogleMap;
    private GeoApiContext mGeoAPi;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    private ClusterManager<ItemLocationMarker> manager;
    private ArrayList<ItemLocationMarker> mClusterMarkers;
    private ArrayList<ArrayList<Polyline>> arrayListOfPolyLines;

    /*ACTIVITY VARIABLES*/
    private boolean isSequenced;
    private double totalDestinationTime;
    private int ARRIVED_INDEX, NEXT_LOCATION, CURRENT_LOCATION;

    private LinearLayout layout;
    private LatLng MOVING_LATLNG;
    private SharedPreferences mprefs;
    private SwitchMaterial switchMaterial;
    private AssignedItemsHelper assignedItemsHelper;
    private SequencedRouteHelper sequencedRouteHelper;
    private ArrayList<Integer> routeTaken, sequencedRoute;
    private ArrayList<String> phoneNumbers;
    private FloatingActionButton fabMain, fabNotNext, fabAddItem;
    private DriverDetails driverDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequenced_route);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        fabMain = findViewById(R.id.fab_main);
        fabAddItem = findViewById(R.id.fab_add);
        fabNotNext = findViewById(R.id.fab_not_next);
        layout = findViewById(R.id.sequenced_progress);
        switchMaterial = findViewById(R.id.sequence_switch);
        switchMaterial.setChecked(true);

        arrayListOfPolyLines = new ArrayList<>();

        mprefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        //isSequenced = false;
        isSequenced = mprefs.getBoolean("isSequenced", false);

        if (isSequenced) {
            layout.setVisibility(View.GONE);

            Gson gson = new Gson();
            assignedItemsHelper = gson.fromJson(mprefs.getString("assignedItemsHelper", null), AssignedItemsHelper.class);
            sequencedRouteHelper = gson.fromJson(mprefs.getString("sequencedRouteHelper", null), SequencedRouteHelper.class);
            driverDetails = gson.fromJson(mprefs.getString("driverDetails", null), DriverDetails.class);

            assignedItemsHelper.setInstance(assignedItemsHelper);
            sequencedRouteHelper.setInstance(sequencedRouteHelper);
            driverDetails.setInstance(driverDetails);
        }
        driverDetails = DriverDetails.getInstance();
        assignedItemsHelper = AssignedItemsHelper.getInstance();
        sequencedRouteHelper = SequencedRouteHelper.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGeoAPi = new GeoApiContext.Builder().apiKey("AIzaSyBix41E07hr6sjO3AkK2AKQewf4tP-SrmE").build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                MOVING_LATLNG = new LatLng(locationResult.getLocations().get(0).getLatitude(), locationResult.getLocations().get(0).getLongitude());
                startDirections(MOVING_LATLNG, assignedItemsHelper.getLatLngAtPosition(NEXT_LOCATION), false);
                mMarker.setPosition(MOVING_LATLNG);
                updatePosition(true);
                sequencedRouteHelper.addGetSpeed(String.valueOf(System.currentTimeMillis()) , MOVING_LATLNG);

                if (!sequencedRouteHelper.isRouteStarted()) {

                    if (sequencedRouteHelper.isInitial()) {
                        fabAddItem.setVisibility(View.GONE);
                        sequencedRouteHelper.setInitial(false);
                        sequencedRouteHelper.setInitialDeliveryTime();
                        new ReadyDialog().show(getSupportFragmentManager(), "Ready");
                    }

                    sequencedRouteHelper.setDestinationTime(System.currentTimeMillis());
                    sequencedRouteHelper.setRIDER_LATLNG(MOVING_LATLNG);
                    sequencedRouteHelper.setRouteStarted(true);
                }

                fabNotNext.setVisibility(View.GONE);
                for (int i = 0; i < sequencedRoute.size(); i++) {

                    if ((PhysicsFunctions.getDistance(assignedItemsHelper.getLatLngAtPosition(sequencedRoute.get(i)), MOVING_LATLNG) * 1000) <= 30
                            && sequencedRoute.get(i) != CURRENT_LOCATION) {

                        totalDestinationTime = System.currentTimeMillis() - sequencedRouteHelper.getDestinationTime();
                        ARRIVED_INDEX = sequencedRoute.get(i);

                        if (sequencedRoute.get(i) != NEXT_LOCATION) {
                            fabNotNext.setVisibility(View.VISIBLE);
                            return;
                        }

                        if (sequencedRoute.get(i) == assignedItemsHelper.indexOfWarehouse()) {

                            intentArrived();
                            return;
                        }

                        intentArrived();
                    }
                }
            }
        };
        phoneNumbers = new ArrayList<>();
        if(sequencedRouteHelper.isInitial())setHistory();
        fabMain.setOnClickListener(fabMainClick);
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) removePolyLines();
        });
        fabNotNext.setOnClickListener(v -> new NotNextDialog().show(getSupportFragmentManager(), "notNextDialog"));
        fabAddItem.setOnClickListener(v -> startActivityForResult(new Intent(SequencedRoute.this, AddItem.class)
                .putExtra("fromSequencedRoute", true), REQUEST_ADD_ITEM));

    }

    private void setHistory() {
        HashMap<String, HashMap<String, LatLng>> hashMapMain = new HashMap<>();
        HashMap<String, LatLng>map = new HashMap<>();
        map.put(String.valueOf(System.currentTimeMillis()), assignedItemsHelper.getLatLngOfWarehouse());
        hashMapMain.put("locations", map);

        FirebaseFirestore.getInstance().collection("Dispatch Riders").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("history").document(Utilities.getSimpleDate(new Date()))
                .set(hashMapMain);
    }

    @Override
    public void startRoute() {
        startTrip();
    }

    @Override
    public void notNext(boolean proceed) {
        if (proceed) intentArrived();
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(10.3157, 123.8854)));
        mGoogleMap.addMarker(new MarkerOptions().position(assignedItemsHelper.getLatLngOfWarehouse()).icon(setMarkerIcon(R.drawable.ic_warehouse)));

        manager = new ClusterManager<>(this, this.mGoogleMap);
        ClusterManagerRenderer mClusterRenderer = new ClusterManagerRenderer(this, this.mGoogleMap, manager);
        manager.setRenderer(mClusterRenderer);

        mGoogleMap.setOnMarkerClickListener(manager);
        manager.setOnClusterItemClickListener(clusterItem -> {
            Item item = assignedItemsHelper.getItemAtPosition(Integer.parseInt(clusterItem.getSnippet()));
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(item);
            bottomSheetDialog.show(getSupportFragmentManager(), "bottom sheet");
            return true;
        });

        routeTaken = sequencedRouteHelper.getRouteTaken();
        sequencedRoute = sequencedRouteHelper.getRouteCheck();
        mClusterMarkers = sequencedRouteHelper.getmClusterMarkers();
        if(sequencedRouteHelper.isInitial()) fabAddItem.setVisibility(View.VISIBLE);


        if (!isSequenced) {
            getSequencedRoute();

            CURRENT_LOCATION = assignedItemsHelper.indexOfWarehouse();
            mMarker = mGoogleMap.addMarker(new MarkerOptions().position(assignedItemsHelper.getLatLngOfWarehouse()).icon(setMarkerIcon(R.drawable.helmet)).zIndex(1));

        } else {
            mMarker = mGoogleMap.addMarker(new MarkerOptions().position(sequencedRouteHelper.getMarker()).icon(setMarkerIcon(R.drawable.helmet)).zIndex(1));
            CURRENT_LOCATION = mprefs.getInt("CURRENT_LOCATION", assignedItemsHelper.indexOfWarehouse());
            if(sequencedRouteHelper.isRouteStarted())startTrip();
            for (ItemLocationMarker marker : mClusterMarkers) {
                manager.addItem(marker);
                manager.cluster();
            }
        }

        googleMap.setOnMapLoadedCallback(() -> {
            layout.setVisibility(View.GONE);
            fabMain.setVisibility(View.VISIBLE);
            switchMaterial.setVisibility(View.VISIBLE);
            zoomRoute(Arrays.asList(assignedItemsHelper.getLatLngs()));
        });

        NEXT_LOCATION = sequencedRoute.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ARRIVED) {
            if (resultCode == RESULT_OK && data != null) {

                CURRENT_LOCATION = data.getIntExtra("current_location", 0);
                sequencedRouteHelper.setRouteStarted(false);
                routeTaken.add(CURRENT_LOCATION);

                sequencedRoute.remove((Integer) CURRENT_LOCATION);
                NEXT_LOCATION = sequencedRoute.get(0);

                System.out.println("NANAY "+mClusterMarkers);
                manager.removeItem(mClusterMarkers.get(CURRENT_LOCATION));
                mClusterMarkers.remove(CURRENT_LOCATION);
                System.out.println("NANAY "+mClusterMarkers);
                manager.cluster();
                startTrip();

                System.out.println("onResult: "+routeTaken);
                System.out.println("onResult: "+sequencedRoute);
            }

        }
        if (requestCode == REQUEST_ADD_ITEM) {
            if (resultCode == RESULT_OK && data != null) {
                removeMarkers();

                ArrayList<LatLng> latLngsResult = (ArrayList<LatLng>) data.getSerializableExtra("latlngs");
                ArrayList<Item> items = (ArrayList<Item>) data.getSerializableExtra("items");
                assignedItemsHelper.addItems(items, latLngsResult);

                CURRENT_LOCATION = (assignedItemsHelper.getLatLngs().length - 1);
                NEXT_LOCATION = sequencedRoute.get(0);

                zoomRoute(Arrays.asList(assignedItemsHelper.getLatLngs()));
                getSequencedRoute();
            }
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(isSequenced)return;
//        startLocationUpdates();
//    }

    @Override
    protected void onStop() {
        super.onStop();


        sequencedRouteHelper.setStopDeliveryTime();
        sequencedRouteHelper.setInitialDeliveryTime();
        sequencedRouteHelper.setmClusterMarkers(mClusterMarkers);
        sequencedRouteHelper.setRiderLatlng(mMarker.getPosition());
        fusedLocationClient.removeLocationUpdates(locationCallback);


        Gson gson = new Gson();
        String assignedItemsHelper = gson.toJson(AssignedItemsHelper.getInstance());
        String sequencedRouteHelper = gson.toJson(SequencedRouteHelper.getInstance());
        String driverDetails = gson.toJson(DriverDetails.getInstance());

        SharedPreferences.Editor prefsEditor = mprefs.edit();
        prefsEditor.putString("sequencedRouteHelper", sequencedRouteHelper);
        prefsEditor.putString("assignedItemsHelper", assignedItemsHelper);
        prefsEditor.putInt("CURRENT_LOCATION", CURRENT_LOCATION);
        prefsEditor.putString("driverDetails", driverDetails);
        prefsEditor.putBoolean("isSequenced", isSequenced);
        prefsEditor.apply();
    }

    private final View.OnClickListener fabMainClick = v -> {
        fabMain.setEnabled(false);
        if (sequencedRouteHelper.isInitial())
            new ItineraryDialog().show(getSupportFragmentManager(), "ItineraryDialog");
        else
            new PopUpDialog(PhysicsFunctions.getDistance(assignedItemsHelper.getLatLngAtPosition(CURRENT_LOCATION),
                    assignedItemsHelper.getLatLngAtPosition(NEXT_LOCATION)), NEXT_LOCATION).show(getSupportFragmentManager(), "popup");
        fabMain.setEnabled(true);
    };

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

    private void intentArrived() {

        DocumentReference routeRef = FirebaseFirestore.getInstance().collection("Route_Detail").document();
        double distance = PhysicsFunctions.getDistance(sequencedRouteHelper.getRIDER_LATLNG(), MOVING_LATLNG);
        double est = PhysicsFunctions.getEST(distance);
        double speed = PhysicsFunctions.getSpeed(distance, totalDestinationTime);
        double scoreTime = PhysicsFunctions.getPercentError(distance, totalDestinationTime, est);
        double scoreSpeed = PhysicsFunctions.getPercentError(distance, speed, 60);
        double totalScore = PhysicsFunctions.getTotalScore(scoreSpeed, scoreTime);

        sequencedRouteHelper.addRouteDetail(new RouteDetail(new Date(), routeRef.getId(), sequencedRouteHelper.getRouteHeaderID(), Utilities.getItemID(CURRENT_LOCATION),
                Utilities.getItemID(ARRIVED_INDEX), totalScore, speed, est, totalDestinationTime, distance));

        sequencedRouteHelper.addScoreBreakdown(new ScoreBreakdown(totalDestinationTime,
                Utilities.getAddress(ARRIVED_INDEX), Utilities.roundOff(distance) + "KM", est,
                Utilities.getAddress(CURRENT_LOCATION), routeRef.getId(), scoreSpeed, scoreTime, speed, totalScore));


        Intent intent;
        if (ARRIVED_INDEX != assignedItemsHelper.indexOfWarehouse()) {
            sendMessageArrive();
            intent = new Intent(SequencedRoute.this, ArrivedLocation.class);
            intent.putExtra("destination", ARRIVED_INDEX);
            startActivityForResult(intent, REQUEST_ARRIVED);
            return;

        }

        GettingLocationDialog dialog = new GettingLocationDialog();
        dialog.show(getSupportFragmentManager(), "showDialog");
        sequencedRouteHelper.setStopDeliveryTime();
        routeTaken.add(ARRIVED_INDEX);
        updatePosition(false);

        sequencedRoute.remove((Integer) ARRIVED_INDEX);



        System.out.println("intentArrived: "+routeTaken);
        System.out.println("intentArrived: "+sequencedRoute);

        FirebaseFirestore.getInstance().collection("Route_Detail").document(routeRef.getId()).set(sequencedRouteHelper.getRouteDetails().get(sequencedRouteHelper.getRouteDetails().size() - 1))
                .addOnSuccessListener(unused -> {

                    startActivity(new Intent(this, FinishedRoute.class));
                    dialog.dismiss();
                    finish();

                });

    }


    private void getSequencedRoute() {

        sendNotif();
        TspAlgorithm tspAlgorithm = new TspAlgorithm(assignedItemsHelper.indexOfWarehouse(), PhysicsFunctions.generateDistanceMatrix(assignedItemsHelper.getLatLngs()));
        sequencedRoute = tspAlgorithm.getSequence();



        for (int i = 0; i < assignedItemsHelper.getLatLngs().length - 1; i++) {
            int indexSequencedRoute = sequencedRoute.indexOf(i);
            ItemLocationMarker marker = new ItemLocationMarker(assignedItemsHelper.getLatLngAtPosition(i), indexSequencedRoute, i);
            mClusterMarkers.add(marker);
            manager.addItem(marker);
            manager.cluster();
        }

        sequencedRoute.remove(0);
        sequencedRouteHelper.setRouteCheck(sequencedRoute);
        sequencedRouteHelper.setSequencedRoute(sequencedRoute);

        System.out.println("METHOD "+sequencedRouteHelper.getSequencedRoute());
        System.out.println(sequencedRouteHelper.getRouteCheck());

        sequencedRouteHelper.setmClusterMarkers(mClusterMarkers);
        isSequenced = true;


        for (int i = 0; i < sequencedRoute.size() - 1; i++)
            startDirections(assignedItemsHelper.getLatLngAtPosition(sequencedRoute.get(i)), assignedItemsHelper.getLatLngAtPosition(sequencedRoute.get(i + 1)), true);
    }

    private void removeMarkers() {
        manager.clearItems();
        manager.cluster();
        mClusterMarkers.clear();
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

    private void sendNotif() {
        SmsManager smsManager = SmsManager.getDefault();
        for (Item item : assignedItemsHelper.getAllItems()) {
            if(phoneNumbers.contains(item.getItemRecipientContactNumber()))continue;
            smsManager.sendTextMessage(item.getItemRecipientContactNumber(),
                    null,
                    "WE ARE GOING TO DELIVER YOUR PACKAGE TODAY",
                    null,
                    null);
            phoneNumbers.add(item.getItemRecipientContactNumber());
        }
    }

    private void sendMessageArrive() {
        SmsManager smsManager = SmsManager.getDefault();
        Item item = assignedItemsHelper.getItemAtPosition(ARRIVED_INDEX);
        smsManager.sendTextMessage(item.getItemRecipientContactNumber(),
                null,
                "Good Day I have arrived at your location",
                null,
                null);
    }

    private void startTrip() {
        removePolyLines();
        startLocationUpdates();
        startDirections(assignedItemsHelper.getLatLngAtPosition(CURRENT_LOCATION), assignedItemsHelper.getLatLngAtPosition(NEXT_LOCATION), false);
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

    private void startDirections(LatLng first, LatLng second, boolean zoomAll) {
        if (!switchMaterial.isChecked()) return;


        com.google.maps.model.LatLng Origin = new com.google.maps.model.LatLng(first.latitude, first.longitude);
        com.google.maps.model.LatLng Destination = new com.google.maps.model.LatLng(second.latitude, second.longitude);

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
                        Polyline polyline = mGoogleMap.addPolyline(po);
                        po.color(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)).width(10);
                        Polyline polyline2 = mGoogleMap.addPolyline(po);
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

    private void updatePosition(boolean status) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        DispatchRider dispatchRider =driverDetails.getDispatchRider();
        if(dispatchRider == null){
            FirebaseFirestore.getInstance().collection("Dispatch Riders").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnSuccessListener(documentSnapshot -> {
                        driverDetails.setDispatchRider(documentSnapshot.toObject(DispatchRider.class));
            });
            return;
        }


        FirebaseFirestore.getInstance().collection("DispatchRiders_Position").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(new RiderPosition(
                        dispatchRider.getCourier_id(),
                        MOVING_LATLNG.latitude,
                        MOVING_LATLNG.longitude,
                        dispatchRider.getContactNumber(),
                        dispatchRider.getId(),
                        dispatchRider.getFname() + " " + dispatchRider.getLname(),
                        dispatchRider.getVehicle_type(),
                        status,
                        Utilities.getAddress(NEXT_LOCATION),
                        dispatchRider.getBranch())
                );
        String nested = "locations." + System.currentTimeMillis();


//        FirebaseFirestore.getInstance().collection("Dispatch Riders")
//                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("history")
//                .document(Utilities.getSimpleDate(new Date()))
//                .update(nested, MOVING_LATLNG);

    }

    public void zoomRoute(List<LatLng> latLngs) {

        if (mGoogleMap == null || latLngs == null || latLngs.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs)
            boundsBuilder.include(latLng);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

}