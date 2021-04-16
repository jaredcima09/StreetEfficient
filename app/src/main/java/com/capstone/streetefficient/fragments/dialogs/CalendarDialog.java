package com.capstone.streetefficient.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.functions.Utilities;

import java.util.Calendar;

public class CalendarDialog extends DialogFragment {

    CalendarDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_calendar, null);

        CalendarView calendarView = v.findViewById(R.id.calendar_calendar);
        calendarView.setDate(Calendar.getInstance().getTimeInMillis(), false, true);
        calendarView.setOnDateChangeListener(dateClick);

        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private final CalendarView.OnDateChangeListener dateClick = (view, year, month, dayOfMonth) -> {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        listener.applyDate(Utilities.getSimpleDate(calendar.getTime()));
        dismiss();

    };

    public interface CalendarDialogListener{
        void applyDate(String date);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CalendarDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CalendarDialogListener");
        }
    }
}
