package com.halanx.userapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by samarthgupta on 25/08/17.
 */

public class SubscriptionInfoGet {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("QuantityPerDay")
    @Expose
    private Double quantityPerDay;
    @SerializedName("CostPerDay")
    @Expose
    private Double costPerDay;
    @SerializedName("DeliveriesLeft")
    @Expose
    private Integer deliveriesLeft;
    @SerializedName("StartTime")
    @Expose
    private String startTime;
    @SerializedName("EndTime")
    @Expose
    private String endTime;
    @SerializedName("OnMonday")
    @Expose
    private Boolean onMonday;
    @SerializedName("OnTuesday")
    @Expose
    private Boolean onTuesday;
    @SerializedName("OnWednesday")
    @Expose
    private Boolean onWednesday;
    @SerializedName("OnThursday")
    @Expose
    private Boolean onThursday;
    @SerializedName("OnFriday")
    @Expose
    private Boolean onFriday;
    @SerializedName("OnSaturday")
    @Expose
    private Boolean onSaturday;
    @SerializedName("OnSunday")
    @Expose
    private Boolean onSunday;
    @SerializedName("StartDate")
    @Expose
    private String startDate;
    @SerializedName("TemporaryRemoved")
    @Expose
    private Boolean temporaryRemoved;
    @SerializedName("PermanentRemoved")
    @Expose
    private Boolean permanentRemoved;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("Item")
    @Expose
    private ProductInfo item;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getQuantityPerDay() {
        return quantityPerDay;
    }

    public void setQuantityPerDay(Double quantityPerDay) {
        this.quantityPerDay = quantityPerDay;
    }

    public Double getCostPerDay() {
        return costPerDay;
    }

    public void setCostPerDay(Double costPerDay) {
        this.costPerDay = costPerDay;
    }

    public Integer getDeliveriesLeft() {
        return deliveriesLeft;
    }

    public void setDeliveriesLeft(Integer deliveriesLeft) {
        this.deliveriesLeft = deliveriesLeft;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Boolean getOnMonday() {
        return onMonday;
    }

    public void setOnMonday(Boolean onMonday) {
        this.onMonday = onMonday;
    }

    public Boolean getOnTuesday() {
        return onTuesday;
    }

    public void setOnTuesday(Boolean onTuesday) {
        this.onTuesday = onTuesday;
    }

    public Boolean getOnWednesday() {
        return onWednesday;
    }

    public void setOnWednesday(Boolean onWednesday) {
        this.onWednesday = onWednesday;
    }

    public Boolean getOnThursday() {
        return onThursday;
    }

    public void setOnThursday(Boolean onThursday) {
        this.onThursday = onThursday;
    }

    public Boolean getOnFriday() {
        return onFriday;
    }

    public void setOnFriday(Boolean onFriday) {
        this.onFriday = onFriday;
    }

    public Boolean getOnSaturday() {
        return onSaturday;
    }

    public void setOnSaturday(Boolean onSaturday) {
        this.onSaturday = onSaturday;
    }

    public Boolean getOnSunday() {
        return onSunday;
    }

    public void setOnSunday(Boolean onSunday) {
        this.onSunday = onSunday;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Boolean getTemporaryRemoved() {
        return temporaryRemoved;
    }

    public void setTemporaryRemoved(Boolean temporaryRemoved) {
        this.temporaryRemoved = temporaryRemoved;
    }

    public Boolean getPermanentRemoved() {
        return permanentRemoved;
    }

    public void setPermanentRemoved(Boolean permanentRemoved) {
        this.permanentRemoved = permanentRemoved;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ProductInfo getItem() {
        return item;
    }

    public void setItem(ProductInfo item) {
        this.item = item;
    }
}
