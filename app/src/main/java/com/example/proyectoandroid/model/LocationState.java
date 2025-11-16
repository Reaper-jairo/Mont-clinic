package com.example.proyectoandroid.model;

import android.location.Location;

/**
 * Modelo para el estado de ubicación
 */
public class LocationState {
    private Location location;
    private String address;
    private boolean isLoading;
    private String error;

    public LocationState() {
        this.isLoading = false;
    }

    public LocationState(Location location, String address) {
        this.location = location;
        this.address = address;
        this.isLoading = false;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

