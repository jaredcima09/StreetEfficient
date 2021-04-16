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
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

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

        assignedItemsHelper = AssignedItemsHelper.getInstance();
        progressBar = findViewById(R.id.splash_progressbar);
        mAuth = FirebaseAuth.getInstance();

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
                .setTitle("Location Access Required")
                .setMessage("We need access to your location and device state to continue using EasyRide")
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
                && checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)

            openLogin();

        else {
            //explicit permission or ask permission on runtime to use location
            ActivityCompat.requestPermissions(Splash.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS}, REQUEST_LOCATION_PERMISSIONS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && permissions.length == grantResults.length) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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
            assignedItemsHelper.reset();
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

            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putString("Date", Utilities.getSimpleDate(new Date()));
            prefsEditor.apply();
            new GetItemsAssigned(Utilities.getSimpleDate(new Date()), this, mAuth.getCurrentUser().getUid());
            return;
        }
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
        finish();
    }
}