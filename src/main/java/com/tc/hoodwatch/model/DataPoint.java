package com.tc.hoodwatch.model;

public class DataPoint {
    private double lat;
    private double lon;

    private String category;
    private double score;

    public DataPoint(double lat, double lon, String category, double score) {
        this.lat = lat;
        this.lon = lon;
        this.category = category;
        this.score = score;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getCategory() {
        return category;
    }

    public double getScore() {
        return score;
    }
}
