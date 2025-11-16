package com.example.proyectoandroid.repository;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.location.CurrentLocationRequest;

/**
 * Repositorio para manejar operaciones de ubicación
 */
public class LocationRepository {
    private static LocationRepository instance;
    private final FusedLocationProviderClient fusedLocationClient;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private CancellationTokenSource locationCts;

    private LocationRepository(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public static synchronized LocationRepository getInstance(Context context) {
        if (instance == null) {
            instance = new LocationRepository(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Obtiene la última ubicación conocida
     */
    public LiveData<Location> getLastLocation() {
        MutableLiveData<Location> result = new MutableLiveData<>();
        
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            result.setValue(location);
                            currentLocation.setValue(location);
                        } else {
                            // Si no hay cache, obtener ubicación actual
                            getCurrentLocation(result);
                        }
                    })
                    .addOnFailureListener(e -> {
                        result.setValue(null);
                    });
        } catch (SecurityException e) {
            result.setValue(null);
        }
        
        return result;
    }

    /**
     * Obtiene la ubicación actual (fresca)
     */
    public LiveData<Location> getCurrentLocation() {
        MutableLiveData<Location> result = new MutableLiveData<>();
        getCurrentLocation(result);
        return result;
    }

    private void getCurrentLocation(MutableLiveData<Location> result) {
        if (locationCts != null) {
            locationCts.cancel();
        }
        locationCts = new CancellationTokenSource();

        CurrentLocationRequest request = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(0)     // no usar cache
                .setDurationMillis(10_000L)   // timeout 10s
                .build();

        try {
            fusedLocationClient.getCurrentLocation(request, locationCts.getToken())
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            result.setValue(location);
                            currentLocation.setValue(location);
                        } else {
                            result.setValue(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        result.setValue(null);
                    });
        } catch (SecurityException e) {
            result.setValue(null);
        }
    }

    /**
     * Obtiene la ubicación actual almacenada
     */
    public LiveData<Location> getCurrentLocationLiveData() {
        return currentLocation;
    }

    /**
     * Cancela las solicitudes de ubicación pendientes
     */
    public void cancelLocationRequests() {
        if (locationCts != null) {
            locationCts.cancel();
            locationCts = null;
        }
    }
}

