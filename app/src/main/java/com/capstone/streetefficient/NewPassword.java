package com.capstone.streetefficient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPassword extends AppCompatActivity {

    private TextInputLayout FirstPass, SecondPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        FirstPass = findViewById(R.id.newpass_first);
        SecondPass = findViewById(R.id.newpass_second);
        MaterialButton SetBtn = findViewById(R.id.newpass_btn);

        SecondPass.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if( s.toString().length() > 0 && FirstPass.getEditText().getText().toString().length() > 0){
                    if(s.toString().equals(FirstPass.getEditText().getText().toString())){
                        Toast.makeText(NewPassword.this, "Same Pass", Toast.LENGTH_SHORT).show();
                        FirstPass.setError("");
                        SecondPass.setError("");
                    }
                    else {
                        FirstPass.setError("Password does not match");
                        SecondPass.setError("Password does not match");
                    }
                }

            }
        });

        SetBtn.setOnClickListener(v -> {
            FirstPass.setError("");
            SecondPass.setError("");
            if (isEmptyFirst() | isEmptySecond()) {
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.getCurrentUser().updatePassword(FirstPass.getEditText().getText().toString().trim())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            FirebaseFirestore.getInstance().collection("Dispatch Riders")
                                    .document(mAuth.getCurrentUser().getUid())
                                    .update("password", FirstPass.getEditText().getText().toString())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "PASSWORD CHANGED", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, BottomMain.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    });
                        }
                    });

        });
    }

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