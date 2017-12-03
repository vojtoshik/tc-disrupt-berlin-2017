package com.tc.hoodwatch.model;

public class DataPoint1 {
    private double lat;
    private double lon;

    private long shops;
    private long transport;
    private long food;
    private long sport;

    public DataPoint1(double lat, double lon, long shops, long transport, long food, long sport) {
        this.lat = lat;
        this.lon = lon;
        this.shops = shops;
        this.transport = transport;
        this.food = food;
        this.sport = sport;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getShops() {
        return shops;
    }

    public long getTransport() {
        return transport;
    }

    public long getFood() {
        return food;
    }

    public long getSport() {
        return sport;
    }
}
