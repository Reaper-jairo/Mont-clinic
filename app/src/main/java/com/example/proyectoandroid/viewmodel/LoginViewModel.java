package com.example.proyectoandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.RutValidation;
import com.example.proyectoandroid.repository.AuthRepository;
import com.example.proyectoandroid.util.ErrorHandler;
import com.example.proyectoandroid.util.NetworkUtils;
import com.example.proyectoandroid.util.RutUtils;
import com.example.proyectoandroid.util.ValidationUtils;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel para la pantalla de login
 */
public class LoginViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<RutValidation> rutValidation = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = AuthRepository.getInstance();
    }

    /**
     * Valida el formato del RUT
     */
    public void validarRut(String rut) {
        String rutNormalizado = RutUtils.normalizarRut(rut);
        
        if (!RutUtils.tieneFormatoMinimo(rutNormalizado)) {
            rutValidation.setValue(new RutValidation(rutNormalizado, false, 
                    "Ingresa RUT sin guion. Ej: 19875613K"));
            return;
        }
        
        if (!RutUtils.esRutValidoSinGuion(rutNormalizado)) {
            rutValidation.setValue(new RutValidation(rutNormalizado, false, 
                    "RUT inválido. Revisa dígitos y DV."));
            return;
        }
        
        rutValidation.setValue(new RutValidation(rutNormalizado, true, null));
    }

    /**
     * Valida la contraseña
     */
    public boolean validarPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Inicia el proceso de login
     */
    public void iniciarSesion(String rut, String password) {
        // Validar RUT
        String rutNormalizado = RutUtils.normalizarRut(rut);
        if (!RutUtils.esRutValidoSinGuion(rutNormalizado)) {
            errorMessage.setValue("RUT inválido");
            return;
        }

        // Validar contraseña
        if (!ValidationUtils.esPasswordValida(password, 6)) {
            errorMessage.setValue(getApplication().getString(R.string.error_password_corta));
            return;
        }

        // Verificar conexión a internet
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(getApplication().getString(R.string.error_sin_conexion));
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Asegurar que el RUT esté normalizado y en mayúsculas
        String rutBuscar = rutNormalizado.toUpperCase();

        // Buscar email por RUT usando callback
        authRepository.buscarEmailPorRut(rutBuscar, email -> {
            if (email != null && !email.trim().isEmpty()) {
                // Email encontrado, intentar login
                authRepository.iniciarSesion(email.trim(), password, authResult -> {
                    isLoading.setValue(false);
                    if (authResult.isSuccess()) {
                        loginSuccess.setValue(true);
                    } else {
                        String error = authResult.getError();
                        // Usar ErrorHandler para mensajes más amigables
                        errorMessage.setValue(ErrorHandler.getFriendlyMessage(new Exception(error)));
                    }
                });
            } else {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_rut_no_encontrado));
            }
        });
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    // Getters para LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<RutValidation> getRutValidation() {
        return rutValidation;
    }
}

