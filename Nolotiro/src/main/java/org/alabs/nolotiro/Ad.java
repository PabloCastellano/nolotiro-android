/*
 * Copyright (c) 2013 "Pablo Castellano <pablo@anche.no>"
 * Copyright (c) 2013 "Eugenio Cano-Manuel Mendoza <eugeniocanom@gmail.com>"
 * Nolotiro App [http://nolotiro.org]
 *
 * This file is part of nolotiro-android.
 *
 * nolotiro-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.alabs.nolotiro;

import java.io.Serializable;

public class Ad implements Serializable {

    public static enum Type { GIVE, WANT };
    public static enum Status { AVAILABLE, BOOKED, DELIVERED };

    private int id;
    private String title;
    private String body;
    private String username;
    private Ad.Type type;
    private int woeid;
    private String date_created;
    private String image_file_name;
    private Ad.Status status;
    private boolean comments_enabled;
    private boolean favorite;

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

    public Ad.Type getType() {
        return type;
    }

    public void setType(Ad.Type type) {
        this.type = type;
    }

    public int getWoeid() {
        return woeid;
    }

    public void setWoeid(int woeid) {
        this.woeid = woeid;
    }

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

    public Ad.Status getStatus() {
        return status;
    }

    public void setStatus(Ad.Status status) {
        this.status = status;
    }

    public boolean isCommentsEnabled() {
        return comments_enabled;
    }

    public void setCommentsEnabled(boolean comments_enabled) {
        this.comments_enabled = comments_enabled;
    }

    public void setFavorite(boolean fav) {
        favorite = fav;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String toString() {
        return "Ad{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", username='" + username + '\'' +
                ", type=" + type +
                ", woeid=" + woeid +
                ", date_created='" + date_created + '\'' +
                ", image_file_name='" + image_file_name + '\'' +
                ", status=" + status +
                ", comments_enabled=" + comments_enabled +
                ", favorite=" + favorite +
                '}';
    }
}
