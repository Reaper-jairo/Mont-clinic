package com.example.proyectoandroid.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Obtiene actualizaciones de ubicación y resuelve a dirección legible.
 * Vive toda la vida del proceso (Application) y comparte el último valor.
 */
public class LocationTracker {

    private static LocationTracker INSTANCE;

    public static void init(Context appContext) {
        if (INSTANCE == null) {
            INSTANCE = new LocationTracker(appContext.getApplicationContext());
        }
    }

    public static LocationTracker get() {
        if (INSTANCE == null) throw new IllegalStateException("LocationTracker no inicializado (llama a MyApp.onCreate)");
        return INSTANCE;
    }

    // ---- implementación ----
    private final Context appContext;
    private final FusedLocationProviderClient fused;
    private final LocationRequest request;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private Location lastLocation;
    private String lastAddress = "Ubicación: inicializando…";

    private final List<WeakReference<TextView>> listeners = new ArrayList<>();

    private final LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            if (result == null || result.getLastLocation() == null) return;
            lastLocation = result.getLastLocation();
            geocodeAsync(lastLocation);
        }
    };

    private LocationTracker(Context ctx) {
        appContext = ctx;
        fused = LocationServices.getFusedLocationProviderClient(appContext);
        request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
                .setMinUpdateIntervalMillis(5_000L)
                .setWaitForAccurateLocation(true)
                .build();
    }

    /** Llama cuando ya tienes permisos concedidos (p.ej. desde MainActivity). */
    public void start() {
        try {
            fused.requestLocationUpdates(request, callback, Looper.getMainLooper());
        } catch (SecurityException se) {
            Log.w("LocationTracker", "Sin permisos de ubicación.");
        }
    }

    public void stop() {
        fused.removeLocationUpdates(callback);
    }

    /** Adjunta un TextView para mostrar siempre la última dirección. */
    public void bindTextView(TextView tv) {
        // limpia referencias muertas
        for (int i = listeners.size() - 1; i >= 0; i--) {
            TextView ref = listeners.get(i).get();
            if (ref == null) listeners.remove(i);
        }
        listeners.add(new WeakReference<>(tv));
        // muestra lo último que tengamos (cache)
        main.post(() -> tv.setText(lastAddress));
    }

    private void notifyAllViews(String text) {
        lastAddress = text;
        for (int i = listeners.size() - 1; i >= 0; i--) {
            TextView tv = listeners.get(i).get();
            if (tv == null) {
                listeners.remove(i);
            } else {
                tv.setText(text);
            }
        }
    }

    private void geocodeAsync(Location loc) {
        if (loc == null) return;

        // API 33+ tiene callback asíncrono; para abajo usamos un hilo.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
            geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1, addresses -> {
                String pretty = formatAddress(addresses);
                main.post(() -> notifyAllViews(pretty));
            });
        } else {
            io.execute(() -> {
                try {
                    Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    String pretty = formatAddress(list);
                    main.post(() -> notifyAllViews(pretty));
                } catch (Exception e) {
                    main.post(() -> notifyAllViews("Ubicación: sin dirección"));
                }
            });
        }
    }

    private String formatAddress(List<Address> list) {
        if (list == null || list.isEmpty()) return "Ubicación: sin dirección";
        Address a = list.get(0);

        // Construye algo tipo: "Flores Doñigue 144, Comuna, Ciudad"
        String calle = safe(a.getThoroughfare());      // calle
        String numero = safe(a.getSubThoroughfare());  // número
        String comuna = safe(a.getSubLocality());      // comuna / barrio
        String ciudad = safe(a.getLocality());         // ciudad
        String admin = safe(a.getAdminArea());         // región
        String pais = safe(a.getCountryName());        // país

        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(calle)) sb.append(calle);
        if (!TextUtils.isEmpty(numero)) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(numero);
        }
        if (!TextUtils.isEmpty(comuna)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(comuna);
        } else if (!TextUtils.isEmpty(ciudad)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ciudad);
        }
        // si quedó muy corto, usa addressLine completa
        if (sb.length() < 5 && a.getMaxAddressLineIndex() >= 0) {
            String line = a.getAddressLine(0);
            if (!TextUtils.isEmpty(line)) return line;
        }
        // agrega región/país si quieres
        if (!TextUtils.isEmpty(admin)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(admin);
        }
        if (!TextUtils.isEmpty(pais)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(pais);
        }
        return sb.length() == 0 ? "Ubicación: sin dirección" : sb.toString();
    }

    private String safe(String s) { return s == null ? "" : s; }
}

