package com.capstone.streetefficient;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.widget.Toast;

import com.capstone.streetefficient.fragments.BreakdownFragment;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.DeliveryDetail;
import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.models.RouteDetail;
import com.capstone.streetefficient.singletons.SequencedRouteHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.capstone.streetefficient.models.DeliveryHeader;
import com.capstone.streetefficient.fragments.dialogs.UnsuccessfulDialog;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ArrivedLocation extends AppCompatActivity implements UnsuccessfulDialog.UnsuccessfulDialogListener {


    private static final int REQUEST_SIGNATURE = 1001;
    private Item item;
    private AssignedItemsHelper assignedItemsHelper;
    private CardView SuccessfulDeliver, UnsuccessfulDeliver;
    private TextView CallCustomer, MessageCustomer, ADDRESS, NAME, COD, ID;
    private RouteDetail mRouteDetail;
    private String downloadLocation;
    private SequencedRouteHelper sequencedRouteHelper;

    private int mCURRENT_LOCATION;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrived_location);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        refWidgets();

        downloadLocation = "NA";
        assignedItemsHelper = AssignedItemsHelper.getInstance();
        sequencedRouteHelper = SequencedRouteHelper.getInstance();
        mCURRENT_LOCATION = getIntent().getIntExtra("destination", 0);

        item = assignedItemsHelper.getItemAtPosition(mCURRENT_LOCATION);

        ID.setText(item.getItem_id());
        COD.setText(item.getItemCOD());
        NAME.setText(item.getItemRecipientname());
        ADDRESS.setText(Utilities.getAddress(mCURRENT_LOCATION));
        if (item.getItemCOD().equals("NA")) COD.setTextColor(Color.GREEN);

        mRouteDetail = sequencedRouteHelper.getRouteDetails().get(sequencedRouteHelper.getRouteDetails().size() - 1);
        BreakdownFragment breakdownFragment = new BreakdownFragment(sequencedRouteHelper.getScoreBreakdown().get(sequencedRouteHelper.getRouteDetails().size() - 1), true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, breakdownFragment, breakdownFragment.getClass().getSimpleName()).commit();

        CallCustomer.setOnClickListener(callClick);
        MessageCustomer.setOnClickListener(messageClick);
        SuccessfulDeliver.setOnClickListener(successClick);
        UnsuccessfulDeliver.setOnClickListener(unSuccessClick);
        computeSpeed();
    }

    private final View.OnClickListener successClick = v -> {
        //updateItem("SD");
        startActivityForResult(new Intent(this, SignItem.class)
                .putExtra("itemID", item.getItem_id())
                .putExtra("customerName", NAME.getText().toString()), REQUEST_SIGNATURE);
    };

    private final View.OnClickListener unSuccessClick = v -> {
        UnsuccessfulDialog dialog = new UnsuccessfulDialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    };

    private final View.OnClickListener callClick = v -> {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + item.getItemRecipientContactNumber()));
        startActivity(intent);
    };

    private final View.OnClickListener messageClick = v -> {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", item.getItemRecipientContactNumber(), null)));
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNATURE && resultCode == RESULT_OK && data != null) {
            downloadLocation = data.getStringExtra("imageUri");
            updateItem("SD");
            Toast.makeText(this, "TRUE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void applyDialog(String status) {
        updateItem(status);
    }

    private void updateItem(String itemStatus) {

        DocumentReference delAttemptRef = FirebaseFirestore.getInstance().collection("Delivery_Attempt").document(item.getItem_id());

        HashMap<String, Object> delAttempt = getDelAttemptMap(itemStatus);
        delAttemptRef.get().addOnSuccessListener(documentSnapshot -> {
            //if has previous attempt
            if (documentSnapshot.exists())
                delAttemptRef.update(delAttempt).addOnSuccessListener(aVoid -> executeBatchWrite(itemStatus));
                //no previous attempt
            else {
                if (itemStatus.equals("SD") || itemStatus.equals("RS"))
                    executeBatchWrite(itemStatus);
                else
                    delAttemptRef.set(delAttempt).addOnSuccessListener(aVoid -> executeBatchWrite(itemStatus));
            }

        });

    }

    private void executeBatchWrite(String itemStatus) {
        DeliveryHeader deliveryHeader = assignedItemsHelper.getDeliveryHeaderAtPosition(mCURRENT_LOCATION);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Delivery_Detail").document();


        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        batch.set(docRef, new DeliveryDetail(docRef.getId(), deliveryHeader.getDel_item_id(), itemStatus, new Date(), downloadLocation));
        batch.set(FirebaseFirestore.getInstance().collection("Route_Detail").document(mRouteDetail.getRoute_detail_id()), mRouteDetail);
        batch.update(FirebaseFirestore.getInstance().collection("Items").document(item.getItem_id()), "status", updateItemStatus(itemStatus));

        batch.commit().addOnSuccessListener(aVoid -> returnActivityResult());
    }

    private HashMap<String, Object> getDelAttemptMap(String itemStatus) {
        HashMap<String, Object> delAttempt = new HashMap<>();
        delAttempt.put("id", item.getItem_id());
        if (itemStatus.equals("RW")) {
            delAttempt.put("status", "back_to_warehouse");
            delAttempt.put("item_counter", FieldValue.increment(1));
        } else if (itemStatus.equals("SD")) delAttempt.put("status", "delivered");
        else delAttempt.put("status", "cancelled");
        return delAttempt;
    }

    private String updateItemStatus(String itemStatus) {
        if (itemStatus.equals("RW")) return "assigned";
        else if (itemStatus.equals("SD")) return "delivered";
        else return "returned";
    }

    private void returnActivityResult() {
        Intent intent = new Intent();
        intent.putExtra("current_location", mCURRENT_LOCATION);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void refWidgets() {
        ID = findViewById(R.id.arrived_id);
        COD = findViewById(R.id.arrived_cod);
        NAME = findViewById(R.id.arrived_name);
        ADDRESS = findViewById(R.id.arrived_address);
        CallCustomer = findViewById(R.id.arrived_call);
        MessageCustomer = findViewById(R.id.arrived_message);
        SuccessfulDeliver = findViewById(R.id.arrived_successful);
        UnsuccessfulDeliver = findViewById(R.id.arrived_unsuccessful);

    }

    public void computeSpeed(){
        double count = 0;
        double speed = 0;
        double computeDistance= 0;
        double computeTime = 0;


        double prevTime = 0;
        LatLng prevLatlng = null;

        HashMap<String, LatLng> map = sequencedRouteHelper.getGetSpeed();
        for (Map.Entry<String, LatLng> entry : map.entrySet()) {
            double newTime = Double.parseDouble(entry.getKey());
            LatLng newLatlng = entry.getValue();

            if(count == 0) {
                count++;
                prevTime = newTime;
                prevLatlng = newLatlng;
                continue;
            }

            computeTime += newTime - prevTime;
            computeDistance += PhysicsFunctions.getDistance(newLatlng, prevLatlng);



            prevLatlng = newLatlng;
            prevTime = newTime;
            count++;

        }

        speed += PhysicsFunctions.getSpeed(computeDistance, computeTime);
        double averageSpeed = speed / count;
        System.out.println("ACTUAL "+speed);
        System.out.println(map.toString());
        System.out.println("Map Size "+map.size());
        System.out.println("Average Speed: "+averageSpeed);

    }
}