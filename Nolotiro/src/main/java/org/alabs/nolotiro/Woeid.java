package org.alabs.nolotiro;

import android.content.Context;

import org.alabs.nolotiro.db.DbAdapter;

import java.util.HashMap;
import java.util.Map;

public class Woeid {

    private int id;
    private String name;
    private String admin;
    private String country;

    public Woeid (int _id, String _name, String _admin, String _country) {
        id = _id;
        name = _name;
        admin = _admin;
        country = _country;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    public String getCountry() {
        return country;
    }

    public String toString() {
        return name + ", " + admin + ", " + country;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Woeid woeid = (Woeid) o;

        if (id != woeid.id) return false;

        return true;
    }

    public int hashCode() {
        return id;
    }

    public static class Manager {

        private static final Woeid.Manager INSTANCE = new Manager();
        private Map<Integer, Woeid> cache;
        private DbAdapter dba = null;

        private Manager () {
            cache = new HashMap<Integer, Woeid>();
        }

        public static Manager getInstance() {
            return INSTANCE;
        }

        public void setContext(Context ctx) {
            //dba = new DbAdapter(ctx);
        }

        public String getName(int id) {
            // retrieve from cache
            if(cache.containsKey(id)) {
                return cache.get(id).getName();
            }

            // retrieve from DB
            //if (dba != null) {
            //    dba.
            //}
            Woeid woeid = new Woeid(1, "", "", "");

            // retrieve from API


            cache.put(id, woeid);
            return woeid.getName();
        }
    }
}
