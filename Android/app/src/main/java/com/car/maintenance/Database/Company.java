package com.car.maintenance.Database;

/**
 * Created by harshitg on 24-05-2018.
 */

public class Company {
    String _id;
    String name;
    String logo;
    String type;

    public Company() {
    }

    public Company(String _id, String name, String logo, String type) {

        this._id = _id;
        this.name = name;
        this.logo = logo;
        this.type = type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
