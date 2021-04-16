package com.capstone.streetefficient.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PopUpDialog extends DialogFragment {


    private Item item;
    private final double distance;
    private final int NEXT_LOCATION;


    public PopUpDialog(double distance, int NEXT_LOCATION) {
        this.distance = distance;
        this.NEXT_LOCATION = NEXT_LOCATION;
    }

    private double time;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_route_popup, null);
        builder.setView(v);

        AssignedItemsHelper assignedItemsHelper = AssignedItemsHelper.getInstance();
        TextView ETA = v.findViewById(R.id.popup_eta);
        TextView ETD = v.findViewById(R.id.popup_etd);
        TextView COD = v.findViewById(R.id.popup_cod);
        TextView BUTTON = v.findViewById(R.id.popup_btn);
        TextView ADDRESS = v.findViewById(R.id.popup_address);

        if (NEXT_LOCATION != assignedItemsHelper.getLatLngs().length - 1) {
            item = assignedItemsHelper.getItemAtPosition(NEXT_LOCATION);
            COD.append(getCOD());
            COD.setVisibility(View.VISIBLE);
            ADDRESS.append(Utilities.italicizeText(item.getItemRecipientAddressStreet()
                    + ", " + item.getItemRecipientAddressBarangay()
                    + ", " + item.getItemRecipientAddressProvince()));
        } else ADDRESS.append(Utilities.italicizeText("BRANCH WAREHOUSE"));

        time = PhysicsFunctions.getEST(distance);

        ETD.append(Utilities.italicizeText(PhysicsFunctions.decimalToHour(time)));
        ETA.append(getETA());
        BUTTON.setOnClickListener(v1 -> dismiss());
        return builder.create();
    }


    private SpannableStringBuilder getCOD() {
        SpannableStringBuilder str = Utilities.italicizeText(" " + item.getItemCOD());
        if (item.getItemCOD().equals("NA"))
            str.setSpan(new ForegroundColorSpan(Color.GREEN), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else
            str.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    private SpannableStringBuilder getETA() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, (int) Math.ceil((time / 3600000) * 60));
        return Utilities.italicizeText(now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));
    }

}
