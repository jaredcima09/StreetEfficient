package com.capstone.streetefficient.functions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.capstone.streetefficient.BottomMain;
import com.capstone.streetefficient.models.DeliveryHeader;
import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetItemsAssigned {


    public GetItemsAssigned(String dateAssigned, Context context, String riderID) {
        this.getAssignedItems(dateAssigned, context, riderID);
    }

    private void getAssignedItems(String dateAssigned, Context context, String riderID) {

        AssignedItemsHelper assignedItemsHelper = AssignedItemsHelper.getInstance();

        FirebaseFirestore.getInstance().collection("Delivery_Header")
                .whereEqualTo("del_date_sched_string", dateAssigned)
                .whereEqualTo("rider_id", riderID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        openMain(context);
                        return;
                    }

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        DeliveryHeader deliveryHeader = documentSnapshot.toObject(DeliveryHeader.class);
                        assignedItemsHelper.addDeliveryHeader(deliveryHeader);

                        FirebaseFirestore.getInstance().collection("Items")
                                .document(documentSnapshot.getString("item_id"))
                                .get()
                                .addOnSuccessListener(documentSnapshots -> {

                                    Item item = documentSnapshots.toObject(Item.class);
                                    assignedItemsHelper.addItem(item);

                                    if (assignedItemsHelper.getCounter() == queryDocumentSnapshots.size()) {
                                        assignedItemsHelper.setLastIndex(null);
                                        openMain(context);
                                    }
                                });
                    }
                });
    }

    private void openMain(Context context) {
        Intent intent = new Intent(context, BottomMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity) context).finish();
    }


}
