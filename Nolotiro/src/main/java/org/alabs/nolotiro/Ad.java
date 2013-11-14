package org.alabs.nolotiro;

import java.io.Serializable;

public class Ad implements Serializable {

    public static final String KEY = "AD";

    private int id;
    private String title;
    private String body;
    private String username;
    private int type; // =1
    private int woeid;
    private String date_created;
    private String photo;
    private String status;
    private int comments_enabled;

    public Ad() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWoeid() {
        return woeid;
    }

    public void setWoeid(int woeid) {
        this.woeid = woeid;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getComments_enabled() {
        return comments_enabled;
    }

    public void setComments_enabled(int comments_enabled) {
        this.comments_enabled = comments_enabled;
    }
}
