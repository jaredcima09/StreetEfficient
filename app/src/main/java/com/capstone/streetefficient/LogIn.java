package com.capstone.streetefficient;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.capstone.streetefficient.functions.GetItemsAssigned;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.DriverDetails;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogIn extends AppCompatActivity {


    private Object FirebaseTooManyRequestsException;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, BottomMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);


        TextInputLayout UserName = findViewById(R.id.login_username);
        TextInputLayout Password = findViewById(R.id.login_password);
        Button Login = findViewById(R.id.login_btn);


        Login.setOnClickListener(v -> {
            Login.setEnabled(false);
            UserName.setError("");
            Password.setError("");

            if (TextUtils.isEmpty(UserName.getEditText().getText().toString()) || TextUtils.isEmpty(Password.getEditText().getText().toString())) {
                Toast.makeText(this, "empty fields", Toast.LENGTH_SHORT).show();
                Login.setEnabled(true);
                return;
            }
            auth.signInWithEmailAndPassword(UserName.getEditText().getText().toString().trim(), Password.getEditText().getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    FirebaseFirestore.getInstance().collection("Dispatch Riders").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                        if (!documentSnapshot.exists()) {
                            Toast.makeText(this, "INVALID USER", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            return;
                        }

                        if (!documentSnapshot.getString("status").equals("active")) {
                            Toast.makeText(this, "Account not Active", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot.getString("password").equals("rider123")) {
                            Intent intent = new Intent(this, NewPassword.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                        AssignedItemsHelper assignedItemsHelper = AssignedItemsHelper.getInstance();
                        DriverDetails.getInstance();
                        assignedItemsHelper.reset();
                        new GetItemsAssigned(Utilities.getSimpleDate(new Date()), this, auth.getCurrentUser().getUid());
                    });
                } else {
                    Toast.makeText(this, "NOT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    Login.setEnabled(true);
                    String errorCode = task.getException().getMessage();

                    switch (errorCode) {
                        case "ERROR_INVALID_CUSTOM_TOKEN":
                            Toast.makeText(LogIn.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_CUSTOM_TOKEN_MISMATCH":
                            Toast.makeText(LogIn.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_INVALID_CREDENTIAL":
                            Toast.makeText(LogIn.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_INVALID_EMAIL":
                            UserName.requestFocus();
                            UserName.setError("The email address is badly formatted.");
                            break;
                        case "ERROR_WRONG_PASSWORD":
                            Password.requestFocus();
                            Password.setError("The password is invalid or the user does not have a password.");
                            break;
                        case "ERROR_USER_MISMATCH":
                            Toast.makeText(LogIn.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_REQUIRES_RECENT_LOGIN":
                            Toast.makeText(LogIn.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                            Toast.makeText(LogIn.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_EMAIL_ALREADY_IN_USE":
                            Toast.makeText(LogIn.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                            Toast.makeText(LogIn.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_USER_DISABLED":
                            Toast.makeText(LogIn.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_USER_TOKEN_EXPIRED":
                            Toast.makeText(LogIn.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_USER_NOT_FOUND":
                            UserName.requestFocus();
                            UserName.setError("There is no user record corresponding to this email");
                            break;
                        case "ERROR_INVALID_USER_TOKEN":
                            Toast.makeText(LogIn.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_OPERATION_NOT_ALLOWED":
                            Toast.makeText(LogIn.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                            break;
                        case "ERROR_WEAK_PASSWORD":
                            Toast.makeText(LogIn.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        });
    }


}