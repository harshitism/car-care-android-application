package com.car.maintenance.Database;

/**
 * Created by harshitg on 24-05-2018.
 */

public class Vehicle {

    String _id;
    String type; // Car or Bike
    String company;
    String model;
    String fuel;
    Integer year;
    String registration_number;
    String daily_avg; // KMS
    Integer odometer; // KMS
    Integer last_service_odometer;
    String last_service_time;

    public Vehicle() {
    }

    public Vehicle(String _id, String type, String company, String model, String fuel, Integer year, String registration_number, String daily_avg, Integer odometer, Integer last_service_odometer, String last_service_time) {
        this._id = _id;
        this.type = type;
        this.company = company;
        this.model = model;
        this.fuel = fuel;
        this.year = year;
        this.registration_number = registration_number;
        this.daily_avg = daily_avg;
        this.odometer = odometer;
        this.last_service_odometer = last_service_odometer;
        this.last_service_time = last_service_time;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    public String getDaily_avg() {
        return daily_avg;
    }

    public void setDaily_avg(String daily_avg) {
        this.daily_avg = daily_avg;
    }

    public Integer getOdometer() {
        return odometer;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public Integer getLast_service_odometer() {
        return last_service_odometer;
    }

    public void setLast_service_odometer(Integer last_service_odometer) {
        this.last_service_odometer = last_service_odometer;
    }

    public String getLast_service_time() {
        return last_service_time;
    }

    public void setLast_service_time(String last_service_time) {
        this.last_service_time = last_service_time;
    }
}
