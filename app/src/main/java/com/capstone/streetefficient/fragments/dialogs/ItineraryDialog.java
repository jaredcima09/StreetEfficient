package com.capstone.streetefficient.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.SequencedRouteHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ItineraryDialog extends DialogFragment {

    private static final long AUTO_DISMISS_MILLIS = 9000;
    ItineraryDialogListener listener;

    private CountDownTimer countDownTimer;
    private boolean isRunning;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_itinerary, null);
        builder.setView(v);

        double distance = 0;
        String location = "";

        Button BUTTON = v.findViewById(R.id.itinerary_btn);
        TextView TIME = v.findViewById(R.id.itinerary_time);
        TextView LOCATIONS = v.findViewById(R.id.itinerary_locations);
        LatLng[] latLngs = AssignedItemsHelper.getInstance().getLatLngs();
        ArrayList<Integer> sequencedRoute = SequencedRouteHelper.getInstance().getSequencedRoute();

        for (int i = 0; i < sequencedRoute.size(); i++) {

            if (i < sequencedRoute.size() - 1) distance += PhysicsFunctions.getDistance(latLngs[sequencedRoute.get(i)], latLngs[sequencedRoute.get(i + 1)]);

            location = location.concat((i+1) + ". " + Utilities.getAddress(sequencedRoute.get(i))) + "\n" + "\n";

        }


        LOCATIONS.setText(location.trim());
        BUTTON.setOnClickListener(btnClick);
        TIME.append(PhysicsFunctions.decimalToHour(PhysicsFunctions.getEST(distance)));


        countDownTimer = new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                BUTTON.setText(String.format(Locale.getDefault(), "%s (%d)", "START", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1));
            }

            @Override
            public void onFinish() {
                isRunning = false;
                listener.startRoute();
                dismiss();
            }
        }.start();
        return builder.create();
    }

    private final View.OnClickListener btnClick = v -> {
        countDownTimer.cancel();
        listener.startRoute();
        dismiss();
    };

    public interface ItineraryDialogListener {
        void startRoute();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(isRunning)countDownTimer.cancel();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ItineraryDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ItineraryDialogListener");
        }
    }
}
