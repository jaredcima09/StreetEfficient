package com.capstone.streetefficient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.streetefficient.fragments.dialogs.GettingLocationDialog;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.widgets.DrawingView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;


public class SignItem extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_item);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ImageView CHECK = findViewById(R.id.signature_save);
        TextView textView = findViewById(R.id.signature_date);
        ImageButton ERASE = findViewById(R.id.signature_erase);
        TextView customerName = findViewById(R.id.signature_name);
        DrawingView drawingView = findViewById(R.id.drawing_view);

        textView.setText(Utilities.getSimpleDate(new Date()));
        customerName.setText(getIntent().getStringExtra("customerName"));

        CHECK.setOnClickListener(saveSignature);
        ERASE.setOnClickListener(v -> drawingView.clear());
    }

    private final View.OnClickListener saveSignature = v -> {
        RelativeLayout relativeLayout = findViewById(R.id.signature_top);
        ImageButton ERASE = findViewById(R.id.signature_erase);
        relativeLayout.setVisibility(View.INVISIBLE);
        ERASE.setVisibility(View.INVISIBLE);

        View view = getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        try {
            uploadImage(bmp);

        } catch (Exception e) {
            e.printStackTrace();
        }


    };

    private void uploadImage(Bitmap bmp) {
        GettingLocationDialog locationDialog = new GettingLocationDialog();
        locationDialog.show(getSupportFragmentManager(), "locationDialog");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("signatures");
        StorageReference picture = storageReference.child(getIntent().getStringExtra("customerName") + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] date = baos.toByteArray();

        picture.putBytes(date).addOnSuccessListener(taskSnapshot -> picture.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                locationDialog.dismiss();
                saveImage(bmp, getIntent().getStringExtra("itemID"));
                setResult(Activity.RESULT_OK,new Intent().putExtra("imageUri", uri.toString()));
                finish();
            } catch (Exception e) {
                locationDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(this, "Error: Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "STREETEFFICIENT");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "STREETEFFICIENT";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

}