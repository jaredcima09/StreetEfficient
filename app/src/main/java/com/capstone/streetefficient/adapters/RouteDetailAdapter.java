package com.capstone.streetefficient.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.models.RouteDetail;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RouteDetailAdapter extends RecyclerView.Adapter<RouteDetailAdapter.RouteDetailHolder> {

    private final List<DocumentSnapshot> queryDocumentSnapshots;

    public RouteDetailAdapter(QuerySnapshot queryDocumentSnapshots) {
        this.queryDocumentSnapshots = queryDocumentSnapshots.getDocuments();
    }

    @NonNull
    @Override
    public RouteDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_details, parent, false);
        return new RouteDetailHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RouteDetailHolder holder, int position) {
        RouteDetail routeDetail = queryDocumentSnapshots.get(position).toObject(RouteDetail.class);
        if (routeDetail == null) return;

        if (routeDetail.getOrigin_item_number().equals("BRANCH WAREHOUSE"))
            holder.ORIGIN.append(Utilities.italicizeText("BRANCH WAREHOUSE"));

        else getAddress(routeDetail.getOrigin_item_number(), holder.ORIGIN);

        if (routeDetail.getDestination_item_number().equals("BRANCH WAREHOUSE"))
            holder.DESTINATION.append(Utilities.italicizeText("BRANCH WAREHOUSE"));

        else getAddress(routeDetail.getDestination_item_number(), holder.DESTINATION);

        holder.SCORE.append(Utilities.assessScore(routeDetail.getScore()));
        holder.SPEED.append(Utilities.assessSpeed(routeDetail.getSpeed()));
        holder.DATE.setText(Utilities.getSimpleDate(routeDetail.getDate()));

        //holder.DESTINATION.append(Utilities.italicizeText(routeDetail.getDestination_item_number()));
        holder.DISTANCE.append(Utilities.italicizeText(Utilities.roundOff(routeDetail.getDistance()) + " KM"));

    }

    private void getAddress(String route_detail_id, TextView textView) {
        FirebaseFirestore.getInstance().collection("Items").document(route_detail_id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Item item = documentSnapshot.toObject(Item.class);
                    if (item == null) return;
                    textView.append(Utilities.italicizeText(item.getItemRecipientAddressStreet()
                            + " " + item.getItemRecipientAddressBarangay()
                            + " " + item.getItemRecipientAddressCity()));
                });
    }

    @Override
    public int getItemCount() {
        return queryDocumentSnapshots.size();
    }

    public static class RouteDetailHolder extends RecyclerView.ViewHolder {

        TextView DATE, ORIGIN, DESTINATION, DISTANCE, SPEED, SCORE;

        public RouteDetailHolder(@NonNull View itemView) {
            super(itemView);
            DATE = itemView.findViewById(R.id.detail_date);
            SCORE = itemView.findViewById(R.id.detail_score);
            SPEED = itemView.findViewById(R.id.detail_speed);
            ORIGIN = itemView.findViewById(R.id.detail_origin);
            DISTANCE = itemView.findViewById(R.id.detail_distance);
            DESTINATION = itemView.findViewById(R.id.detail_destination);
        }
    }
}
