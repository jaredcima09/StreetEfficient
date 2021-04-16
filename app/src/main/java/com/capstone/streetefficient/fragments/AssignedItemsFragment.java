package com.capstone.streetefficient.fragments;

import android.Manifest;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.capstone.streetefficient.AddItem;
import com.capstone.streetefficient.functions.Utilities;
import com.google.gson.Gson;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.capstone.streetefficient.R;
import com.capstone.streetefficient.ReviewItem;
import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.SequencedRoute;
import com.capstone.streetefficient.adapters.ItemAdapter;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.fragments.dialogs.GettingLocationDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class AssignedItemsFragment extends Fragment {

    private static final int SCAN_BARCODE = 1;
    private static final int REQUEST_REVIEW = 2;


    private int mPosition;
    private boolean isOpen = true;
    private boolean clickedSequenced = false;


    private ItemAdapter mAdapter;
    private LocationCallback locationCallback;
    private AssignedItemsHelper assignedItemsHelper;
    private GettingLocationDialog gettingLocationDialog;
    private FusedLocationProviderClient fusedLocationClient;
    private FloatingActionButton fab_main, fab_1, fab_2, fab_3;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private TextView TotalItems, TotalWeight, Fab1TextView, Fab2TextView, Fab3TextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_items, container, false);

        assignedItemsHelper = AssignedItemsHelper.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        fab_1 = v.findViewById(R.id.fab_1);
        fab_2 = v.findViewById(R.id.fab_2);
        fab_3 = v.findViewById(R.id.fab_3);
        fab_main = v.findViewById(R.id.fab_main);
        Fab1TextView = v.findViewById(R.id.fab_1_textview);
        Fab2TextView = v.findViewById(R.id.fab_2_textview);
        Fab3TextView = v.findViewById(R.id.fab_3_textview);
        TotalItems = v.findViewById(R.id.assigned_total_items);
        TotalWeight = v.findViewById(R.id.assigned_total_weight);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        fab_clock = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_anticlock);

        TextView Date = v.findViewById(R.id.assigned_date);
        RecyclerView recyclerView = v.findViewById(R.id.items_recycler);

        refreshDetails();
        Date.setText(Utilities.getSimpleDate(new Date()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ItemAdapter(assignedItemsHelper.getAllItems());
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((position) -> {

            Item item = assignedItemsHelper.getItemAtPosition(position);
            mPosition = position;

            Intent intent = new Intent(getActivity(), ReviewItem.class);

            intent.putExtra("item", item);
            intent.putExtra("LatLng", assignedItemsHelper.getLatLngAtPosition(position));
            intent.putExtra("reviewed", assignedItemsHelper.getItemAtPosition(position).isReviewed());

            startActivityForResult(intent, REQUEST_REVIEW);

        });

        fab_1.setOnClickListener(fab1Click);
        fab_2.setOnClickListener(fab2Click);
        fab_3.setOnClickListener(fab3Click);
        fab_main.setOnClickListener(fabMainClick);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                assignedItemsHelper.setLastIndex(new LatLng(locationResult.getLocations().get(0).getLatitude(),
                        locationResult.getLocations().get(0).getLongitude()));
                fusedLocationClient.removeLocationUpdates(locationCallback);
                gettingLocationDialog.dismiss();
                if (clickedSequenced) intentToSequence();
            }
        };

        saveData();
        return v;
    }

    private void refreshDetails() {
        String totalItems = "TOTAL ITEMS: " + assignedItemsHelper.getAllItems().size();
        String weight = "TOTAL WEIGHT: " + (Utilities.assignedWeight(assignedItemsHelper.getAllItems()));

        TotalWeight.setText(weight);
        TotalItems.setText(totalItems);

    }

    private final View.OnClickListener fab3Click = v -> startLocationUpdates();

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_REVIEW && resultCode == Activity.RESULT_OK && data != null) {
            assignedItemsHelper.addLatLng(mPosition, data.getParcelableExtra("LatLng"));
            assignedItemsHelper.getItemAtPosition(mPosition).setReviewed(true);
            mAdapter.notifyDataSetChanged();
        }

        if (requestCode == SCAN_BARCODE && resultCode == Activity.RESULT_OK && data != null) {
            Toast.makeText(getActivity(), "SALHD", Toast.LENGTH_SHORT).show();
            ArrayList<Item> items = (ArrayList<Item>) data.getSerializableExtra("items");

            assignedItemsHelper.addItems(items);
            mAdapter.notifyDataSetChanged();
            refreshDetails();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {

        gettingLocationDialog = new GettingLocationDialog();
        gettingLocationDialog.show(requireActivity().getSupportFragmentManager(), "gettingDialog");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setSmallestDisplacement(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if ((ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

    }

    private final View.OnClickListener fabMainClick = v -> {
        if (isOpen) {
            Fab1TextView.setVisibility(View.VISIBLE);
            Fab2TextView.setVisibility(View.VISIBLE);
            Fab3TextView.setVisibility(View.VISIBLE);
            fab_1.startAnimation(fab_open);
            fab_2.startAnimation(fab_open);
            fab_3.startAnimation(fab_open);
            fab_main.startAnimation(fab_clock);
            isOpen = false;
        } else {
            Fab1TextView.setVisibility(View.INVISIBLE);
            Fab2TextView.setVisibility(View.INVISIBLE);
            Fab3TextView.setVisibility(View.INVISIBLE);
            fab_1.startAnimation(fab_close);
            fab_2.startAnimation(fab_close);
            fab_3.startAnimation(fab_close);
            fab_main.startAnimation(fab_anticlock);
            isOpen = true;
        }
    };

    private final View.OnClickListener fab1Click = v -> {

        Intent intent = new Intent(getActivity(), AddItem.class);
        startActivityForResult(intent, SCAN_BARCODE);
    };

    private final View.OnClickListener fab2Click = v -> {
        if(assignedItemsHelper.getAllItems().isEmpty() || assignedItemsHelper.getLatLngs() == null) return;

        for (int i = 0; i < assignedItemsHelper.getLatLngs().length - 1; i++) {
            if (assignedItemsHelper.getLatLngAtPosition(i) == null) {
                Toast.makeText(getActivity(), "ITEM " + (i + 1) + " : NOT REVIEWED", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (assignedItemsHelper.getLatLngAtPosition(assignedItemsHelper.getLatLngs().length - 1) == null) {
            clickedSequenced = true;
            startLocationUpdates();
            return;
        }
        intentToSequence();
    };

    private void intentToSequence() {
        Intent intent = new Intent(getActivity(), SequencedRoute.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void saveData() {
        SharedPreferences mprefs = requireActivity().getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mprefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(AssignedItemsHelper.getInstance());
        prefsEditor.putString("assignedItemsHelper", json);
        prefsEditor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveData();
    }


}