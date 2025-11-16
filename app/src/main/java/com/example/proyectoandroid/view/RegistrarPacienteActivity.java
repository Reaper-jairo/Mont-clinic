package com.example.proyectoandroid.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.viewmodel.RegistrarPacienteViewModel;

public class RegistrarPacienteActivity extends BaseActivity {

    private EditText etRutNuevo, etEmailNuevo, etPasswordNuevo;
    private EditText etNombreNuevo, etTelefonoNuevo, etDireccionNueva;
    private Button btnRegistrarPaciente;
    private Button btnVolverRegistro;
    private android.widget.ProgressBar progressRegistrarPaciente;
    private RegistrarPacienteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_paciente);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(RegistrarPacienteViewModel.class);

        // Inicializar UI
        etRutNuevo = findViewById(R.id.etRutNuevo);
        etEmailNuevo = findViewById(R.id.etEmailNuevo);
        etPasswordNuevo = findViewById(R.id.etPasswordNuevo);
        etNombreNuevo = findViewById(R.id.etNombreNuevo);
        etTelefonoNuevo = findViewById(R.id.etTelefonoNuevo);
        etDireccionNueva = findViewById(R.id.etDireccionNueva);
        btnRegistrarPaciente = findViewById(R.id.btnRegistrarPaciente);
        btnVolverRegistro = findViewById(R.id.btnVolverRegistro);
        progressRegistrarPaciente = findViewById(R.id.progressRegistrarPaciente);

        // Botón volver al login
        if (btnVolverRegistro != null) {
            btnVolverRegistro.setOnClickListener(v -> finish());
        }

        // Configurar filtros y validaciones
        configurarRut();
        configurarTelefono();

        // Observar ViewModel
        observarViewModel();

        btnRegistrarPaciente.setOnClickListener(v -> registrarPaciente());
        
        // Vincular GPS footer
        android.widget.TextView tvGps = findViewById(R.id.tvGpsEstado);
        if (tvGps != null) {
            com.example.proyectoandroid.util.LocationTracker.get().bindTextView(tvGps);
        }
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
        etRutNuevo.setFilters(new InputFilter[]{rutFilter, new InputFilter.LengthFilter(9)});

        // Convertir a mayúsculas automáticamente
        etRutNuevo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                String upper = text.toUpperCase();
                if (!text.equals(upper)) {
                    etRutNuevo.removeTextChangedListener(this);
                    etRutNuevo.setText(upper);
                    etRutNuevo.setSelection(upper.length());
                    etRutNuevo.addTextChangedListener(this);
                }
            }
        });
    }

    private void configurarTelefono() {
        // Filtro para teléfono: solo números, máximo 9 caracteres (después del +56)
        InputFilter phoneFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        etTelefonoNuevo.setFilters(new InputFilter[]{phoneFilter, new InputFilter.LengthFilter(9)});
    }

    private void registrarPaciente() {
        String rut = etRutNuevo.getText().toString().trim();
        String email = etEmailNuevo.getText().toString().trim();
        String password = etPasswordNuevo.getText().toString();
        String nombre = etNombreNuevo.getText().toString().trim();
        String telefono = etTelefonoNuevo.getText().toString().trim();
        String direccion = etDireccionNueva.getText().toString().trim();

        // Agregar prefijo +56 al teléfono si no está vacío
        if (!TextUtils.isEmpty(telefono)) {
            telefono = "+56" + telefono;
        }

        // Validaciones básicas en UI
        if (TextUtils.isEmpty(rut)) {
            etRutNuevo.setError("RUT es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmailNuevo.setError("Correo es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPasswordNuevo.setError("Contraseña es obligatoria");
            return;
        }

        // Hacer variables final para usar en lambda
        final String rutFinal = rut;
        final String emailFinal = email;
        final String passwordFinal = password;
        final String nombreFinal = nombre;
        final String telefonoFinal = telefono;
        final String direccionFinal = direccion;

        // Mostrar confirmación antes de registrar
        mostrarConfirmacion(
                getString(R.string.titulo_confirmacion),
                getString(R.string.confirmar_registro_paciente),
                () -> {
                    // Confirmar: registrar paciente
                    viewModel.registrarPaciente(rutFinal, emailFinal, passwordFinal, nombreFinal, telefonoFinal, direccionFinal);
                },
                null // Cancelar: no hacer nada
        );
    }

    private void observarViewModel() {
        // Observar carga usando BaseActivity
        observarCarga(viewModel.getIsLoading(), progressRegistrarPaciente);
        
        viewModel.getIsLoading().observe(this, isLoading -> {
            btnRegistrarPaciente.setEnabled(!isLoading);
            if (isLoading) {
                btnRegistrarPaciente.setText(getString(R.string.btn_registrando));
            } else {
                btnRegistrarPaciente.setText(getString(R.string.btn_registrar_paciente));
            }
        });

        // Observar errores usando BaseActivity
        observarErrores(viewModel.getErrorMessage());

        viewModel.getRegistroExitoso().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                mostrarExito(getString(R.string.registro_exitoso));
                finish();
            }
        });
    }
}

