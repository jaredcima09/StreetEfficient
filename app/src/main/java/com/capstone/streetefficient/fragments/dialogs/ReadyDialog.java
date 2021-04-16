package com.capstone.streetefficient.fragments.dialogs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.os.Bundle;
import android.app.Dialog;
import android.widget.Button;
import android.graphics.Color;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.graphics.drawable.ColorDrawable;

import com.capstone.streetefficient.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ReadyDialog extends DialogFragment {

    private static final int AUTO_DISMISS_MILLIS = 5000;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_ready, null);
        Button button = v.findViewById(R.id.btn_dismiss);
        button.setOnClickListener(v1 -> dismiss());
        builder.setView(v);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

                button.setText(String.format(Locale.getDefault(), "%s (%d)", "DISMISS", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1));
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing()) dialog.dismiss();
            }
        }.start();
        return dialog;
    }
}
