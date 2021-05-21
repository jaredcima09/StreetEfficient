package com.capstone.streetefficient.singletons;

import com.capstone.streetefficient.models.DeliveryHeader;
import com.capstone.streetefficient.models.Item;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AssignedItemsHelper {

    private int counter;
    private static AssignedItemsHelper instance;

    private LatLng[] latLngs;
    private ArrayList<Item> items;
    private ArrayList<DeliveryHeader> deliveryHeaders;


    public void resetCounter() {
        counter = 0;
    }

    public void incrementCounter() {
        counter++;
    }

    private AssignedItemsHelper() {
        this.reset();
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

    public int indexOfWarehouse() {
        return latLngs.length - 1;
    }

    public LatLng getLatLngOfWarehouse() {
        return latLngs[latLngs.length - 1];
    }

    public int getCounter() {
        return counter;
    }

    public DeliveryHeader getDeliveryHeaderAtPosition(int position) {
        for (DeliveryHeader deliveryHeader : deliveryHeaders)
            if (deliveryHeader.getItem_id().equals(items.get(position).getItem_id()))
                return deliveryHeader;
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
        if (latLngs == null) latLngs = new LatLng[0];

        this.items.addAll(items);
        LatLng[] newLatlngs = new LatLng[this.items.size() + 1];
        for (int i = 0; i < latLngs.length; i++) {
            if (i == latLngs.length - 1) {
                newLatlngs[newLatlngs.length - 1] = latLngs[i];
                break;
            }
            newLatlngs[i] = latLngs[i];
        }
        latLngs = newLatlngs;
    }

    public ArrayList<DeliveryHeader> getDeliveryHeaders() {
        return deliveryHeaders;
    }

    public void addItems(ArrayList<Item> items, ArrayList<LatLng> latLngs) {
        int startpoint = this.latLngs.length - 1;
        int j = 0;
        System.out.println("NANAY ORIGINAL: " + Arrays.toString(this.latLngs));
        addItems(items);
        System.out.println("NANAY AFTER ADD: " + Arrays.toString(this.latLngs));
        for (int i = startpoint; i < this.latLngs.length - 1; i++) {
            this.latLngs[i] = latLngs.get(j++);
        }
        System.out.println("NANAY NEW: " + Arrays.toString(this.latLngs));
    }

    public void checkOrder() {
        for (int i = 0; i < deliveryHeaders.size(); i++) {
            if (!deliveryHeaders.get(i).getItem_id().equals(items.get(i).getItem_id())) {
                int indexOfItem = getIndexofItem(deliveryHeaders.get(i).getItem_id());
                Item holder = items.get(i);
                items.set(i, items.get(indexOfItem));
                items.set(indexOfItem, holder);

            }
        }
    }

    private int getIndexofItem(String item_id) {
        for (int i = 0; i < items.size(); i++) {
            if (item_id.equals(items.get(i).getItem_id())) return i;
        }
        return 0;
    }

    public boolean containsItem(String id) {
        for (Item item : items)
            if (item.getItem_id().equals(id)) return true;
        return false;
    }

    public boolean containsHeader(String id) {
        for (DeliveryHeader header : deliveryHeaders)
            if (header.getItem_id().equals(id)) return true;
        return false;
    }


    public void addItemHeader(Item item, DeliveryHeader header) {
        addDeliveryHeader(header);
        addItems(new ArrayList<>(Collections.singletonList(item)));
        checkOrder();
    }
}
