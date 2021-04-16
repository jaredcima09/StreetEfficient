package com.capstone.streetefficient.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.streetefficient.Documents;
import com.capstone.streetefficient.R;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.DispatchRider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView NAME = v.findViewById(R.id.profile_name);
        TextView GENDER = v.findViewById(R.id.profile_gender);
        TextView ADDRESS = v.findViewById(R.id.profile_address);
        TextView CONTACT = v.findViewById(R.id.profile_contact);
        TextView BirthDay = v.findViewById(R.id.profile_birthday);
        TextView EMERGENCY = v.findViewById(R.id.profile_emergency);
        TextView ShowDocuments = v.findViewById(R.id.profile_documents);

        FirebaseFirestore.getInstance().collection("Dispatch Riders").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(!documentSnapshot.exists()) return;
                    DispatchRider rider = documentSnapshot.toObject(DispatchRider.class);

                    GENDER.append(Utilities.italicizeText(rider.getGender()));
                    ADDRESS.append(Utilities.italicizeText(rider.getAddress()));
                    CONTACT.append(Utilities.italicizeText(rider.getContactNumber()));
                    EMERGENCY.append(Utilities.italicizeText(rider.getEmerg_number()));
                    BirthDay.append(Utilities.italicizeText(Utilities.getSimpleDate(rider.getBirthdate())));
                    NAME.append(Utilities.italicizeText(rider.getFname() + " " + rider.getMname() + " " + rider.getLname()));


                    ShowDocuments.setOnClickListener(v1 -> {
                        ArrayList<String>docs = new ArrayList<>();
                         
                        docs.add(rider.getcR());
                        docs.add(rider.getoR());
                        docs.add(rider.getLicense());
                        docs.add(rider.getWrittenExam());
                        docs.add(rider.getActualAssessment());

                        Intent intent = new Intent(getActivity(), Documents.class);
                        intent.putExtra("documents", docs);
                        startActivity(intent);

                    });
                });

        return v;
    }

}