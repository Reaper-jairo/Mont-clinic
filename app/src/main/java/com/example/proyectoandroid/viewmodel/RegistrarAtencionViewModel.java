package com.example.proyectoandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.repository.PacienteRepository;
import com.example.proyectoandroid.util.DateUtils;
import com.example.proyectoandroid.util.ErrorHandler;
import com.example.proyectoandroid.util.NetworkUtils;
import com.example.proyectoandroid.util.RutUtils;

import java.util.Date;

/**
 * ViewModel para registrar atenciones médicas
 */
public class RegistrarAtencionViewModel extends AndroidViewModel {
    private final PacienteRepository pacienteRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registroExitoso = new MutableLiveData<>();

    public RegistrarAtencionViewModel(@NonNull Application application) {
        super(application);
        pacienteRepository = PacienteRepository.getInstance();
    }

    /**
     * Registra una nueva atención médica
     */
    public void registrarAtencion(String rutPaciente, String fechaStr, String medico,
                                   String motivo, String diagnostico, String observaciones) {
        // Validar RUT
        String rutNormalizado = RutUtils.normalizarRut(rutPaciente);
        if (!RutUtils.esRutValidoSinGuion(rutNormalizado)) {
            errorMessage.setValue(getApplication().getString(R.string.error_rut_invalido));
            return;
        }

        // Validar campos obligatorios
        if (medico == null || medico.trim().isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_medico_obligatorio));
            return;
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_motivo_obligatorio));
            return;
        }

        // Parsear fecha usando DateUtils
        Date fecha;
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            fecha = DateUtils.getCurrentDate(); // Usar fecha actual si no se especifica
        } else {
            fecha = DateUtils.parseDate(fechaStr);
            if (fecha == null) {
                errorMessage.setValue(getApplication().getString(R.string.error_fecha_invalida));
                return;
            }
        }

        // Verificar conexión a internet
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(getApplication().getString(R.string.error_sin_conexion));
            return;
        }

        // Hacer variables final para usar en lambdas
        final Date fechaFinal = fecha;
        final String rutFinal = rutNormalizado;
        final String motivoFinal = motivo.trim();
        final String medicoFinal = medico.trim();
        final String diagnosticoFinal = diagnostico != null ? diagnostico.trim() : "";
        final String observacionesFinal = observaciones != null ? observaciones.trim() : "";

        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Obtener email del paciente desde rut_index
        pacienteRepository.obtenerEmailPorRut(rutFinal, email -> {
            if (email == null || email.isEmpty()) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_paciente_no_encontrado));
                return;
            }

            pacienteRepository.registrarAtencion(
                    email,
                    rutFinal,
                    fechaFinal,
                    motivoFinal,
                    medicoFinal,
                    diagnosticoFinal,
                    observacionesFinal,
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
        });
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

