package com.capstone.streetefficient.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.capstone.streetefficient.R;

public class UnsuccessfulDialog extends DialogFragment {

    private UnsuccessfulDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_unsucessful, null);
        CardView returnSender = v.findViewById(R.id.return_sender);
        CardView returnWarehouse = v.findViewById(R.id.return_warehouse);
        returnSender.setOnClickListener(returnSenderClick);
        returnWarehouse.setOnClickListener(returnWarehouseClick);
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private final View.OnClickListener returnWarehouseClick = v -> {
        Apply("RW");
    };

    private final View.OnClickListener returnSenderClick = v -> {
        Apply("RS");
    };

    public interface UnsuccessfulDialogListener {
        void applyDialog(String status);
    }

    private void Apply(String status) {
        listener.applyDialog(status);
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (UnsuccessfulDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement UnsuccessfulDialogListener");
        }
    }
}
