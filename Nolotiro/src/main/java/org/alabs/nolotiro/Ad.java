package org.alabs.nolotiro;

import android.util.Log;

import org.alabs.nolotiro.exceptions.AdStatusException;
import org.alabs.nolotiro.exceptions.AdTypeException;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ad implements Serializable {

    public static final class Type {
        public static final int GIVE = 0x0;
        public static final int WANT = 0x1;

        public static final int fromString(String str) throws AdTypeException {
            if (str.toLowerCase().equals("busco")) {
                return Ad.Type.WANT;
            } else if (str.toLowerCase().equals("regalo")) {
                return Ad.Type.GIVE;
            } else {
                throw new AdTypeException("Invalid Ad.Type value: " + str);
            }
        }
    }

    public static final class Status {
        public static final int AVAILABLE = 0x0;
        public static final int BOOKED = 0x1;
        public static final int DELIVERED = 0x2;

        public static final int fromString(String str) throws AdStatusException {
            if (str.toLowerCase().equals("available")) {
                return Ad.Status.AVAILABLE;
            } else if (str.toLowerCase().equals("booked")) {
                return Ad.Status.BOOKED;
            } else if (str.toLowerCase().equals("____")) {
                return Ad.Status.DELIVERED;
            } else {
                throw new AdStatusException("Invalid Ad.Status value: " + str);
            }
        }
    }

    public static final String KEY = "AD";

    private int id;
    private String title;
    private String body;
    private String username;
    private int type;
    private int woeid;
    //private Date date_created;
    private String date_created;
    private String image_file_name;
    private int status;
    private boolean comments_enabled;
    
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

    public void setType(int type) throws AdTypeException {
        if (type != Type.GIVE && type != Type.WANT) {
            throw new AdTypeException("Invalid Ad.Type value: " + type);
        }
        this.type = type;
    }

    public int getWoeid() {
        return woeid;
    }

    public void setWoeid(int woeid) {
        this.woeid = woeid;
    }

    /*
    public Date getDate() {
        return date_created;
    }

    public void setDate(Date date_created) {
        this.date_created = date_created;
    }
    */

    public String getDate() {
        return date_created;
    }

    public void setDate(String date_created) {
        this.date_created = date_created;
    }


    public String getImageFilename() {
        return image_file_name;
    }

    public void setImageFilename(String image_file_name) {
        this.image_file_name = image_file_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) throws AdStatusException {
        if (status != Status.AVAILABLE && status != Status.BOOKED && status != Status.DELIVERED) {
            throw new AdStatusException("Invalid Ad.Status value: " + type);
        }
        this.status = status;
    }

    public boolean isCommentsEnabled() {
        return comments_enabled;
    }

    public void setCommentsEnabled(boolean comments_enabled) {
        this.comments_enabled = comments_enabled;
    }
}
