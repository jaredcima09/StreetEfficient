package com.capstone.streetefficient.functions;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.capstone.streetefficient.models.Item;
import com.capstone.streetefficient.models.RouteDetail;
import com.capstone.streetefficient.models.RouteHeader;
import com.capstone.streetefficient.singletons.AssignedItemsHelper;
import com.capstone.streetefficient.singletons.SequencedRouteHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utilities {

    public static SpannableStringBuilder italicizeText(String s) {
        SpannableStringBuilder str = new SpannableStringBuilder("\n" + s);
        str.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    public static double roundOff(double number) {
        return (double) Math.round(number * 100) / 100;
    }

    public static String getAddress(int itemNumber) {
        if (itemNumber == AssignedItemsHelper.getInstance().getLatLngs().length - 1)
            return "BRANCH WAREHOUSE";
        Item item = AssignedItemsHelper.getInstance().getItemAtPosition(itemNumber);
        return item.getItemRecipientAddressStreet() + ", " + item.getItemRecipientAddressBarangay() + ", " + item.getItemRecipientAddressProvince();
    }

    public static double assignedWeight(ArrayList<Item> items) {
        double weight = 0;
        for (Item item : items) weight += Double.parseDouble(item.getItemweight());
        return weight;
    }

    public static String getSimpleDate(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        return dateFormat.format(date);
    }

    public static SpannableStringBuilder assessSpeed(Double speed) {
        String str = "\n" + (int) Math.ceil(speed) + " KM/H";
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder((str));
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (speed >= 60)
            stringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else
            stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    public static SpannableStringBuilder assessScore(Double score) {
        String str = "\n" + roundOff(score) + " %";
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder((str));
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (score >= 70)
            stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else if (score >= 50)
            stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF781F")), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else
            stringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return stringBuilder;
    }

    public static SpannableStringBuilder assessTime(double actual, double estimated) {
        String str = "\n" + PhysicsFunctions.decimalToHour(actual);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder((str));
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (actual > estimated)
            stringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else
            stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return stringBuilder;
    }

    public static String getItemID(int origin) {
        AssignedItemsHelper assignedItemsHelper = AssignedItemsHelper.getInstance();
        if (origin == assignedItemsHelper.getLatLngs().length - 1) return "BRANCH WAREHOUSE";
        return assignedItemsHelper.getItemAtPosition(origin).getItem_id();
    }
}
