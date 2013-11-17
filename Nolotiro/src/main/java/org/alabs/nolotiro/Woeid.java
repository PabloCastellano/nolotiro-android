package org.alabs.nolotiro;


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

}
