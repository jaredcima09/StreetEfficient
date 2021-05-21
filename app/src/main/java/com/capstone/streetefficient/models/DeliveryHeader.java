package com.capstone.streetefficient.models;

import java.io.Serializable;
import java.util.Date;

public class DeliveryHeader implements Serializable {
    private String item_id;
    private String rider_id;
    private String assignedBy;
    private String item_weight;
    private String del_item_id;
    private String del_date_sched_string;
    private String itemRecipientContactNumber;
    private Date date_assigned;
    private Date del_date_sched;


    public DeliveryHeader() {

    }



    public DeliveryHeader(String item_id, String rider_id, String assignedBy, String item_weight, String del_item_id, String del_date_sched_string, String itemRecipientContactNumber, Date date_assigned, Date del_date_sched) {
        this.item_id = item_id;
        this.rider_id = rider_id;
        this.assignedBy = assignedBy;
        this.item_weight = item_weight;
        this.del_item_id = del_item_id;
        this.del_date_sched_string = del_date_sched_string;
        this.itemRecipientContactNumber = itemRecipientContactNumber;
        this.date_assigned = date_assigned;
        this.del_date_sched = del_date_sched;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getRider_id() {
        return rider_id;
    }

    public void setRider_id(String rider_id) {
        this.rider_id = rider_id;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getItem_weight() {
        return item_weight;
    }

    public void setItem_weight(String item_weight) {
        this.item_weight = item_weight;
    }

    public String getDel_item_id() {
        return del_item_id;
    }

    public void setDel_item_id(String del_item_id) {
        this.del_item_id = del_item_id;
    }

    public String getDel_date_sched_string() {
        return del_date_sched_string;
    }

    public void setDel_date_sched_string(String del_date_sched_string) {
        this.del_date_sched_string = del_date_sched_string;
    }

    public String getItemRecipientContactNumber() {
        return itemRecipientContactNumber;
    }

    public void setItemRecipientContactNumber(String itemRecipientContactNumber) {
        this.itemRecipientContactNumber = itemRecipientContactNumber;
    }

    public Date getDate_assigned() {
        return date_assigned;
    }

    public void setDate_assigned(Date date_assigned) {
        this.date_assigned = date_assigned;
    }

    public Date getDel_date_sched() {
        return del_date_sched;
    }

    public void setDel_date_sched(Date del_date_sched) {
        this.del_date_sched = del_date_sched;
    }

    @Override
    public String toString() {
        return "item id: "+ item_id;
    }
}
