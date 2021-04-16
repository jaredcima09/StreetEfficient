package com.capstone.streetefficient.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ItemLocationMarker implements ClusterItem {

    private final LatLng ITEM_LATLNG;
    private final int SEQUENCE_INDEX, LATLNG_INDEX;

    public ItemLocationMarker(LatLng ITEM_LATLNG, int SEQUENCE_INDEX, int LATLNG_INDEX) {
        this.ITEM_LATLNG = ITEM_LATLNG;
        this.SEQUENCE_INDEX = SEQUENCE_INDEX;
        this.LATLNG_INDEX = LATLNG_INDEX;
    }

    @Override
    public LatLng getPosition() {
        return ITEM_LATLNG;
    }

    @Override
    public String getTitle() {
        return String.valueOf(SEQUENCE_INDEX);
    }

    @Override
    public String getSnippet() {
        return String.valueOf(LATLNG_INDEX);
    }
}
