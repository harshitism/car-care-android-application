package com.car.maintenance.Database;

/**
 * Created by harshitg on 29-05-2018.
 */

public class Transaction {
    String id;
    String type; // REFUEL / EXPENSE
    Integer odometer;
    String part_id;
    String created_on;
    String date;
    Integer amount;
    Double latitude;
    Double longitude;
    String note;

    public Transaction() {
    }

    public Transaction(String id, String type, Integer odometer, String part_id, String created_on, String date, Integer amount, Double latitude, Double longitude, String note) {
        this.id = id;
        this.type = type;
        this.odometer = odometer;
        this.part_id = part_id;
        this.created_on = created_on;
        this.date = date;
        this.amount = amount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOdometer() {
        return odometer;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public String getPart_id() {
        return part_id;
    }

    public void setPart_id(String part_id) {
        this.part_id = part_id;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
