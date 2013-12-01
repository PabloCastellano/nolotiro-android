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
