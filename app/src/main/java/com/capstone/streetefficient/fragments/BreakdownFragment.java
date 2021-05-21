package com.capstone.streetefficient.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.functions.PhysicsFunctions;
import com.capstone.streetefficient.functions.Utilities;
import com.capstone.streetefficient.models.RouteDetail;
import com.capstone.streetefficient.models.ScoreBreakdown;

import java.io.Serializable;

public class BreakdownFragment extends Fragment {

    private final boolean isArrived;
    private final ScoreBreakdown scoreBreakdown;

    public BreakdownFragment(ScoreBreakdown scoreBreakdown, boolean isArrived) {
        this.isArrived = isArrived;
        this.scoreBreakdown = scoreBreakdown;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_breakdown, container, false);

        TextView RouteID = v.findViewById(R.id.breakdown_id);
        TextView SPEED = v.findViewById(R.id.breakdown_speed);
        TextView ORIGIN = v.findViewById(R.id.breakdown_origin);
        TextView ActualTime = v.findViewById(R.id.breakdown_ata);
        TextView Message = v.findViewById(R.id.breakdown_message);
        TextView EstimatedTime = v.findViewById(R.id.breakdown_eta);
        TextView ScoreTime = v.findViewById(R.id.breakdown_score_time);
        TextView ScoreSpeed = v.findViewById(R.id.breakdown_score_speed);
        TextView TotalScore = v.findViewById(R.id.breakdown_score_total);
        TextView DESTINATION = v.findViewById(R.id.breakdown_destination);
        TextView DistanceTravelled = v.findViewById(R.id.breakdown_distance);

        ORIGIN.append(Utilities.italicizeText(scoreBreakdown.getOrigin()));
        RouteID.append(Utilities.italicizeText(scoreBreakdown.getRouteID()));
        ScoreTime.append(Utilities.assessScore(scoreBreakdown.getScoreTime()));
        ScoreSpeed.append(Utilities.assessScore(scoreBreakdown.getScoreSpeed()));
        TotalScore.append(Utilities.assessScore(scoreBreakdown.getTotalScore()));
        DESTINATION.append(Utilities.italicizeText(scoreBreakdown.getDestination()));
        DistanceTravelled.append(Utilities.italicizeText(scoreBreakdown.getDistanceTravelled()));

        if (!isArrived) {
            Message.setVisibility(View.VISIBLE);
            SPEED.append(italicizeText(Color.RED));
            ActualTime.append(italicizeText(Color.BLACK));
            EstimatedTime.append(italicizeText(Color.BLACK));
        } else {
            SPEED.append(Utilities.assessSpeed(scoreBreakdown.getSpeed()));
            ActualTime.append(Utilities.assessTime(scoreBreakdown.getActualTime(), scoreBreakdown.getEstimatedTime()));
            EstimatedTime.append(Utilities.italicizeText(PhysicsFunctions.decimalToHour(scoreBreakdown.getEstimatedTime())));
        }
        return v;
    }

    private SpannableStringBuilder italicizeText(int i) {
        SpannableStringBuilder str = new SpannableStringBuilder("\nN/A");
        str.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(i), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
