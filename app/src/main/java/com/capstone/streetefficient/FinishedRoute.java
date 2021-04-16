package com.capstone.streetefficient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.capstone.streetefficient.adapters.BreakdownPagerAdapter;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.Performance;
import com.capstone.streetefficient.models.RouteDetail;
import com.capstone.streetefficient.models.RouteHeader;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.SequencedRouteHelper;
import com.capstone.streetefficient.widgets.WrapViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FinishedRoute extends AppCompatActivity {


    private TabLayout TabLayout;
    private WrapViewPager ViewPager;
    private WriteBatch batch;
    private TextView TotalOrders, TotalDeliveryTime, AverageSpeed, ExpectedSequence, ActualSequence, DISTANCE, TotalScore, HEADER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_route);
        refWidgets();

        String date = getIntent().getStringExtra("date");

        AssignedItemsHelper assignedItemsHelper = AssignedItemsHelper.getInstance();
        SequencedRouteHelper sequencedRouteHelper = SequencedRouteHelper.getInstance();


        RouteDetail routeDetailLast = sequencedRouteHelper.getRouteDetails().get(sequencedRouteHelper.getRouteDetails().size() -1);
        batch = FirebaseFirestore.getInstance().batch();
        batch.set(FirebaseFirestore.getInstance().collection("Route_Detail").document(routeDetailLast.getRoute_detail_id()), routeDetailLast);

        if (!sequencedRouteHelper.getRouteCheck().isEmpty()) {
            for (Integer integer : sequencedRouteHelper.getRouteCheck()) {

                if (integer == AssignedItemsHelper.getInstance().getLatLngs().length - 1) continue;

                DocumentReference routeRef = FirebaseFirestore.getInstance().collection("Route_Detail").document();
                RouteDetail routeDetail = new RouteDetail(new Date(), routeRef.getId(), sequencedRouteHelper.getRouteHeaderID(), "NA",
                        Utilities.getItemID(integer), 0, 0, 0, 0, 0);

                sequencedRouteHelper.addRouteDetail(routeDetail);
                batch.set(routeRef, routeDetail);

            }
        }

        double correct = 0;
        int failedToDeliver = sequencedRouteHelper.getSequencedRoute().size() - sequencedRouteHelper.getRouteTaken().size();

        double averageSpeed = 0;
        double totalDistance = 0;
        double averageTotalScore = 0;


        for (RouteDetail routeDetail : sequencedRouteHelper.getRouteDetails()) {
            averageSpeed += routeDetail.getSpeed();
            totalDistance += routeDetail.getDistance();
            averageTotalScore += routeDetail.getScore();
        }


        for (int i = 0; i < sequencedRouteHelper.getRouteTaken().size(); i++) {
            if (sequencedRouteHelper.getRouteTaken().get(i).equals(sequencedRouteHelper.getSequencedRoute().get(i)))
                correct++;
        }



        averageTotalScore /= sequencedRouteHelper.getRouteDetails().size();
        averageSpeed /= (sequencedRouteHelper.getRouteDetails().size() - failedToDeliver);

        double finalPerformanceScore = ((averageTotalScore / 100) * 50) + ((correct / sequencedRouteHelper.getSequencedRoute().size()) * 50);
        RouteHeader routeHeader = new RouteHeader(Utilities.getSimpleDate(new Date()), new Date(), FirebaseAuth.getInstance().getCurrentUser().getUid(),
                sequencedRouteHelper.getRouteHeaderID(), finalPerformanceScore, averageSpeed, sequencedRouteHelper.getTotalDeliveryTime(),
                totalDistance, sequencedRouteHelper.getSequencedRoute().equals(sequencedRouteHelper.getRouteTaken()));


        HEADER.append(Utilities.italicizeText(date));
        TotalScore.append(Utilities.assessScore(routeHeader.getTotal_score()));
        AverageSpeed.append(Utilities.assessSpeed(routeHeader.getAverage_speed()));
        ActualSequence.append(Utilities.italicizeText(getSequence(sequencedRouteHelper.getRouteTaken())));
        TotalOrders.append(Utilities.italicizeText(String.valueOf(assignedItemsHelper.getAllItems().size())));
        ExpectedSequence.append((Utilities.italicizeText(getSequence(sequencedRouteHelper.getSequencedRoute()))));
        DISTANCE.append((Utilities.italicizeText(Utilities.roundOff(routeHeader.getTotal_distance()) + "KM")));
        TotalDeliveryTime.append(Utilities.italicizeText(PhysicsFunctions.decimalToHour(routeHeader.getTotal_delivery_time())));

        BreakdownPagerAdapter breakdownPagerAdapter = new BreakdownPagerAdapter(getSupportFragmentManager(), sequencedRouteHelper.getBreakdownFragments());
        ViewPager.setAdapter(breakdownPagerAdapter);
        TabLayout.setupWithViewPager(ViewPager, true);
        updatePerformance(routeHeader);


    }

    private String getSequence(ArrayList<Integer> route) {
        String sequence = "";

        for (int i = 0; i < route.size() - 1; i++)
            sequence = sequence.concat(Utilities.getAddress(route.get(i)) + " -> ");

        sequence = sequence.concat(Utilities.getAddress(route.get(route.size() - 1)));

        return sequence;
    }

    private void refWidgets() {
        TabLayout = findViewById(R.id.finish_tabs);
        HEADER = findViewById(R.id.finished_header);
        ViewPager = findViewById(R.id.finish_pager);
        TotalScore = findViewById(R.id.finished_score);
        DISTANCE = findViewById(R.id.finished_distance);
        AverageSpeed = findViewById(R.id.finished_speed);
        TotalOrders = findViewById(R.id.finished_orders);
        ActualSequence = findViewById(R.id.finished_actual);
        TotalDeliveryTime = findViewById(R.id.finished_time);
        ExpectedSequence = findViewById(R.id.finished_expected);
    }

    private void updatePerformance(RouteHeader header) {
        DocumentReference performanceRef = FirebaseFirestore.getInstance().collection("Performance").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        batch.set(FirebaseFirestore.getInstance().collection("Route_Header").document(header.getRoute_header()), header);

        FirebaseFirestore.getInstance().collection("Route_Header")
                .whereEqualTo("rider_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {


                        batch.set(performanceRef, new Performance(FirebaseAuth.getInstance().getUid(), header.getAverage_speed(), header.getTotal_delivery_time(), header.getTotal_score()));
                        batch.commit();
                        return;
                    }


                    double totalTime = 0;
                    double totalScore = 0;
                    double totalSpeed = 0;

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        RouteHeader routeHeader = documentSnapshot.toObject(RouteHeader.class);

                        if (routeHeader == null) return;
                        totalScore += routeHeader.getTotal_score();
                        totalSpeed += routeHeader.getAverage_speed();
                        totalTime += routeHeader.getTotal_delivery_time();

                    }


                    batch.set(performanceRef, new Performance(FirebaseAuth.getInstance().getUid(), totalSpeed / queryDocumentSnapshots.size(),
                            totalTime / queryDocumentSnapshots.size() / queryDocumentSnapshots.size(), totalScore / queryDocumentSnapshots.size()));
                    batch.commit();

                });
    }
}