package com.example.swaroj;

public class coordinates{

    double latitude;
    double longitude;
    double altitude;

    public coordinates(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public coordinates() {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude(){ return altitude; }

}
