package com.capstone.streetefficient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;

import com.capstone.streetefficient.functions.GetItemsAssigned;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.DriverDetails;
import com.capstone.streetefficient.singletons.SequencedRouteHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Date;

public class Splash extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSIONS = 1;
    private static final int REQUEST_LOCATION_SERVICES = 2;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private AssignedItemsHelper assignedItemsHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.splash_progressbar);
        assignedItemsHelper = AssignedItemsHelper.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isGPSEnable();
    }

    private void isGPSEnable() {
        //first step check if location services is turned on
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //if turned off request user to turn on using dialog
            showDialogGPS();

        } else {
            //if turned on use implicit request for device location
            requestPermissions();
        }
    }

    private void openDialog() {
        new AlertDialog.Builder(this)
                .setTitle("All Permissions Required")
                .setMessage("All Permissions must be accepted to use StreetEfficient")
                .setPositiveButton("OK", (dialog, which) -> requestPermissions())
                .setNegativeButton("Don't Allow", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                })
                .show();
    }


    private void showDialogGPS() {
        new AlertDialog.Builder(this)
                .setTitle("Location Services Required")
                .setMessage("StreetEfficient requires location services to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, REQUEST_LOCATION_SERVICES);
                })
                .create().show();
    }

    private void requestPermissions() {
        //check if location permissions are granted by the user
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

            openLogin();

            //explicit permission or ask permission on runtime to use location
        else
            ActivityCompat.requestPermissions(Splash.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_LOCATION_PERMISSIONS);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            System.out.println(Arrays.toString(grantResults));
            if (grantResults.length > 0 && permissions.length == grantResults.length) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    openLogin(); //if Location permission granted proceed to log in
                } else {
                    // if user denies permissions open dialog
                    progressBar.setVisibility(View.GONE);
                    openDialog();
                }
            }
        }
    }

    private void openLogin() {
        if (mAuth.getCurrentUser() != null) {

            SharedPreferences mPrefs = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
            String prefDate = mPrefs.getString("Date", "");

            boolean isSequenced = mPrefs.getBoolean("isSequenced", false);
            DriverDetails.getInstance();
            assignedItemsHelper.reset();

            System.out.println("NANAY "+prefDate);
            System.out.println("NANAY "+Utilities.getSimpleDate(new Date()));
            if (isSequenced && prefDate.equals(Utilities.getSimpleDate(new Date()))) {
                startIntent(new Intent(this, SequencedRoute.class));
                return;
            }

            if(isSequenced && !prefDate.equals(Utilities.getSimpleDate(new Date()))){
                Gson gson = new Gson();
                assignedItemsHelper = gson.fromJson(mPrefs.getString("assignedItemsHelper", null), AssignedItemsHelper.class);
                SequencedRouteHelper sequencedRouteHelper = gson.fromJson(mPrefs.getString("sequencedRouteHelper", null), SequencedRouteHelper.class);
                assignedItemsHelper.setInstance(assignedItemsHelper);
                sequencedRouteHelper.setInstance(sequencedRouteHelper);
                startIntent(new Intent(this, FinishedRoute.class));
                return;
            }

            if (prefDate.equals(Utilities.getSimpleDate(new Date()))) {
                Gson gson = new Gson();
                assignedItemsHelper = gson.fromJson(mPrefs.getString("assignedItemsHelper", null), AssignedItemsHelper.class);
                assignedItemsHelper.setInstance(assignedItemsHelper);
                Intent intent = new Intent(this, BottomMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            }


            new GetItemsAssigned(Utilities.getSimpleDate(new Date()), this, mAuth.getCurrentUser().getUid());
            return;
        }
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
        finish();
    }

    public void startIntent(Intent intent){
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}