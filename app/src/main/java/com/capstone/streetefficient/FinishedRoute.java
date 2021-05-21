package com.capstone.streetefficient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.streetefficient.adapters.BreakdownPagerAdapter;
import com.capstone.streetefficient.fragments.BreakdownFragment;
import com.capstone.streetefficient.functions.GetItemsAssigned;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.Performance;
import com.capstone.streetefficient.models.RouteDetail;
import com.capstone.streetefficient.models.RouteHeader;
import com.capstone.streetefficient.models.ScoreBreakdown;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.SequencedRouteHelper;
import com.capstone.streetefficient.widgets.WrapViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class FinishedRoute extends AppCompatActivity {

    private ImageView ARROW;
    private WriteBatch batch;
    private CardView FinishBtn;
    private TabLayout TabLayout;
    private ProgressBar progressBar;
    private WrapViewPager ViewPager;
    private LinearLayout hiddenLayout;
    private RelativeLayout expandableLayout;
    private SequencedRouteHelper sequencedRouteHelper;
    private ArrayList<BreakdownFragment> breakdownFragments;
    private TextView TotalOrders, TotalDeliveryTime, AverageSpeed, ExpectedSequence, ActualSequence, DISTANCE, TotalScore, HEADER, TimeStarted, TimeEnded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_route);
        saveIsSequenced();
        setFragments();
        refWidgets();

        String date = Utilities.getSimpleDate(sequencedRouteHelper.getDate());
        AssignedItemsHelper assignedItemsHelper = AssignedItemsHelper.getInstance();

        batch = FirebaseFirestore.getInstance().batch();

        if (!sequencedRouteHelper.getRouteCheck().isEmpty()) {
            for (Integer integer : sequencedRouteHelper.getRouteCheck()) {
                //if (integer == AssignedItemsHelper.getInstance().getLatLngs().length - 1) continue
                // ;
                DocumentReference routeRef = FirebaseFirestore.getInstance().collection("Route_Detail").document();
                RouteDetail routeDetail = new RouteDetail(new Date(), routeRef.getId(), sequencedRouteHelper.getRouteHeaderID(), "NA",
                        Utilities.getItemID(integer), 0, 0, 0, 0, 0);

                ScoreBreakdown scoreBreakdown = new ScoreBreakdown(0, Utilities.getAddress(integer), "N/A",
                        0, "N/A", routeRef.getId(), 0, 0, 0, 0);

                breakdownFragments.add(new BreakdownFragment(scoreBreakdown, false));
                sequencedRouteHelper.addRouteDetail(routeDetail);
                batch.set(routeRef, routeDetail);

            }
        }
        System.out.println("finished: " + sequencedRouteHelper.getRouteCheck());

        double correct = 0;
        int failedToDeliver = sequencedRouteHelper.getSequencedRoute().size() - sequencedRouteHelper.getRouteTaken().size();
        System.out.println("finished: " + failedToDeliver);

        double averageSpeed = 0;
        double totalDistance = 0;
        double averageTotalScore = 0;

        for (RouteDetail routeDetail : sequencedRouteHelper.getRouteDetails()) {
            averageSpeed += routeDetail.getSpeed();
            totalDistance += routeDetail.getDistance();
            averageTotalScore += routeDetail.getScore();
        }

        System.out.println("finished: " + averageSpeed);

        for (int i = 0; i < sequencedRouteHelper.getRouteTaken().size(); i++) {
            if (sequencedRouteHelper.getRouteTaken().get(i).equals(sequencedRouteHelper.getSequencedRoute().get(i)))
                correct++;
        }

        averageTotalScore /= sequencedRouteHelper.getRouteDetails().size();
        if ((sequencedRouteHelper.getRouteDetails().size() - failedToDeliver) != 0)
            averageSpeed /= (sequencedRouteHelper.getRouteDetails().size() - failedToDeliver);

        System.out.println("finished: " + averageSpeed);
        double finalPerformanceScore = ((averageTotalScore / 100) * 50) + ((correct / sequencedRouteHelper.getSequencedRoute().size()) * 50);
        RouteHeader routeHeader = new RouteHeader(Utilities.getSimpleDate(new Date()), new Date(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                sequencedRouteHelper.getRouteHeaderID(), finalPerformanceScore, averageSpeed, sequencedRouteHelper.getTotalDeliveryTime(),
                totalDistance, sequencedRouteHelper.getSequencedRoute().equals(sequencedRouteHelper.getRouteTaken()));


        HEADER.append(Utilities.italicizeText(date));
        TimeEnded.append(getTime(sequencedRouteHelper.getStopDeliveryTime()));
        TotalScore.append(Utilities.assessScore(routeHeader.getTotal_score()));
        AverageSpeed.append(Utilities.assessSpeed(routeHeader.getAverage_speed()));
        TimeStarted.append(getTime(sequencedRouteHelper.getInitialDeliveryTime()));
        ActualSequence.append(Utilities.italicizeText(getSequence(sequencedRouteHelper.getRouteTaken())));
        TotalOrders.append(Utilities.italicizeText(String.valueOf(assignedItemsHelper.getAllItems().size())));
        ExpectedSequence.append((Utilities.italicizeText(getSequence(sequencedRouteHelper.getSequencedRoute()))));
        DISTANCE.append((Utilities.italicizeText(Utilities.roundOff(routeHeader.getTotal_distance()) + "KM")));
        TotalDeliveryTime.append(Utilities.italicizeText(PhysicsFunctions.decimalToHour(routeHeader.getTotal_delivery_time())));

        setUpPager();
        updatePerformance(routeHeader);
        FinishBtn.setOnClickListener(finishSummary);
        expandableLayout.setOnClickListener(v -> expand(hiddenLayout));

    }

    private final View.OnClickListener finishSummary = v -> {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences mPrefs = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        String prefDate = mPrefs.getString("Date", "");
        boolean sameDate = prefDate.equals(Utilities.getSimpleDate(new Date()));

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putBoolean("sameDate", sameDate);
        prefsEditor.putString("Date", Utilities.getSimpleDate(new Date()));
        prefsEditor.apply();

        if (sameDate) {

            AssignedItemsHelper.getInstance().reset();
            SequencedRouteHelper.getInstance().reset();
            Intent intent = new Intent(this, BottomMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }


        new GetItemsAssigned(Utilities.getSimpleDate(new Date()), this, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

    };

    private String getTime(double millis) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) millis);
        return dateFormat.format(calendar.getTime());
    }

    private void setFragments() {
        sequencedRouteHelper = SequencedRouteHelper.getInstance();
        breakdownFragments = new ArrayList<>();
        for (ScoreBreakdown scoreBreakdown : sequencedRouteHelper.getScoreBreakdown())
            breakdownFragments.add(new BreakdownFragment(scoreBreakdown, true));
    }

    private void setUpPager() {
        BreakdownPagerAdapter breakdownPagerAdapter = new BreakdownPagerAdapter(getSupportFragmentManager(), breakdownFragments);
        ViewPager.setAdapter(breakdownPagerAdapter);
        TabLayout.setupWithViewPager(ViewPager, true);
    }

    private String getSequence(ArrayList<Integer> route) {
        String sequence = "BRANCH WAREHOUSE ->";
        if (route.isEmpty()) return sequence;

        for (int i = 0; i < route.size() - 1; i++)
            sequence = sequence.concat(Utilities.getAddress(route.get(i)) + " -> ");

        sequence = sequence.concat(Utilities.getAddress(route.get(route.size() - 1)));
        return sequence;
    }

    private void refWidgets() {
        FinishBtn = findViewById(R.id.finish_btn);
        TabLayout = findViewById(R.id.finish_tabs);
        ARROW = findViewById(R.id.expandable_arrow);
        HEADER = findViewById(R.id.finished_header);
        ViewPager = findViewById(R.id.finish_pager);
        TimeEnded = findViewById(R.id.expandable_end);
        progressBar = findViewById(R.id.progress_bar);
        TotalScore = findViewById(R.id.finished_score);
        DISTANCE = findViewById(R.id.finished_distance);
        AverageSpeed = findViewById(R.id.finished_speed);
        TotalOrders = findViewById(R.id.finished_orders);
        TimeStarted = findViewById(R.id.expandable_start);
        ActualSequence = findViewById(R.id.finished_actual);
        hiddenLayout = findViewById(R.id.expandable_hidden);
        TotalDeliveryTime = findViewById(R.id.finished_time);
        expandableLayout = findViewById(R.id.expandable_layout);
        ExpectedSequence = findViewById(R.id.finished_expected);
    }


    private void updatePerformance(RouteHeader header) {
        DocumentReference performanceRef = FirebaseFirestore.getInstance().collection("Performance").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
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
                    totalScore += header.getTotal_score();
                    totalSpeed += header.getAverage_speed();
                    totalTime += header.getTotal_delivery_time();

                    batch.set(performanceRef, new Performance(FirebaseAuth.getInstance().getUid(), totalSpeed / (queryDocumentSnapshots.size() + 1),
                            totalTime / (queryDocumentSnapshots.size() + 1) / (queryDocumentSnapshots.size() + 1), totalScore / (queryDocumentSnapshots.size() + 1)));
                    batch.commit();

                });
    }

    public void expand(final View v) {
        ARROW.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_drop_up_24));
        expandableLayout.setOnClickListener(v1 -> collapse(hiddenLayout));
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        ARROW.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_drop_down_24));
        expandableLayout.setOnClickListener(v1 -> expand(hiddenLayout));
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void saveIsSequenced() {
        SharedPreferences mprefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mprefs.edit();
        prefsEditor.putBoolean("isSequenced", false);
        prefsEditor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveIsSequenced();
    }
}