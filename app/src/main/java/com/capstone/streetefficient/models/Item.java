package com.capstone.streetefficient.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Item implements Serializable {
    private String courier_id;
    private Date date_encoded;
    private String encodedBy;
    private String itemCOD;
    private String itemRecipientAddressBarangay;
    private String itemRecipientAddressCity;
    private String itemRecipientAddressProvince;
    private String itemRecipientAddressStreet;
    private String itemRecipientBranch;
    private String itemRecipientContactNumber;
    private String itemRecipientname;
    private String itemSenderAddressBarangay;
    private String itemSenderAddressCity;
    private String itemSenderAddressProvince;
    private String itemSenderAddressStreet;
    private String itemSenderBranch;
    private String itemSenderContactNumber;
    private String itemSendername;
    private String item_id;
    private String itemqty;
    private String itemweight;
    private String status;
    private boolean reviewed = false;

    public Item() {
    }

    public Item(String courier_id, Date date_encoded, String encodedBy, String itemCOD, String itemRecipientAddressBarangay, String itemRecipientAddressCity,
                String itemRecipientAddressProvince, String itemRecipientAddressStreet, String itemRecipientBranch, String itemRecipientContactNumber,
                String itemRecipientname, String itemSenderAddressBarangay, String itemSenderAddressCity, String itemSenderAddressProvince, String itemSenderAddressStreet,
                String itemSenderBranch, String itemSenderContactNumber, String itemSendername, String item_id, String itemqty, String itemweight, String status) {
        this.courier_id = courier_id;
        this.date_encoded = date_encoded;
        this.encodedBy = encodedBy;
        this.itemCOD = itemCOD;
        this.itemRecipientAddressBarangay = itemRecipientAddressBarangay;
        this.itemRecipientAddressCity = itemRecipientAddressCity;
        this.itemRecipientAddressProvince = itemRecipientAddressProvince;
        this.itemRecipientAddressStreet = itemRecipientAddressStreet;
        this.itemRecipientBranch = itemRecipientBranch;
        this.itemRecipientContactNumber = itemRecipientContactNumber;
        this.itemRecipientname = itemRecipientname;
        this.itemSenderAddressBarangay = itemSenderAddressBarangay;
        this.itemSenderAddressCity = itemSenderAddressCity;
        this.itemSenderAddressProvince = itemSenderAddressProvince;
        this.itemSenderAddressStreet = itemSenderAddressStreet;
        this.itemSenderBranch = itemSenderBranch;
        this.itemSenderContactNumber = itemSenderContactNumber;
        this.itemSendername = itemSendername;
        this.item_id = item_id;
        this.itemqty = itemqty;
        this.itemweight = itemweight;
        this.status = status;
    }

    public String getCourier_id() {
        return courier_id;
    }

    public Date getDate_encoded() {
        return date_encoded;
    }

    public String getEncodedBy() {
        return encodedBy;
    }

    public String getItemCOD() {
        return itemCOD;
    }

    public String getItemRecipientAddressBarangay() {
        return itemRecipientAddressBarangay;
    }

    public String getItemRecipientAddressCity() {
        return itemRecipientAddressCity;
    }

    public String getItemRecipientAddressProvince() {
        return itemRecipientAddressProvince;
    }

    public String getItemRecipientAddressStreet() {
        return itemRecipientAddressStreet;
    }

    public String getItemRecipientBranch() {
        return itemRecipientBranch;
    }

    public String getItemRecipientContactNumber() {
        return itemRecipientContactNumber;
    }

    public String getItemRecipientname() {
        return itemRecipientname;
    }

    public String getItemSenderAddressBarangay() {
        return itemSenderAddressBarangay;
    }

    public String getItemSenderAddressCity() {
        return itemSenderAddressCity;
    }

    public String getItemSenderAddressProvince() {
        return itemSenderAddressProvince;
    }

    public String getItemSenderAddressStreet() {
        return itemSenderAddressStreet;
    }

    public String getItemSenderBranch() {
        return itemSenderBranch;
    }

    public String getItemSenderContactNumber() {
        return itemSenderContactNumber;
    }

    public String getItemSendername() {
        return itemSendername;
    }

    public String getItem_id() {
        return item_id;
    }

    public String getItemqty() {
        return itemqty;
    }

    public String getItemweight() {
        return itemweight;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "item id: "+ item_id;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(item_id, item.item_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item_id);
    }
}
