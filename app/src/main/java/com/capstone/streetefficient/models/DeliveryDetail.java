package com.capstone.streetefficient.models;

import java.util.Date;

public class DeliveryDetail {

    private String del_detail_id;
    private String del_item_id;
    private String delivery_status;
    private Date date;

    public DeliveryDetail() {
    }

    public DeliveryDetail(String del_detail_id, String del_item_id, String delivery_status, Date date) {
        this.del_detail_id = del_detail_id;
        this.del_item_id = del_item_id;
        this.delivery_status = delivery_status;
        this.date = date;
    }

    public String getDel_detail_id() {
        return del_detail_id;
    }

    public void setDel_detail_id(String del_detail_id) {
        this.del_detail_id = del_detail_id;
    }

    public String getDel_item_id() {
        return del_item_id;
    }

    public void setDel_item_id(String del_item_id) {
        this.del_item_id = del_item_id;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
