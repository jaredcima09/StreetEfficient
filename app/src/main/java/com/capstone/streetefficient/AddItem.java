package com.capstone.streetefficient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.capstone.streetefficient.adapters.ItemAdapter;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.DeliveryHeader;
import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.DriverDetails;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.type.LatLng;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AddItem extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_REVIEW = 2;

    private ItemAdapter adapter;
    private ArrayList<Item> items;
    private CodeScanner codeScanner;
    private DriverDetails driverDetails;

    private LinearLayout Parent;
    private TextView AddItem, CANCEL;
    private RecyclerView recyclerView;
    private CodeScannerView scannerView;
    private int mPosition;
    private ArrayList<LatLng> latLngs;
    private AssignedItemsHelper assignedItemsHelper;
    private boolean needsLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        refWidgets();

        items = new ArrayList<>();
        latLngs = new ArrayList<>();
        driverDetails = DriverDetails.getInstance();
        assignedItemsHelper = AssignedItemsHelper.getInstance();
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(result -> {
            if (!contains(result)) getItem(result.getText());
            codeScanner.startPreview();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(items);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((position) -> {

            Item item = items.get(position);
            mPosition = position;

            Intent intent = new Intent(this, ReviewItem.class);

            intent.putExtra("item", item);
            intent.putExtra("reviewed", items.get(position).isReviewed());

            startActivityForResult(intent, REQUEST_REVIEW);

        });
        AddItem.setOnClickListener(saveClick);
        CANCEL.setOnClickListener(v -> finish());
        scannerView.setOnClickListener(v -> codeScanner.startPreview());
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//
//                Item item = items.get(position);
//                items.remove(position);
//                adapter.notifyItemRemoved(position);
//                Snackbar.make(Parent, "ITEM REMOVED", Snackbar.LENGTH_SHORT)
//                        .setAction("UNDO", v -> {
//                            items.add(position,item);
//                            adapter.notifyItemInserted(position);
//                        })
//                        .setActionTextColor(Color.RED)
//                        .show();
//
//            }
//
//            @Override
//            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                final View foregroundView = ((ItemAdapter.ItemViewHolder) viewHolder).Foreground;
//                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
//                        actionState, isCurrentlyActive);
//            }
//
//        }).attachToRecyclerView(recyclerView);

        needsLatLng = getIntent().getBooleanExtra("fromSequencedRoute", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REVIEW && resultCode == Activity.RESULT_OK && data != null) {
            latLngs.add(data.getParcelableExtra("LatLng"));
            items.get(mPosition).setReviewed(true);
            adapter.notifyDataSetChanged();
        }
    }

    private boolean contains(Result result) {
        if (items.isEmpty()) return false;
        for (Item item : items) {
            if (item.getItem_id().equals(result.getText())) return true;
        }
        codeScanner.startPreview();
        return false;
    }

    private final View.OnClickListener saveClick = v -> {
        if (items.isEmpty()) return;

        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for (Item item : items) {
            if(needsLatLng && !item.isReviewed()){
                Toast.makeText(this, "All items needs to be reviewed", Toast.LENGTH_SHORT).show();
                return;
            }
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Delivery_Header").document();
            DeliveryHeader deliveryHeader = new DeliveryHeader(item.getItem_id(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), driverDetails.getName(), item.getItemweight(), docRef.getId(), Utilities.getSimpleDate(new Date()), item.getItemRecipientContactNumber(), new Date(), new Date());
            assignedItemsHelper.addDeliveryHeader(deliveryHeader);
            batch.set(docRef, deliveryHeader);
        }

        batch.commit().addOnSuccessListener(aVoid -> {
            Intent intent = new Intent();
            intent.putExtra("items", items);
            intent.putExtra("latlngs", latLngs);
            setResult(RESULT_OK, intent);
            finish();

        });


    };

    private void refWidgets() {
        Parent = findViewById(R.id.parent);
        AddItem = findViewById(R.id.add_save);
        CANCEL = findViewById(R.id.add_cancel);
        scannerView = findViewById(R.id.scanner_view);
        recyclerView = findViewById(R.id.add_recycler);
    }

    private void getItem(String text) {

        FirebaseFirestore.getInstance().collection("Items").document(text).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) return;
                    Item item = documentSnapshot.toObject(Item.class);
                    if (item == null) return;
                    if (assignedItemsHelper.containsItem(item.getItem_id()) || item.getStatus().equals("assigned")) {
                        Snackbar.make(Parent, "ITEM ALREADY ASSIGNED", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    Snackbar.make(Parent, "ITEM ADDED", Snackbar.LENGTH_SHORT).show();
                    items.add(documentSnapshot.toObject(Item.class));
                    adapter.notifyDataSetChanged();

                });
    }


}

//SurfaceView surfaceView = findViewById(R.id.surfaceview);
//        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
//                .setBarcodeFormats(Barcode.ALL_FORMATS)
//                .build();
//        CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
//                .setRequestedPreviewSize(1920,1080)
//                .setAutoFocusEnabled(true)
//                .build();
//
//        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(@NonNull SurfaceHolder holder) {
//                try {
//                    if (ActivityCompat.checkSelfPermission(AddItem.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                        cameraSource.start(surfaceView.getHolder());
//                    } else {
//                        ActivityCompat.requestPermissions(AddItem.this, new
//                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//                cameraSource.stop();
//            }
//        });
//
//        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() {
//
//            }
//
//            @Override
//            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
//
//            }
//        });