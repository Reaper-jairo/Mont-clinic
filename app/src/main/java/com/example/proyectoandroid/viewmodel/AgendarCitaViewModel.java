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
import com.example.proyectoandroid.util.ValidationUtils;

import java.util.Date;

/**
 * ViewModel para agendar citas médicas
 */
public class AgendarCitaViewModel extends AndroidViewModel {
    private final PacienteRepository pacienteRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registroExitoso = new MutableLiveData<>();

    public AgendarCitaViewModel(@NonNull Application application) {
        super(application);
        pacienteRepository = PacienteRepository.getInstance();
    }

    /**
     * Agenda una nueva cita
     */
    public void agendarCita(String rutPaciente, String fechaStr, String hora,
                            String medico, String tipo, String motivo) {
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
        if (hora == null || hora.trim().isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_hora_obligatoria));
            return;
        }

        // Parsear fecha usando DateUtils
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_fecha_obligatoria));
            return;
        }
        
        Date fecha = DateUtils.parseDateFromStorage(fechaStr);
        if (fecha == null) {
            errorMessage.setValue(getApplication().getString(R.string.error_fecha_invalida));
            return;
        }
        
        // Combinar fecha y hora usando DateUtils
        fecha = DateUtils.combineDateAndTime(fecha, hora);
        
            // Validar que la fecha sea futura
            if (!ValidationUtils.esFechaFuturaOIgual(fecha)) {
                errorMessage.setValue(getApplication().getString(R.string.error_fecha_pasado));
                return;
            }

        // Verificar conexión a internet
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            errorMessage.setValue(getApplication().getString(R.string.error_sin_conexion));
            return;
        }

        // Hacer variables final para usar en lambdas
        final Date fechaFinal = fecha;
        final String rutFinal = rutNormalizado;
        final String horaFinal = hora.trim();
        final String motivoFinal = motivo.trim();
        final String medicoFinal = medico.trim();
        final String tipoFinal = tipo != null ? tipo.trim() : "consulta";

        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Obtener email del paciente desde rut_index
        pacienteRepository.obtenerEmailPorRut(rutFinal, email -> {
            if (email == null || email.isEmpty()) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_paciente_no_encontrado));
                return;
            }

            pacienteRepository.agendarCita(
                    email,
                    rutFinal,
                    fechaFinal,
                    horaFinal,
                    motivoFinal,
                    medicoFinal,
                    tipoFinal,
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

