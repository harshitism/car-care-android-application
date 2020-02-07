package com.car.maintenance.Database;

/**
 * Created by Harshit on 21-02-2018.
 */

public class Blog {
    Integer id;
    String title;
    String description;
    String link;
    String created_on;

    public Blog() {
    }

    public Blog(Integer id, String title, String description, String link, String created_on) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.created_on = created_on;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
