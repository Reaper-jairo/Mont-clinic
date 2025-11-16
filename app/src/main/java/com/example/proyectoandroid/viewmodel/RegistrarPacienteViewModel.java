package com.example.proyectoandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.repository.PacienteRepository;
import com.example.proyectoandroid.util.ErrorHandler;
import com.example.proyectoandroid.util.NetworkUtils;
import com.example.proyectoandroid.util.RutUtils;
import com.example.proyectoandroid.util.ValidationUtils;

/**
 * ViewModel para registrar nuevos pacientes
 */
public class RegistrarPacienteViewModel extends AndroidViewModel {
    private final PacienteRepository pacienteRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registroExitoso = new MutableLiveData<>();

    public RegistrarPacienteViewModel(@NonNull Application application) {
        super(application);
        pacienteRepository = PacienteRepository.getInstance();
    }

    /**
     * Valida y registra un nuevo paciente
     */
    public void registrarPaciente(String rutRaw, String email, String password,
                                   String nombre, String telefono, String direccion) {
        // Validar RUT
        String rutNormalizado = RutUtils.normalizarRut(rutRaw);
        
        // Validar formato básico (8 o 9 caracteres: 7-8 dígitos + 1 dígito verificador)
        if (rutNormalizado == null || rutNormalizado.length() < 8 || rutNormalizado.length() > 9) {
            errorMessage.setValue(getApplication().getString(R.string.error_rut_formato));
            return;
        }
        
        // Validar formato del RUT (solo formato, no algoritmo)
        if (!RutUtils.esRutValidoSinGuion(rutNormalizado)) {
            errorMessage.setValue(getApplication().getString(R.string.error_rut_formato));
            return;
        }

        // Validar email
        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_email_obligatorio));
            return;
        }
        
        if (!ValidationUtils.esEmailValido(email)) {
            errorMessage.setValue(getApplication().getString(R.string.error_email_invalido));
            return;
        }

        // Validar contraseña
        if (!ValidationUtils.esPasswordValida(password, 6)) {
            errorMessage.setValue(getApplication().getString(R.string.error_password_corta));
            return;
        }
        
        // Validar nombre (opcional pero recomendado)
        if (nombre != null && !nombre.trim().isEmpty() && !ValidationUtils.esNombreValido(nombre)) {
            errorMessage.setValue(getApplication().getString(R.string.error_nombre_invalido));
            return;
        }

        // Verificar conexión a internet
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(getApplication().getString(R.string.error_sin_conexion));
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        pacienteRepository.registrarPaciente(
                rutNormalizado,
                email.trim(),
                password,
                nombre != null ? nombre.trim() : "",
                telefono != null ? telefono.trim() : "",
                direccion != null ? direccion.trim() : "",
                new PacienteRepository.RegistroCallback() {
                    @Override
                    public void onSuccess() {
                        isLoading.setValue(false);
                        registroExitoso.setValue(true);
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.setValue(false);
                        // Usar ErrorHandler para mensajes más amigables
                        errorMessage.setValue(ErrorHandler.getFriendlyMessage(new Exception(error)));
                    }
                }
        );
    }

    // Getters para LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getRegistroExitoso() {
        return registroExitoso;
    }
}

