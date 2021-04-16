package com.capstone.streetefficient.fragments;

import android.os.Bundle;
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

import java.io.Serializable;

public class BreakdownFragment extends Fragment implements Serializable {


    private final String destination, distanceTravelled, origin, routeID;
    private final double speed, scoreTime, scoreSpeed, actualTime, totalScore, estimatedTime;



    public BreakdownFragment(double actualTime, String destination, String distanceTravelled, double estimatedTime, String origin,
                             String routeID, double scoreSpeed, double scoreTime, double speed, double totalScore) {
        this.speed = speed;
        this.origin = origin;
        this.routeID = routeID;
        this.scoreTime = scoreTime;
        this.totalScore = totalScore;
        this.scoreSpeed = scoreSpeed;
        this.actualTime = actualTime;
        this.destination = destination;
        this.estimatedTime = estimatedTime;
        this.distanceTravelled = distanceTravelled;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_breakdown, container, false);

        TextView RouteID = v.findViewById(R.id.breakdown_id);
        TextView SPEED = v.findViewById(R.id.breakdown_speed);
        TextView ORIGIN = v.findViewById(R.id.breakdown_origin);
        TextView ActualTime = v.findViewById(R.id.breakdown_ata);
        TextView EstimatedTime = v.findViewById(R.id.breakdown_eta);
        TextView ScoreTime = v.findViewById(R.id.breakdown_score_time);
        TextView ScoreSpeed = v.findViewById(R.id.breakdown_score_speed);
        TextView TotalScore = v.findViewById(R.id.breakdown_score_total);
        TextView DESTINATION = v.findViewById(R.id.breakdown_destination);
        TextView DistanceTravelled = v.findViewById(R.id.breakdown_distance);

        SPEED.append(Utilities.assessSpeed(speed));
        ORIGIN.append(Utilities.italicizeText(origin));
        RouteID.append(Utilities.italicizeText(routeID));
        ScoreTime.append(Utilities.assessScore(scoreTime));
        ScoreSpeed.append(Utilities.assessScore(scoreSpeed));
        TotalScore.append(Utilities.assessScore(totalScore));
        DESTINATION.append(Utilities.italicizeText(destination));
        ActualTime.append(Utilities.assessTime(actualTime, estimatedTime));
        DistanceTravelled.append(Utilities.italicizeText(distanceTravelled));
        EstimatedTime.append(Utilities.italicizeText(PhysicsFunctions.decimalToHour(estimatedTime)));

        return v;
    }
}
