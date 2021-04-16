package com.capstone.streetefficient.adapters;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.RouteHeader;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class RouteHeaderAdapter extends FirestoreRecyclerAdapter<RouteHeader, RouteHeaderAdapter.RouteHeaderHolder> {

    public RouteHeaderAdapter(@NonNull FirestoreRecyclerOptions<RouteHeader> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RouteHeaderHolder holder, int position, @NonNull RouteHeader model) {
        holder.Date.setText(Utilities.getSimpleDate(model.getDate()));
        holder.Score.append(model.getTotal_score()+" %");
    }

    @NonNull
    @Override
    public RouteHeaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
        return new RouteHeaderHolder(v);
    }

     static class RouteHeaderHolder extends RecyclerView.ViewHolder{

        TextView Date, Score;

        public RouteHeaderHolder(@NonNull View itemView) {
            super(itemView);
            Date = itemView.findViewById(R.id.header_date);
            Score = itemView.findViewById(R.id.header_score);
        }
    }
}
