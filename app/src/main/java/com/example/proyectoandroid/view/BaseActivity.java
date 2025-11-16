package com.example.proyectoandroid.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.util.BrightnessManager;
import com.google.android.material.snackbar.Snackbar;

/**
 * Clase base para Activities con funcionalidades comunes
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    protected BrightnessManager brightnessManager;

    /**
     * Observa errores de un ViewModel y los muestra al usuario
     */
    protected <T> void observarErrores(LiveData<String> errorLiveData) {
        errorLiveData.observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                mostrarError(error);
            }
        });
    }

    /**
     * Observa el estado de carga y actualiza un ProgressBar
     */
    protected void observarCarga(LiveData<Boolean> loadingLiveData, ProgressBar progressBar) {
        loadingLiveData.observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Muestra un mensaje de error usando Snackbar
     */
    protected void mostrarError(String mensaje) {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar.make(rootView, mensaje, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getColor(R.color.error))
                    .setTextColor(getColor(R.color.white))
                    .show();
        } else {
            // Fallback a Toast si no hay root view
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Muestra un mensaje de éxito usando Snackbar
     */
    protected void mostrarExito(String mensaje) {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar.make(rootView, mensaje, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.success))
                    .setTextColor(getColor(R.color.white))
                    .show();
        } else {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Muestra un mensaje informativo usando Snackbar
     */
    protected void mostrarInfo(String mensaje) {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar.make(rootView, mensaje, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.primary))
                    .setTextColor(getColor(R.color.white))
                    .show();
        } else {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Muestra un diálogo de confirmación
     */
    protected void mostrarConfirmacion(String titulo, String mensaje, 
                                       Runnable onConfirm, Runnable onCancel) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(getString(R.string.btn_confirmar), (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancelar), (dialog, which) -> {
                    if (onCancel != null) {
                        onCancel.run();
                    }
                })
                .setCancelable(true)
                .show();
    }

    /**
     * Muestra un diálogo de confirmación simple (solo con confirmar)
     */
    protected void mostrarConfirmacionSimple(String titulo, String mensaje, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(getString(R.string.btn_aceptar), (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setCancelable(true)
                .show();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializar gestor de brillo
        brightnessManager = BrightnessManager.getInstance();
        brightnessManager.initialize(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Habilitar ajuste automático de brillo si está disponible y el usuario lo permite
        if (brightnessManager != null && brightnessManager.isSensorAvailable() && brightnessManager.getUserPreference()) {
            brightnessManager.enableAutoBrightness();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Deshabilitar ajuste automático de brillo para ahorrar batería
        if (brightnessManager != null) {
            brightnessManager.disableAutoBrightness();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar recursos del gestor de brillo
        if (brightnessManager != null) {
            brightnessManager.restoreSystemBrightness();
        }
    }
}

