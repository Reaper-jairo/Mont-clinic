package com.example.proyectoandroid.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.RutValidation;
import com.example.proyectoandroid.util.LocationTracker;
import com.example.proyectoandroid.viewmodel.LoginViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity {

    // UI
    private EditText etRut, etPassword;
    private Button btnIngresar;
    private Button btnRegistrarse;
    private Button btnModoNoche;

    // ViewModel
    private LoginViewModel viewModel;

    // Permisos de ubicación (para LocationTracker global)
    private ActivityResultLauncher<String[]> permisosUbicacionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setContentView(R.layout.activity_main);

        etRut       = findViewById(R.id.etRut);
        etPassword  = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnModoNoche = findViewById(R.id.btnModoNoche);

        // Observar cambios del ViewModel
        observarViewModel();

        // -------- GPS global: pedir permisos al entrar y arrancar el tracker --------
        permisosUbicacionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    boolean fine   = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false));
                    boolean coarse = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false));
                    if (fine || coarse) {
                        // Inicia el tracker compartido (singleton) y vincula el footer si está en este layout
                        LocationTracker.get().start();
                        TextView tv = findViewById(R.id.tvGpsEstado);
                        if (tv != null) LocationTracker.get().bindTextView(tv);
                    }
                });

        if (tienePermisosUbicacion()) {
            LocationTracker.get().start();
        } else {
            permisosUbicacionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        // Vincula el footer si existe en este layout (no rompe si no está)
        TextView tvFooter = findViewById(R.id.tvGpsEstado);
        if (tvFooter != null) LocationTracker.get().bindTextView(tvFooter);

        // --------- RUT: filtro de entrada (solo dígitos + K/k) y mayúsculas automáticas ---------
        InputFilter soloRutChars = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = src.charAt(i);
                    if (!(Character.isDigit(c) || c == 'k' || c == 'K')) return "";
                }
                return null;
            }
        };
        etRut.setFilters(new InputFilter[]{soloRutChars, new InputFilter.LengthFilter(9)});

        etRut.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                String up = s.toString().toUpperCase();
                if (!up.equals(s.toString())) {
                    etRut.removeTextChangedListener(this);
                    etRut.setText(up);
                    etRut.setSelection(up.length());
                    etRut.addTextChangedListener(this);
                }
            }
        });

        // --------- Botón Ingresar ---------
        btnIngresar.setOnClickListener(v -> {
            String rut = etRut.getText().toString();
            String pass = etPassword.getText().toString();

            // Validar RUT
            viewModel.validarRut(rut);
            
            // Validar contraseña
            if (!viewModel.validarPassword(pass)) {
                etPassword.setError(getString(R.string.error_password_corta));
                return;
            }

            // Iniciar sesión a través del ViewModel
            viewModel.iniciarSesion(rut, pass);
        });

        // Botón para registrarse
        if (btnRegistrarse != null) {
            btnRegistrarse.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, RegistrarPacienteActivity.class));
            });
        }

        // Botón modo noche
        if (btnModoNoche != null) {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
                btnModoNoche.setText(isNightMode ? getString(R.string.modo_dia_icon) : getString(R.string.modo_noche_icon));
            
            btnModoNoche.setOnClickListener(v -> {
                int nightMode = AppCompatDelegate.getDefaultNightMode();
                if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    btnModoNoche.setText(getString(R.string.modo_noche_icon));
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    btnModoNoche.setText(getString(R.string.modo_dia_icon));
                }
            });
        }
    }

    /**
     * Observa los cambios del ViewModel y actualiza la UI
     */
    private void observarViewModel() {
        // Observar validación de RUT
        viewModel.getRutValidation().observe(this, validation -> {
            if (validation != null && !validation.isValid()) {
                etRut.setError(validation.getErrorMessage());
            } else {
                etRut.setError(null);
            }
        });

        // Observar estado de carga
        viewModel.getIsLoading().observe(this, isLoading -> {
            btnIngresar.setEnabled(!isLoading);
        });

        // Observar errores usando BaseActivity
        observarErrores(viewModel.getErrorMessage());

        // Observar éxito de login
        viewModel.getLoginSuccess().observe(this, success -> {
            if (success != null && success) {
                mostrarExito(getString(R.string.login_success));
                irAInformacion();
            }
        });
    }

    // Comentado: Ya no redirige automáticamente si hay usuario autenticado
    // El usuario debe iniciar sesión manualmente cada vez
    // @Override
    // protected void onStart() {
    //     super.onStart();
    //     FirebaseUser current = viewModel.getCurrentUser();
    //     if (current != null) irAInformacion();
    // }

    private void irAInformacion() {
        startActivity(new Intent(MainActivity.this, InformacionActivity.class));
        finish(); // no volver al login con back
    }

    // ----------------- Permisos ubicación helper -----------------
    private boolean tienePermisosUbicacion() {
        int fine   = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED;
    }
}

