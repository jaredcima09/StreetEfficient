package com.capstone.streetefficient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.capstone.streetefficient.fragments.dialogs.GettingLocationDialog;
import com.capstone.streetefficient.functions.GetItemsAssigned;
import com.capstone.streetefficient.functions.Utilities;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class NewPassword extends AppCompatActivity {

    private boolean clicked, same;
    private ProgressBar progressBar;
    private TextInputLayout FirstPass, SecondPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        same = false;
        clicked = false;
        FirstPass = findViewById(R.id.newpass_first);
        progressBar = findViewById(R.id.progress_bar);
        SecondPass = findViewById(R.id.newpass_second);

        CardView setBtn = findViewById(R.id.newpass_btn);

        SecondPass.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0 && FirstPass.getEditText().getText().toString().length() > 0) {
                    if (s.toString().equals(FirstPass.getEditText().getText().toString())) {
                        Toast.makeText(NewPassword.this, "Same Pass", Toast.LENGTH_SHORT).show();
                        FirstPass.setError("");
                        SecondPass.setError("");
                        same = true;
                    } else {
                        FirstPass.setError("Password does not match");
                        SecondPass.setError("Password does not match");
                        same = false;
                    }
                }

            }
        });

        setBtn.setOnClickListener(changePass);
    }

    View.OnClickListener changePass = v -> {
        if (clicked) {
            Toast.makeText(this, "Please Wait..", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!same) return;

        clicked = true;
        FirstPass.setError("");
        SecondPass.setError("");
        progressBar.setVisibility(View.VISIBLE);

        if (isEmptyFirst() | isEmptySecond()) return;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().updatePassword(FirstPass.getEditText().getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore.getInstance().collection("Dispatch Riders")
                                .document(mAuth.getCurrentUser().getUid())
                                .update("password", FirstPass.getEditText().getText().toString())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "PASSWORD CHANGED", Toast.LENGTH_SHORT).show();
                                    new GetItemsAssigned(Utilities.getSimpleDate(new Date()), this, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                });
                    } else {
                        clicked = false;
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    };

    private boolean isEmptyFirst() {
        if (TextUtils.isEmpty(FirstPass.getEditText().getText().toString())) {
            FirstPass.setError("Empty Field Not Allowed");
            return true;
        }
        return false;
    }

    private boolean isEmptySecond() {
        if (TextUtils.isEmpty(SecondPass.getEditText().getText().toString())) {
            SecondPass.setError("Empty Field Not Allowed");
            return true;
        }
        return false;
    }
}