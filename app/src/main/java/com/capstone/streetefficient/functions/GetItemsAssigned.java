package com.capstone.streetefficient.functions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.capstone.streetefficient.BottomMain;
import com.capstone.streetefficient.R;
import com.capstone.streetefficient.models.DeliveryHeader;
import com.capstone.streetefficient.models.DispatchRider;
import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.DriverDetails;
import com.capstone.streetefficient.singletons.SequencedRouteHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class GetItemsAssigned {


    public GetItemsAssigned(String dateAssigned, Context context, String riderID) {
        this.getAssignedItems(dateAssigned, context, riderID);
    }

    private void getAssignedItems(String dateAssigned, Context context, String riderID) {

        AssignedItemsHelper.getInstance().reset();
        AssignedItemsHelper assignedItemsHelper = AssignedItemsHelper.getInstance();

        FirebaseFirestore.getInstance().collection("Dispatch Riders").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    DriverDetails.getInstance().setDispatchRider(documentSnapshot.toObject(DispatchRider.class));


                    FirebaseFirestore.getInstance().collection("Delivery_Header")
                            .whereEqualTo("del_date_sched_string", dateAssigned)
                            .whereEqualTo("rider_id", riderID)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                if (queryDocumentSnapshots.isEmpty()) {
                                    openMain(context);
                                    return;
                                }


                                for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {

                                    DeliveryHeader deliveryHeader = documentSnapshot1.toObject(DeliveryHeader.class);
                                    assignedItemsHelper.addDeliveryHeader(deliveryHeader);

                                    FirebaseFirestore.getInstance().collection("Items")
                                            .document(Objects.requireNonNull(documentSnapshot1.getString("item_id")))
                                            .get()
                                            .addOnSuccessListener(documentSnapshots -> {

                                                Item item = documentSnapshots.toObject(Item.class);
                                                assignedItemsHelper.addItem(item);


                                                if (assignedItemsHelper.getCounter() == queryDocumentSnapshots.size()) {
                                                    assignedItemsHelper.checkOrder();
                                                    assignedItemsHelper.setLastIndex(null);
                                                    openMain(context);
                                                }
                                            });
                                }
                            });
                });


    }

    private void openMain(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        SequencedRouteHelper.getInstance().setDate(new Date());
        prefsEditor.putString("Date", Utilities.getSimpleDate(new Date()));
        prefsEditor.putBoolean("sameDate", false);
        prefsEditor.apply();


        Intent intent = new Intent(context, BottomMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity) context).finish();
    }


}
