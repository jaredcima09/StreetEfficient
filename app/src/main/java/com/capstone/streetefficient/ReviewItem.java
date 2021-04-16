package com.capstone.streetefficient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.streetefficient.fragments.dialogs.BottomSheetDialog;
import com.capstone.streetefficient.models.Item;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ReviewItem extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {

    private ImageView imageView;
    private GoogleMap googleMap;
    private Item item;
    private TextView adds, fab_1_textview, fab_2_textview;
    private FloatingActionButton fab_main, fab_1, fab_2;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;


    private boolean isOpen = true;
    private boolean isLocked = true;
    private LatLng latLng;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_item);
        refWidgets();


        item = (Item) getIntent().getSerializableExtra("item");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        adds.setText(item.getItemRecipientAddressStreet()
                + ", " + item.getItemRecipientAddressBarangay()
                + ", " + item.getItemRecipientAddressCity());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        fab_main.setOnClickListener(fabMainClick);
        fab_1.setOnClickListener(fab1Click);
        fab_2.setOnClickListener(fab2Click);
    }

    private final View.OnClickListener fabMainClick = v -> {
        if (isOpen) {
            fab_1_textview.setVisibility(View.VISIBLE);
            fab_2_textview.setVisibility(View.VISIBLE);
            fab_1.startAnimation(fab_open);
            fab_2.startAnimation(fab_open);
            fab_main.startAnimation(fab_clock);
            isOpen = false;
        } else {
            closeFab();
        }
    };

    private void closeFab() {
        fab_1_textview.setVisibility(View.INVISIBLE);
        fab_2_textview.setVisibility(View.INVISIBLE);
        fab_1.startAnimation(fab_close);
        fab_2.startAnimation(fab_close);
        fab_main.startAnimation(fab_anticlock);
        isOpen = true;
    }

    private void refWidgets() {
        fab_1 = findViewById(R.id.fab_1);
        fab_2 = findViewById(R.id.fab_2);
        fab_main = findViewById(R.id.fab_main);
        imageView = findViewById(R.id.review_pin);
        adds = findViewById(R.id.toolbar_textview);
        fab_1_textview = findViewById(R.id.fab_1_textview);
        fab_2_textview = findViewById(R.id.fab_2_textview);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);
    }

    @SuppressLint("SetTextI18n")
    private final View.OnClickListener fab1Click = v -> {
        if (isLocked) {
            isLocked = false;
            fab_1_textview.setText("Lock Pin");
            fab_1.setImageDrawable(ContextCompat.getDrawable(ReviewItem.this, R.drawable.ic_outline_lock_24));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        } else {
            isLocked = true;
            fab_1_textview.setText("Unlock Pin");
            fab_1.setImageDrawable(ContextCompat.getDrawable(ReviewItem.this, R.drawable.ic_outline_lock_open_24));
        }
        closeFab();
    };

    private final View.OnClickListener fab2Click = v -> {
        Intent intent = new Intent();
        intent.putExtra("LatLng", latLng);
        setResult(Activity.RESULT_OK, intent);
        finish();
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        imageView.setVisibility(View.INVISIBLE);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnMarkerClickListener(this);

        boolean reviewed = getIntent().getBooleanExtra("reviewed", false);

        if (!reviewed) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocationName(item.getItemRecipientAddressStreet()
                        + " " + item.getItemRecipientAddressBarangay()
                        + " " + item.getItemRecipientAddressProvince(), 3);
                Address location = addresses.get(0);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            latLng = getIntent().getParcelableExtra("LatLng");
        }

        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.5f));
            googleMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(this))
                    .position(latLng));
        }
        catch (Exception e){
            Log.d("ERROR", e.getLocalizedMessage());
        }



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_pin);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onCameraMove() {
        if (!isLocked) {
            imageView.setVisibility(View.VISIBLE);
            googleMap.clear();
        }

    }

    @Override
    public void onCameraIdle() {
        if (!isLocked) {
            googleMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(this))
                    .position(googleMap.getCameraPosition()
                            .target));
            imageView.setVisibility(View.INVISIBLE);
            latLng = googleMap.getCameraPosition().target;
        }

    }

    public void ScreenTap(View view) {
        Toast.makeText(this, "screen tap", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(item);
        bottomSheetDialog.show(getSupportFragmentManager(), "example bottom sheet");
        return false;
    }
}