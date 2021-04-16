package com.capstone.streetefficient.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.models.Item;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final ArrayList<Item> items;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ItemAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView ItemID, ItemAddress, ItemReviewed;

        public ItemViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            ItemID = itemView.findViewById(R.id.item_id);
            ItemAddress = itemView.findViewById(R.id.item_address);
            ItemReviewed = itemView.findViewById(R.id.item_reviewed);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_items, parent, false);
        return new ItemViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.ItemID.setText(item.getItem_id());
        holder.ItemAddress.setText(String.format("%s, %s, \n%s",
                item.getItemRecipientAddressStreet(),
                item.getItemRecipientAddressBarangay(),
                item.getItemRecipientAddressCity()));
        if(item.isReviewed()){
            holder.ItemReviewed.setText(R.string.reviewed);
            holder.ItemReviewed.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void deleteItem (int position){
        items.remove(position);
        notifyDataSetChanged();
    }

}
