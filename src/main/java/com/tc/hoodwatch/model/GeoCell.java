package com.tc.hoodwatch.model;

public class GeoCell {
    private String geohash;
    private double lat;
    private double lon;
    private int count;

    public GeoCell(String geohash, double lat, double lon, int count) {
        this.geohash = geohash;
        this.lat = lat;
        this.lon = lon;
        this.count = count;
    }

    public String getGeohash() {
        return geohash;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getCount() {
        return count;
    }
}
