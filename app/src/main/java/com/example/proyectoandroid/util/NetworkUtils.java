package com.example.proyectoandroid.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * Utilidades para verificar el estado de la conexión de red
 * 
 * Nota: Usa solo APIs modernas (NetworkCapabilities) para evitar deprecaciones.
 * Para versiones antiguas de Android, se usa un enfoque compatible.
 */
public class NetworkUtils {

    /**
     * Verifica si hay conexión a internet disponible
     * 
     * @param context Contexto de la aplicación
     * @return true si hay conexión a internet, false en caso contrario
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        // minSdk es 25, así que podemos usar APIs modernas directamente
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        if (capabilities == null) {
            return false;
        }

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    /**
     * Verifica si hay conexión WiFi disponible
     * 
     * @param context Contexto de la aplicación
     * @return true si hay conexión WiFi, false en caso contrario
     */
    public static boolean isWifiAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        // minSdk es 25, así que podemos usar APIs modernas directamente
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        if (capabilities == null) {
            return false;
        }

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    /**
     * Verifica si hay conexión de datos móviles disponible
     * 
     * @param context Contexto de la aplicación
     * @return true si hay conexión de datos móviles, false en caso contrario
     */
    public static boolean isMobileDataAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        // minSdk es 25, así que podemos usar APIs modernas directamente
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        if (capabilities == null) {
            return false;
        }

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }
}

