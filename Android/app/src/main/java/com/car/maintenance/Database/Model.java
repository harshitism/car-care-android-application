package com.car.maintenance.Database;

/**
 * Created by harshitg on 24-05-2018.
 */

public class Model {
    String _id;
    String name;
    String image;

    public Model() {
    }

    public Model(String _id, String name, String image) {
        this._id = _id;
        this.name = name;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
