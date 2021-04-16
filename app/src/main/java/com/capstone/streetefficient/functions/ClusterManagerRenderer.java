package com.capstone.streetefficient.functions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.streetefficient.R;
import com.capstone.streetefficient.models.ItemLocationMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ItemLocationMarker> {

    private final IconGenerator iconGenerator;
    private final TextView textView;

    public ClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ItemLocationMarker> clusterManager) {
        super(context, map, clusterManager);

        iconGenerator = new IconGenerator(context.getApplicationContext());
        textView = new TextView(context.getApplicationContext());
        int markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image) - 10;
        int markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image) -10;
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));

        iconGenerator.setContentView(textView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ItemLocationMarker item, MarkerOptions markerOptions) {
        textView.setText(item.getTitle());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title("");
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ItemLocationMarker> cluster) {
        return false;
    }
}
