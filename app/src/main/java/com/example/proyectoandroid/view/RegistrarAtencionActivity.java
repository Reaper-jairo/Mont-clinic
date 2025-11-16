package com.example.proyectoandroid.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.util.DateUtils;
import com.example.proyectoandroid.viewmodel.RegistrarAtencionViewModel;

import java.util.Calendar;

public class RegistrarAtencionActivity extends BaseActivity {

    private EditText etRutPacienteAtencion, etFechaAtencion, etMedicoAtencion;
    private EditText etMotivoAtencion, etDiagnosticoAtencion, etObservacionesAtencion;
    private Button btnRegistrarAtencion;
    private android.widget.ProgressBar progressRegistrarAtencion;
    private RegistrarAtencionViewModel viewModel;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_atencion);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(RegistrarAtencionViewModel.class);

        // Inicializar UI
        etRutPacienteAtencion = findViewById(R.id.etRutPacienteAtencion);
        etFechaAtencion = findViewById(R.id.etFechaAtencion);
        etMedicoAtencion = findViewById(R.id.etMedicoAtencion);
        etMotivoAtencion = findViewById(R.id.etMotivoAtencion);
        etDiagnosticoAtencion = findViewById(R.id.etDiagnosticoAtencion);
        etObservacionesAtencion = findViewById(R.id.etObservacionesAtencion);
        btnRegistrarAtencion = findViewById(R.id.btnRegistrarAtencion);
        progressRegistrarAtencion = findViewById(R.id.progressRegistrarAtencion);

        // Inicializar Calendar
        calendar = Calendar.getInstance();

        // Configurar fecha con DatePickerDialog
        etFechaAtencion.setFocusable(false);
        etFechaAtencion.setClickable(true);
        etFechaAtencion.setOnClickListener(v -> mostrarDatePicker());

        // Establecer valor por defecto usando DateUtils
        etFechaAtencion.setText(DateUtils.getCurrentDateDisplay());

        // Observar ViewModel
        observarViewModel();

        btnRegistrarAtencion.setOnClickListener(v -> registrarAtencion());
        
        // Vincular GPS footer
        android.widget.TextView tvGps = findViewById(R.id.tvGpsEstado);
        if (tvGps != null) {
            com.example.proyectoandroid.util.LocationTracker.get().bindTextView(tvGps);
        }
    }

    private void mostrarDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etFechaAtencion.setText(DateUtils.formatDateForDisplay(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void registrarAtencion() {
        String rut = etRutPacienteAtencion.getText().toString().trim();
        String fecha = etFechaAtencion.getText().toString().trim();
        String medico = etMedicoAtencion.getText().toString().trim();
        String motivo = etMotivoAtencion.getText().toString().trim();
        String diagnostico = etDiagnosticoAtencion.getText().toString().trim();
        String observaciones = etObservacionesAtencion.getText().toString().trim();

        // Validaciones básicas
        if (TextUtils.isEmpty(rut)) {
            etRutPacienteAtencion.setError(getString(R.string.error_rut_obligatorio));
            return;
        }
        if (TextUtils.isEmpty(medico)) {
            etMedicoAtencion.setError(getString(R.string.error_medico_obligatorio));
            return;
        }
        if (TextUtils.isEmpty(motivo)) {
            etMotivoAtencion.setError(getString(R.string.error_motivo_obligatorio));
            return;
        }

        // Hacer variables final para usar en lambda
        final String rutFinal = rut;
        final String fechaFinal = fecha;
        final String medicoFinal = medico;
        final String motivoFinal = motivo;
        final String diagnosticoFinal = diagnostico;
        final String observacionesFinal = observaciones;

        // Mostrar confirmación antes de registrar
        mostrarConfirmacion(
                getString(R.string.titulo_confirmacion),
                getString(R.string.confirmar_registrar_atencion),
                () -> {
                    // Confirmar: registrar atención
                    viewModel.registrarAtencion(rutFinal, fechaFinal, medicoFinal, motivoFinal, diagnosticoFinal, observacionesFinal);
                },
                null // Cancelar: no hacer nada
        );
    }

    private void observarViewModel() {
        // Observar carga usando BaseActivity
        observarCarga(viewModel.getIsLoading(), progressRegistrarAtencion);
        
        viewModel.getIsLoading().observe(this, isLoading -> {
            btnRegistrarAtencion.setEnabled(!isLoading);
            if (isLoading) {
                btnRegistrarAtencion.setText(getString(R.string.btn_registrando_atencion));
            } else {
                btnRegistrarAtencion.setText(getString(R.string.btn_registrar_atencion));
            }
        });

        // Observar errores usando BaseActivity
        observarErrores(viewModel.getErrorMessage());

        viewModel.getRegistroExitoso().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                mostrarExito(getString(R.string.atencion_registrada_exitoso));
                finish();
            }
        });
    }
}

