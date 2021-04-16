package com.capstone.streetefficient.singletons;

import com.capstone.streetefficient.models.DeliveryHeader;
import com.capstone.streetefficient.models.Item;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AssignedItemsHelper {

    private int counter;
    private static AssignedItemsHelper instance;

    private LatLng[] latLngs;
    private ArrayList<Item> items;
    private ArrayList<DeliveryHeader> deliveryHeaders;


    private AssignedItemsHelper() {

    }

    public static AssignedItemsHelper getInstance() {
        if (instance == null) {
            instance = new AssignedItemsHelper();
        }
        return instance;
    }

    public Item getItemAtPosition(int i) {
        return items.get(i);
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }

    public LatLng[] getLatLngs() {
        return latLngs;
    }


    public LatLng getLatLngAtPosition(int position) {
        return latLngs[position];
    }

//    public LatLng getLastLatLng() {
//        return latLngs[latLngs.length - 1];
//    }

    public int getCounter() {
        return counter;
    }

    public DeliveryHeader getDeliveryHeaderAtPosition(int position) {
        return deliveryHeaders.get(position);
    }

    public void addDeliveryHeader(DeliveryHeader deliveryHeader) {
        deliveryHeaders.add(deliveryHeader);
    }

    public void addLatLng(int position, LatLng latLng) {
        latLngs[position] = latLng;
    }

    public void addItem(Item item) {
        items.add(item);
        counter++;
    }

    public void setLastIndex(LatLng latLng) {
        if (latLngs == null) latLngs = new LatLng[items.size() + 1];
        latLngs[latLngs.length - 1] = latLng;
    }

    public void reset() {
        counter = 0;
        latLngs = null;
        items = new ArrayList<>();
        deliveryHeaders = new ArrayList<>();
    }

    public void setInstance(AssignedItemsHelper instance) {
        AssignedItemsHelper.instance = instance;
    }

    public void addItems(ArrayList<Item> items) {
        if(latLngs == null) latLngs = new LatLng[0];
        LatLng[] newLatlngs = new LatLng[latLngs.length + items.size()];
        for (int i = 0; i < latLngs.length; i++) {
            if (i == latLngs.length - 1) {
                newLatlngs[newLatlngs.length - 1] = latLngs[i];
                break;
            }
            newLatlngs[i] = latLngs[i];
        }
        latLngs = newLatlngs;
        this.items.addAll(items);
    }
}
