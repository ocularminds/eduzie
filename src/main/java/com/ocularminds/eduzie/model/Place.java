package com.ocularminds.eduzie.model;

import java.io.Serializable;

public class Place implements Serializable, Comparable<Place> {

    private long id;
    private String name;
    private String userId;
    private double longitude;
    private double latitude;
    private double longitude2;
    private double latitude2;
    private double distance;
    private String travel;
    private String time;

    public Place() {
    }

    public Place(long id, String name, double latitude, double longitude) {

        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude2() {
        return this.latitude2;
    }

    public void setLatitude2(double latitude2) {
        this.latitude2 = latitude2;
    }

    public double getLongitude2() {
        return this.longitude2;
    }

    public void setLongitude2(double longitude2) {
        this.longitude2 = longitude2;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof Place)
                && (((Place) o).getId() == this.getId())) ? true : false;
    }

    @Override
    public int hashCode() {
        return (int) (17 * id) * name.hashCode();
    }

    @Override
    public int compareTo(Place other) {
        return (this.getId() < other.getId()) ? -1 : 1;
    }
}
