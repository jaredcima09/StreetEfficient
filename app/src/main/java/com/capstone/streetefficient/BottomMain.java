package com.capstone.streetefficient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.capstone.streetefficient.fragments.AssignedItemsFragment;
import com.capstone.streetefficient.fragments.PerformanceFragment;
import com.capstone.streetefficient.fragments.ProfileFragment;
import com.capstone.streetefficient.singletons.DriverDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayDeque;
import java.util.Deque;

public class BottomMain extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Deque<Integer> integerDeque;
    private Fragment fMain, fPerf, fProf;
    private BottomNavigationView bottomNavigationView;

    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_main);
        DriverDetails.getInstance();
        integerDeque = new ArrayDeque<>(3);
        integerDeque.push(R.id.nav_list);
        loadFragment(fMain = new AssignedItemsFragment());

        bottomNavigationView = findViewById(R.id.main_bottomnav);
        bottomNavigationView.setSelectedItemId(R.id.nav_list);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        integerDeque.pop();
        if (!integerDeque.isEmpty()) loadFragment(getFragment(integerDeque.peek()));
        else super.onBackPressed();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (integerDeque.contains(id)) {
            if (id == R.id.nav_list) {
                if (integerDeque.size() != 1) {
                    if (flag) {
                        integerDeque.addFirst(R.id.nav_list);
                        flag = false;
                    }
                }
            }
            integerDeque.remove(id);
        }
        integerDeque.push(id);
        loadFragment(getFragment(id));
        return true;
    }



    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @SuppressLint("NonConstantResourceId")
    private Fragment getFragment(int id) {
        switch (id) {
            case R.id.nav_list:
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                if (fMain == null) {
                    fMain = new AssignedItemsFragment();
                }
                return fMain;

            case R.id.nav_performance:
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                if (fPerf == null) {
                    Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
                    fPerf = new PerformanceFragment();
                }
                return fPerf;

            case R.id.nav_profile:
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                if (fProf == null) {
                    fProf = new ProfileFragment();
                }
                return fProf;

        }

        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        return new AssignedItemsFragment();
    }
}