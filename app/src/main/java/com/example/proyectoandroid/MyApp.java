package com.example.proyectoandroid;

import android.app.Application;

import com.example.proyectoandroid.util.LocationTracker;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa el tracker con contexto de aplicación (una sola vez)
        LocationTracker.init(this);
    }
}
