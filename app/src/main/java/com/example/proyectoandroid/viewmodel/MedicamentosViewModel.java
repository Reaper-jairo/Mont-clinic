package com.example.proyectoandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.Medicamento;
import com.example.proyectoandroid.repository.MedicamentoRepository;
import com.example.proyectoandroid.util.NetworkUtils;

import java.util.List;

/**
 * ViewModel para la pantalla de medicamentos
 */
public class MedicamentosViewModel extends AndroidViewModel {
    private final MedicamentoRepository medicamentoRepository;
    private final MutableLiveData<List<Medicamento>> medicamentos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MedicamentosViewModel(@NonNull Application application) {
        super(application);
        medicamentoRepository = MedicamentoRepository.getInstance();
    }

    /**
     * Carga los medicamentos del paciente con actualización en tiempo real
     */
    public void cargarMedicamentos() {
        // Verificar conexión a internet
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            android.util.Log.w("MedicamentosViewModel", "Sin conexión a internet");
            // No mostrar error, solo no cargar datos
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        medicamentoRepository.obtenerMedicamentos(medicamentosList -> {
            medicamentos.setValue(medicamentosList);
            isLoading.setValue(false);
        });
    }

    /**
     * Detiene el listener cuando el ViewModel se destruye
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        medicamentoRepository.detenerListener();
    }

    // Getters para LiveData
    public LiveData<List<Medicamento>> getMedicamentos() {
        return medicamentos;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}

