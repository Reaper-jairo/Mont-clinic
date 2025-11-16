package com.example.proyectoandroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

/**
 * Gestor de brillo automático basado en sensor de luz ambiental
 */
public class BrightnessManager implements SensorEventListener {
    private static final String TAG = "BrightnessManager";
    private static final String PREFS_NAME = "brightness_prefs";
    private static final String KEY_AUTO_BRIGHTNESS_ENABLED = "auto_brightness_enabled";
    private static BrightnessManager instance;
    
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Activity activity;
    private boolean isEnabled = false;
    private boolean userPreferenceEnabled = true; // Por defecto activado
    private float currentLux = 0;
    
    // Valores de brillo (0.0 a 1.0)
    private static final float BRIGHTNESS_DARK = 0.1f;      // Muy oscuro
    private static final float BRIGHTNESS_DIM = 0.3f;       // Oscuro
    private static final float BRIGHTNESS_NORMAL = 0.5f;    // Normal
    private static final float BRIGHTNESS_BRIGHT = 0.7f;    // Brillante
    private static final float BRIGHTNESS_VERY_BRIGHT = 0.9f; // Muy brillante
    
    // Umbrales de luz en lux
    private static final float LUX_DARK = 10.0f;
    private static final float LUX_DIM = 50.0f;
    private static final float LUX_NORMAL = 200.0f;
    private static final float LUX_BRIGHT = 500.0f;
    
    private BrightnessManager() {
        // Constructor privado para singleton
    }
    
    public static BrightnessManager getInstance() {
        if (instance == null) {
            instance = new BrightnessManager();
        }
        return instance;
    }
    
    /**
     * Inicializa el sensor de luz ambiental
     */
    public void initialize(Activity activity) {
        this.activity = activity;
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        
        // Cargar preferencia del usuario
        loadUserPreference(activity);
        
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (lightSensor != null) {
                Log.d(TAG, "Sensor de luz ambiental disponible");
            } else {
                Log.w(TAG, "Sensor de luz ambiental no disponible en este dispositivo");
            }
        }
    }
    
    /**
     * Carga la preferencia del usuario desde SharedPreferences
     */
    private void loadUserPreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userPreferenceEnabled = prefs.getBoolean(KEY_AUTO_BRIGHTNESS_ENABLED, true);
        Log.d(TAG, "Preferencia de brillo automático cargada: " + userPreferenceEnabled);
    }
    
    /**
     * Guarda la preferencia del usuario
     */
    private void saveUserPreference(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_AUTO_BRIGHTNESS_ENABLED, enabled).apply();
        userPreferenceEnabled = enabled;
        Log.d(TAG, "Preferencia de brillo automático guardada: " + enabled);
    }
    
    /**
     * Establece la preferencia del usuario para el ajuste automático de brillo
     */
    public void setUserPreference(boolean enabled) {
        if (activity != null) {
            saveUserPreference(activity, enabled);
            
            // Si se desactiva mientras está activo, desactivarlo
            if (!enabled && isEnabled) {
                disableAutoBrightness();
            }
            // Si se activa y la actividad está en primer plano, activarlo
            else if (enabled && !isEnabled && activity.hasWindowFocus()) {
                enableAutoBrightness();
            }
        }
    }
    
    /**
     * Obtiene la preferencia del usuario
     */
    public boolean getUserPreference() {
        return userPreferenceEnabled;
    }
    
    /**
     * Habilita el ajuste automático de brillo (solo si el usuario lo ha permitido)
     */
    public void enableAutoBrightness() {
        if (lightSensor == null || activity == null) {
            Log.w(TAG, "No se puede habilitar: sensor o actividad no disponible");
            return;
        }
        
        // Verificar preferencia del usuario
        if (!userPreferenceEnabled) {
            Log.d(TAG, "Brillo automático deshabilitado por el usuario");
            return;
        }
        
        if (!isEnabled) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isEnabled = true;
            Log.d(TAG, "Ajuste automático de brillo habilitado");
        }
    }
    
    /**
     * Deshabilita el ajuste automático de brillo
     */
    public void disableAutoBrightness() {
        if (sensorManager != null && isEnabled) {
            sensorManager.unregisterListener(this);
            isEnabled = false;
            Log.d(TAG, "Ajuste automático de brillo deshabilitado");
        }
    }
    
    /**
     * Verifica si el sensor está disponible
     */
    public boolean isSensorAvailable() {
        return lightSensor != null;
    }
    
    /**
     * Verifica si el ajuste automático está habilitado
     */
    public boolean isEnabled() {
        return isEnabled;
    }
    
    /**
     * Obtiene el nivel de luz actual
     */
    public float getCurrentLux() {
        return currentLux;
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT && activity != null) {
            currentLux = event.values[0];
            adjustBrightness(currentLux);
        }
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario hacer nada aquí
    }
    
    /**
     * Ajusta el brillo de la pantalla según el nivel de luz ambiental
     */
    private void adjustBrightness(float lux) {
        if (activity == null) {
            return;
        }
        
        float brightness;
        
        if (lux < LUX_DARK) {
            brightness = BRIGHTNESS_DARK;
        } else if (lux < LUX_DIM) {
            brightness = BRIGHTNESS_DIM;
        } else if (lux < LUX_NORMAL) {
            brightness = BRIGHTNESS_NORMAL;
        } else if (lux < LUX_BRIGHT) {
            brightness = BRIGHTNESS_BRIGHT;
        } else {
            brightness = BRIGHTNESS_VERY_BRIGHT;
        }
        
        // Aplicar brillo a la ventana
        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.screenBrightness = brightness;
        activity.getWindow().setAttributes(layoutParams);
        
        Log.d(TAG, String.format("Lux: %.2f, Brillo: %.2f", lux, brightness));
    }
    
    /**
     * Restaura el brillo automático del sistema
     */
    public void restoreSystemBrightness() {
        if (activity != null) {
            WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            activity.getWindow().setAttributes(layoutParams);
        }
    }
    
    /**
     * Limpia los recursos
     */
    public void cleanup() {
        disableAutoBrightness();
        activity = null;
        sensorManager = null;
        lightSensor = null;
    }
}

