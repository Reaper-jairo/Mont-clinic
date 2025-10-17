package com.example.proyectoandroid;

import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa el tracker con contexto de aplicación (una sola vez)
        LocationTracker.init(this);
    }
}
