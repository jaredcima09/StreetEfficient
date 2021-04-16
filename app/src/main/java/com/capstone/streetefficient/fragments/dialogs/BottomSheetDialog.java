package com.capstone.streetefficient.fragments.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.models.Item;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private final Item item;

    public BottomSheetDialog(Item item) {
        this.item = item;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom, container, false);

        TextView ID = v.findViewById(R.id.review_id);
        TextView COD = v.findViewById(R.id.review_cod);
        TextView name = v.findViewById(R.id.review_name);
        TextView sName = v.findViewById(R.id.review_sname);
        TextView weight = v.findViewById(R.id.review_weight);
        TextView contact = v.findViewById(R.id.review_contact);
        TextView address = v.findViewById(R.id.review_address);
        TextView sContact = v.findViewById(R.id.review_scontact);
        TextView encodedBy = v.findViewById(R.id.review_encoded);


        ID.setText(item.getItem_id());
        name.setText(item.getItemRecipientname());
        contact.setText(item.getItemRecipientContactNumber());
        address.setText(item.getItemRecipientAddressStreet()
                + ",\n" + item.getItemRecipientAddressBarangay()
                + ", " + item.getItemRecipientAddressCity());

        encodedBy.setText("Encoded By:\n" + item.getEncodedBy());
        sName.setText("From: " + item.getItemSendername());
        sContact.setText(item.getItemSenderContactNumber());
        weight.setText("Weight: " + item.getItemweight());

        if (item.getItemCOD().equals("NA")) {
            COD.setText("NOT COD");
            return v;
        }

        COD.setText("Php" + item.getItemCOD());

        return v;
    }
}
