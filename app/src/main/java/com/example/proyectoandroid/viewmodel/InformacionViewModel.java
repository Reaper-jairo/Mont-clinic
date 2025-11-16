package com.example.proyectoandroid.viewmodel;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.proyectoandroid.model.Atencion;
import com.example.proyectoandroid.model.Cita;
import com.example.proyectoandroid.model.LocationState;
import com.example.proyectoandroid.model.Paciente;
import com.example.proyectoandroid.repository.AuthRepository;
import com.example.proyectoandroid.repository.LocationRepository;
import com.example.proyectoandroid.repository.PacienteRepository;
import com.example.proyectoandroid.util.NetworkUtils;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * ViewModel para la pantalla de información
 */
public class InformacionViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final LocationRepository locationRepository;
    private final PacienteRepository pacienteRepository;
    private final MutableLiveData<LocationState> locationState = new MutableLiveData<>(new LocationState());
    private final MutableLiveData<Paciente> paciente = new MutableLiveData<>();
    private final MutableLiveData<List<Atencion>> atenciones = new MutableLiveData<>();
    private final MutableLiveData<List<Cita>> citas = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public InformacionViewModel(@NonNull Application application) {
        super(application);
        authRepository = AuthRepository.getInstance();
        locationRepository = LocationRepository.getInstance(application);
        pacienteRepository = PacienteRepository.getInstance();
    }

    /**
     * Carga todos los datos del paciente
     */
    public void cargarDatosPaciente() {
        // Verificar conexión a internet
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            android.util.Log.w("InformacionViewModel", "Sin conexión a internet");
            // No mostrar error, solo no cargar datos
            return;
        }

        isLoading.setValue(true);
        
        // Cargar datos del paciente
        pacienteRepository.obtenerDatosPaciente(pacienteData -> {
            paciente.setValue(pacienteData);
            isLoading.setValue(false);
        });

        // Cargar últimas atenciones
        pacienteRepository.obtenerUltimasAtenciones(5, atencionesList -> {
            atenciones.setValue(atencionesList);
        });

        // Cargar próximas citas
        pacienteRepository.obtenerProximasCitas(5, citasList -> {
            android.util.Log.d("InformacionViewModel", "Citas recibidas: " + (citasList != null ? citasList.size() : 0));
            citas.setValue(citasList);
        });
    }

    /**
     * Obtiene la última ubicación conocida
     */
    public void obtenerUltimaUbicacion() {
        LocationState state = new LocationState();
        state.setLoading(true);
        locationState.setValue(state);

        // Observar una sola vez el LiveData
        LiveData<Location> lastLocationLiveData = locationRepository.getLastLocation();
        lastLocationLiveData.observeForever(new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                // Remover el observer después de recibir el valor
                lastLocationLiveData.removeObserver(this);
                
                LocationState newState = new LocationState();
                if (location != null) {
                    newState.setLocation(location);
                    newState.setAddress(formatLocation(location));
                } else {
                    // Si no hay última ubicación, intentar obtener ubicación actual
                    obtenerUbicacionActual();
                    return;
                }
                newState.setLoading(false);
                locationState.setValue(newState);
            }
        });
    }

    /**
     * Obtiene la ubicación actual (fresca)
     */
    public void obtenerUbicacionActual() {
        LocationState state = new LocationState();
        state.setLoading(true);
        locationState.setValue(state);

        // Observar una sola vez el LiveData
        LiveData<Location> currentLocationLiveData = locationRepository.getCurrentLocation();
        currentLocationLiveData.observeForever(new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                // Remover el observer después de recibir el valor
                currentLocationLiveData.removeObserver(this);
                
                LocationState newState = new LocationState();
                if (location != null) {
                    newState.setLocation(location);
                    newState.setAddress(formatLocation(location));
                } else {
                    newState.setError("No se pudo obtener la ubicación");
                }
                newState.setLoading(false);
                locationState.setValue(newState);
            }
        });
    }

    /**
     * Formatea la ubicación como string (ya no se usa, LocationTracker maneja las direcciones)
     */
    private String formatLocation(Location location) {
        if (location == null) return "Ubicación: sin datos";
        // LocationTracker ya formatea las direcciones, este método ya no se usa
        return "Ubicación: obteniendo dirección...";
    }

    /**
     * Cierra sesión
     */
    public void cerrarSesion() {
        authRepository.cerrarSesion();
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    /**
     * Cancela las solicitudes de ubicación
     */
    public void cancelarSolicitudesUbicacion() {
        locationRepository.cancelLocationRequests();
    }

    /**
     * Detiene los listeners cuando el ViewModel se destruye
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        pacienteRepository.detenerListeners();
    }

    // Getters para LiveData
    public LiveData<LocationState> getLocationState() {
        return locationState;
    }

    public LiveData<Paciente> getPaciente() {
        return paciente;
    }

    public LiveData<List<Atencion>> getAtenciones() {
        return atenciones;
    }

    public LiveData<List<Cita>> getCitas() {
        return citas;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}

