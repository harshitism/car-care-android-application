package com.car.maintenance.Database;

/**
 * Created by harshitg on 29-05-2018.
 */

public class Parts {
    String id;
    String name;
    Integer kms;
    String vehicle_id;
    Integer cost;
    String created_on;

    public Parts() {
    }

    public Parts(String id, String name, Integer kms, String vehicle_id, Integer cost, String created_on) {
        this.id = id;
        this.name = name;
        this.kms = kms;
        this.vehicle_id = vehicle_id;
        this.cost = cost;
        this.created_on = created_on;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getKms() {
        return kms;
    }

    public void setKms(Integer kms) {
        this.kms = kms;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
