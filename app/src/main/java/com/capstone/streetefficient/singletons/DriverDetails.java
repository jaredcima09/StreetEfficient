package com.capstone.streetefficient.singletons;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DriverDetails {

    private String Name;
    private String VehicleType;
    private String ContNumber;
    private static  DriverDetails instance;

    private DriverDetails(){
        FirebaseFirestore.getInstance().collection("Dispatch Riders").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(documentSnapshot -> {
                    Name = documentSnapshot.getString("fname") + " " +documentSnapshot.getString("lname");
        });
    }

    public static DriverDetails getInstance( ){
        if (instance == null) {
            instance = new DriverDetails();
        }
        return instance;
    }

    public String getName() {
        return Name;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public String getContNumber() {
        return ContNumber;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public void setContNumber(String contNumber) {
        ContNumber = contNumber;
    }
}
