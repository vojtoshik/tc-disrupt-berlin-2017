package com.tc.hoodwatch.model;

public class DataPoint {
    private String name;

    private double lat;
    private double lon;

    private short category;
    private double score;


    public DataPoint(String name, double lat, double lon, short category, double score) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.category = category;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public short getCategory() {
        return category;
    }

    public double getScore() {
        return score;
    }
}
