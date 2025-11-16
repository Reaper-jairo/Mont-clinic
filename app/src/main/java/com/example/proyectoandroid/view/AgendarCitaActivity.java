package com.example.proyectoandroid.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.util.DateUtils;
import com.example.proyectoandroid.viewmodel.AgendarCitaViewModel;

import java.util.Calendar;

public class AgendarCitaActivity extends BaseActivity {

    private EditText etRutPacienteCita, etFechaCita, etHoraCita;
    private EditText etMedicoCita, etMotivoCita;
    private Spinner spTipoCita;
    private Button btnAgendarCita;
    private Button btnVolverAgendar;
    private android.widget.ProgressBar progressAgendarCita;
    private AgendarCitaViewModel viewModel;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(AgendarCitaViewModel.class);

        // Inicializar UI
        etRutPacienteCita = findViewById(R.id.etRutPacienteCita);
        etFechaCita = findViewById(R.id.etFechaCita);
        etHoraCita = findViewById(R.id.etHoraCita);
        etMedicoCita = findViewById(R.id.etMedicoCita);
        spTipoCita = findViewById(R.id.spTipoCita);
        etMotivoCita = findViewById(R.id.etMotivoCita);
        btnAgendarCita = findViewById(R.id.btnAgendarCita);
        btnVolverAgendar = findViewById(R.id.btnVolverAgendar);
        progressAgendarCita = findViewById(R.id.progressAgendarCita);

        // Botón volver
        if (btnVolverAgendar != null) {
            btnVolverAgendar.setOnClickListener(v -> finish());
        }

        // Configurar filtro para RUT
        configurarRut();

        // Configurar Spinner de tipo de cita
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_cita, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoCita.setAdapter(adapter);

        // Inicializar Calendar
        calendar = Calendar.getInstance();

        // Configurar fecha con DatePickerDialog
        etFechaCita.setFocusable(false);
        etFechaCita.setClickable(true);
        etFechaCita.setOnClickListener(v -> mostrarDatePicker());

        // Configurar hora con TimePickerDialog
        etHoraCita.setFocusable(false);
        etHoraCita.setClickable(true);
        etHoraCita.setOnClickListener(v -> mostrarTimePicker());

        // Establecer valores por defecto usando DateUtils
        etFechaCita.setText(DateUtils.getCurrentDateDisplay());
        etHoraCita.setText(DateUtils.getCurrentTime());

        // Observar ViewModel
        observarViewModel();

        btnAgendarCita.setOnClickListener(v -> agendarCita());
        
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
                    etFechaCita.setText(DateUtils.formatDateForDisplay(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        // No permitir fechas pasadas
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void mostrarTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    etHoraCita.setText(DateUtils.formatTime(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // formato 24 horas
        );
        timePickerDialog.show();
    }

    private void configurarRut() {
        // Filtro para RUT: solo números y K, máximo 9 caracteres
        InputFilter rutFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (!(Character.isDigit(c) || c == 'k' || c == 'K')) {
                        return "";
                    }
                }
                return null;
            }
        };
        etRutPacienteCita.setFilters(new InputFilter[]{rutFilter, new InputFilter.LengthFilter(9)});

        // Convertir a mayúsculas automáticamente
        etRutPacienteCita.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                String upper = text.toUpperCase();
                if (!text.equals(upper)) {
                    etRutPacienteCita.removeTextChangedListener(this);
                    etRutPacienteCita.setText(upper);
                    etRutPacienteCita.setSelection(upper.length());
                    etRutPacienteCita.addTextChangedListener(this);
                }
            }
        });
    }

    private void agendarCita() {
        String rut = etRutPacienteCita.getText().toString().trim();
        String fecha = etFechaCita.getText().toString().trim();
        String hora = etHoraCita.getText().toString().trim();
        String medico = etMedicoCita.getText().toString().trim();
        String tipo = spTipoCita.getSelectedItem() != null ? spTipoCita.getSelectedItem().toString().toLowerCase() : "consulta";
        String motivo = etMotivoCita.getText().toString().trim();

        // Convertir fecha de dd/MM/yyyy a yyyy-MM-dd para el ViewModel usando DateUtils
        String fechaFormato = DateUtils.convertDisplayToStorage(fecha);

        // Validaciones básicas
        if (TextUtils.isEmpty(rut)) {
            etRutPacienteCita.setError("RUT es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(fecha)) {
            etFechaCita.setError("Fecha es obligatoria");
            return;
        }
        if (TextUtils.isEmpty(hora)) {
            etHoraCita.setError("Hora es obligatoria");
            return;
        }
        if (TextUtils.isEmpty(medico)) {
            etMedicoCita.setError("Médico es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(motivo)) {
            etMotivoCita.setError("Motivo es obligatorio");
            return;
        }

        // Hacer variables final para usar en lambda
        final String rutFinal = rut;
        final String fechaFormatoFinal = fechaFormato;
        final String horaFinal = hora;
        final String medicoFinal = medico;
        final String tipoFinal = tipo;
        final String motivoFinal = motivo;

        // Mostrar confirmación antes de agendar
        mostrarConfirmacion(
                getString(R.string.titulo_confirmacion),
                getString(R.string.confirmar_agendar_cita),
                () -> {
                    // Confirmar: agendar cita
                    viewModel.agendarCita(rutFinal, fechaFormatoFinal, horaFinal, medicoFinal, tipoFinal, motivoFinal);
                },
                null // Cancelar: no hacer nada
        );
    }

    private void observarViewModel() {
        // Observar carga usando BaseActivity
        observarCarga(viewModel.getIsLoading(), progressAgendarCita);
        
        viewModel.getIsLoading().observe(this, isLoading -> {
            btnAgendarCita.setEnabled(!isLoading);
            if (isLoading) {
                btnAgendarCita.setText(getString(R.string.btn_agendando));
            } else {
                btnAgendarCita.setText(getString(R.string.btn_agendar_cita));
            }
        });

        // Observar errores usando BaseActivity
        observarErrores(viewModel.getErrorMessage());

        viewModel.getRegistroExitoso().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                mostrarExito(getString(R.string.cita_agendada_exitoso));
                finish();
            }
        });
    }
}

