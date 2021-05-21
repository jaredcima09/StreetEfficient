package com.capstone.streetefficient.singletons;

import com.capstone.streetefficient.models.DispatchRider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DriverDetails {

    private static DriverDetails instance;
    private DispatchRider dispatchRider;


    private DriverDetails() {

    }

    public static DriverDetails getInstance() {
        if (instance == null) instance = new DriverDetails();
        return instance;
    }

    public DispatchRider getDispatchRider() {
        return dispatchRider;
    }

    public String getName(){
        return dispatchRider.getFname()+" "+dispatchRider.getMname()+dispatchRider.getLname();
    }

    public void setDispatchRider(DispatchRider dispatchRider) {
        this.dispatchRider = dispatchRider;
    }

    public void setInstance(DriverDetails instance) {
        DriverDetails.instance = instance;
    }
}
