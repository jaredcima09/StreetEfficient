package com.capstone.streetefficient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.adapters.RouteDetailAdapter;
import com.capstone.streetefficient.fragments.dialogs.CalendarDialog;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.Performance;
import com.capstone.streetefficient.models.RouteHeader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PerformanceFragment extends Fragment implements CalendarDialog.CalendarDialogListener {


    private RecyclerView RecyclerView;
    private RouteDetailAdapter adapter;
    private TextView OverallScore, AverageSpeed, HeaderDate, HeaderScore, FollowRoute, HeaderTime, HeaderSpeed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_performance, container, false);

        OverallScore = v.findViewById(R.id.performance_score);
        AverageSpeed = v.findViewById(R.id.performance_speed);
        FollowRoute = v.findViewById(R.id.performance_follow);
        RecyclerView = v.findViewById(R.id.performance_recycler);
        HeaderDate = v.findViewById(R.id.performance_header_date);
        HeaderTime = v.findViewById(R.id.performance_header_time);
        HeaderSpeed = v.findViewById(R.id.performance_header_speed);
        HeaderScore = v.findViewById(R.id.performance_header_score);
        TextView SelectDate = v.findViewById(R.id.performance_select);


        getPerformance();
        SelectDate.setOnClickListener(selectDate);
        RecyclerView.setNestedScrollingEnabled(false);
        return v;
    }

    private final View.OnClickListener selectDate = v -> new CalendarDialog().show(getChildFragmentManager(), "calendarDialog");


    private void getHeader(String simpleDate) {
        FirebaseFirestore.getInstance().collection("Route_Header").whereEqualTo("rider_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("date_string", simpleDate).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) return;

                    RouteHeader routeHeader = queryDocumentSnapshots.getDocuments().get(0).toObject(RouteHeader.class);
                    if (routeHeader == null) return;
                    HeaderDate.setVisibility(View.VISIBLE);
                    HeaderTime.setVisibility(View.VISIBLE);
                    HeaderScore.setVisibility(View.VISIBLE);
                    HeaderSpeed.setVisibility(View.VISIBLE);

                    HeaderDate.setText(simpleDate);
                    HeaderScore.append(Utilities.assessScore(routeHeader.getTotal_score()));
                    HeaderSpeed.append(Utilities.assessSpeed(routeHeader.getAverage_speed()));
                    HeaderTime.append(Utilities.italicizeText(PhysicsFunctions.decimalToHour(routeHeader.getTotal_delivery_time())));

                    FirebaseFirestore.getInstance().collection("Route_Detail").whereEqualTo("route_header_id", routeHeader.getRoute_header()).orderBy("date", Query.Direction.ASCENDING)
                            .get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                        RecyclerView.setVisibility(View.VISIBLE);
                        adapter = new RouteDetailAdapter(queryDocumentSnapshots1);
                        RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        RecyclerView.setAdapter(adapter);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
    }

//    private void setUpRecyclerView() {
//        Query query = FirebaseFirestore.getInstance()
//                .collection("Route_Header")
//                .whereEqualTo("rider_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .orderBy("date", Query.Direction.ASCENDING);
//
//        FirestoreRecyclerOptions<RouteHeader> options = new FirestoreRecyclerOptions.Builder<RouteHeader>()
//                .setQuery(query, RouteHeader.class)
//                .build();
////
//        adapter = new RouteHeaderAdapter(options);
//        RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        RecyclerView.setAdapter(adapter);
//        adapter.startListening();
//
//    }

    private void getPerformance() {

        FirebaseFirestore.getInstance().collection("Performance").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        OverallScore.append(Utilities.italicizeText(0 + " %"));
                        AverageSpeed.append(Utilities.italicizeText(0 + " KM/H"));
                    }

                    Performance performance = documentSnapshot.toObject(Performance.class);
                    if (performance == null) return;

                    OverallScore.append(Utilities.assessScore(performance.getAverage_score()));
                    AverageSpeed.append(Utilities.assessSpeed(performance.getAverage_speed()));

                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void applyDate(String date) {
        Toast.makeText(getActivity(), date, Toast.LENGTH_SHORT).show();
        getHeader(date);
    }
}