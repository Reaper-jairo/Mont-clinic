package com.example.proyectoandroid;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class InformacionActivity extends AppCompatActivity {

    // --- UI (footer GPS) ---
    private TextView tvGpsEstado;
    private MaterialButton btnGps;

    // --- Location/GPS ---
    private FusedLocationProviderClient fused;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private CancellationTokenSource locationCts;

    // --- Launchers (permisos + resolución de ajustes de ubicación) ---
    private final ActivityResultLauncher<String[]> permisosLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean fine   = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false));
                boolean coarse = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false));
                if (fine || coarse) {
                    verificarSettingsYObtenerUbicacion();
                } else {
                    tvGpsEstado.setText("Ubicación: permiso denegado");
                }
            });

    private final ActivityResultLauncher<IntentSenderRequest> settingsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), res -> {
                // Tras intentar encender el GPS, volvemos a intentar obtener ubicación
                verificarSettingsYObtenerUbicacion();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion);

        // --- Nav Rail ---
        NavigationRailView nav = findViewById(R.id.navRail);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.itAgenda) {
                startActivity(new Intent(this, AgendarHoraActivity.class));
                return true;
            } else if (id == R.id.itSolicitar) {
                startActivity(new Intent(this, SolicitarHoraActivity.class));
                return true;
            } else if (id == R.id.itVolver) {
                volverAlLogin();
                return true;
            }
            return false;
        });

        // Botón físico “atrás”
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { volverAlLogin(); }
        });

        // --- Footer GPS views ---
        tvGpsEstado = findViewById(R.id.tvGpsEstado);
        btnGps      = findViewById(R.id.btnGps);
        tvGpsEstado.setText("Ubicación: toca 'Activar GPS'");

        // --- Location init ---
        fused = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 8000L)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(4000L)
                .build();

        btnGps.setOnClickListener(v -> iniciarFlujoUbicacion());
    }

    // ---------- Navegación: volver al login ----------
    private void volverAlLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    // ---------- Flujo GPS ----------
    private void iniciarFlujoUbicacion() {
        if (!tienePermisosUbicacion()) {
            solicitarPermisos();
            return;
        }
        verificarSettingsYObtenerUbicacion();
    }

    private boolean tienePermisosUbicacion() {
        int fine   = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED;
    }

    private void solicitarPermisos() {
        permisosLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void verificarSettingsYObtenerUbicacion() {
        LocationSettingsRequest req = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
                .build();

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(req);
        task.addOnSuccessListener(r -> obtenerUltimaUbicacion());
        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    // getResolution() devuelve PendingIntent → usamos su IntentSender
                    IntentSender sender = ((ResolvableApiException) e).getResolution().getIntentSender();
                    IntentSenderRequest ir = new IntentSenderRequest.Builder(sender).build();
                    settingsLauncher.launch(ir);
                } catch (Exception ex) {
                    tvGpsEstado.setText("Ubicación: error al abrir ajustes");
                }
            } else {
                tvGpsEstado.setText("Ubicación: desactivada");
            }
        });
    }

    private void obtenerUltimaUbicacion() {
        if (!tienePermisosUbicacion()) {
            tvGpsEstado.setText("Ubicación: sin permisos");
            return;
        }
        tvGpsEstado.setText("Ubicación: buscando…");
        try {
            fused.getLastLocation()
                    .addOnSuccessListener(loc -> {
                        if (loc != null) {
                            mostrarUbicacion(loc);
                        } else {
                            // Si no hay cache, pedimos una ubicación fresca
                            obtenerUbicacionActual();
                        }
                    })
                    .addOnFailureListener(e -> tvGpsEstado.setText("Ubicación: error al leer"));
        } catch (SecurityException se) {
            tvGpsEstado.setText("Ubicación: permiso requerido");
        }
    }

    private void obtenerUbicacionActual() {
        if (!tienePermisosUbicacion()) {
            tvGpsEstado.setText("Ubicación: sin permisos");
            return;
        }
        tvGpsEstado.setText("Ubicación: obteniendo ubicación actual…");

        if (locationCts != null) locationCts.cancel();
        locationCts = new CancellationTokenSource();

        CurrentLocationRequest req = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(0)     // no usar cache
                .setDurationMillis(10_000L)   // timeout 10s
                .build();

        try {
            fused.getCurrentLocation(req, locationCts.getToken())
                    .addOnSuccessListener(loc -> {
                        if (loc != null) {
                            mostrarUbicacion(loc);
                        } else {
                            tvGpsEstado.setText("Ubicación: sin datos (reintenta)");
                        }
                    })
                    .addOnFailureListener(e -> tvGpsEstado.setText("Ubicación: error al obtener"));
        } catch (SecurityException se) {
            tvGpsEstado.setText("Ubicación: permiso requerido");
        }
    }

    private void mostrarUbicacion(Location loc) {
        String s = String.format("Lat: %.5f  Lon: %.5f", loc.getLatitude(), loc.getLongitude());
        tvGpsEstado.setText(s);
        btnGps.setText("Actualizar");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationCts != null) locationCts.cancel();
    }
}
