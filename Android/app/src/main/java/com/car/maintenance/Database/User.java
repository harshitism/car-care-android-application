package com.car.maintenance.Database;

/**
 * Created by harshitgupta on 22/09/17.
 */

public class User {

    String uid;
    String name;
    String email;
    String mobile;
    String pic;
    Boolean is_completed;
    String fcm_id;
    String google_id;
    String device_id;
    String app_version;
    String created_on;
    String last_usage;

    public User() {
    }

    public User(String uid, String name, String email, String mobile, String pic, Boolean is_completed, String fcm_id, String google_id, String device_id, String app_version, String created_on, String last_usage) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.pic = pic;
        this.is_completed = is_completed;
        this.fcm_id = fcm_id;
        this.google_id = google_id;
        this.device_id = device_id;
        this.app_version = app_version;
        this.created_on = created_on;
        this.last_usage = last_usage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Boolean getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(Boolean is_completed) {
        this.is_completed = is_completed;
    }

    public String getFcm_id() {
        return fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        this.fcm_id = fcm_id;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getLast_usage() {
        return last_usage;
    }

    public void setLast_usage(String last_usage) {
        this.last_usage = last_usage;
    }
}
