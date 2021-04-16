package com.capstone.streetefficient.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstone.streetefficient.R;

public class NotNextDialog extends DialogFragment {

    NotNextListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_not_next, container, false);
        Button YES = v.findViewById(R.id.not_continue);
        Button NO = v.findViewById(R.id.not_no);

        YES.setOnClickListener(v1 -> applyProceed(true));
        NO.setOnClickListener(v1 -> applyProceed(false));

        return v;
    }

    private void applyProceed(boolean proceed){
        listener.notNext(proceed);
        dismiss();
    }

    public interface NotNextListener {
        void notNext(boolean proceed);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NotNextListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ItineraryDialogListener");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Toast.makeText(getActivity(), "DISMISSED", Toast.LENGTH_SHORT).show();
        listener.notNext(false);
    }
}
