package com.example.proyectoandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    // UI
    private EditText etRut, etPassword;
    private Button btnIngresar;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Permisos de ubicación (para LocationTracker global)
    private ActivityResultLauncher<String[]> permisosUbicacionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_main);

        etRut       = findViewById(R.id.etRut);
        etPassword  = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);

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
        etRut.setFilters(new InputFilter[]{soloRutChars, new InputFilter.LengthFilter(10)});

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
            String rutNorm = normalizarRut(etRut.getText().toString());
            String pass    = etPassword.getText().toString();

            if (rutNorm.length() < 2) { etRut.setError("Ingresa RUT sin guion. Ej: 19875613K"); return; }
            if (!esRutValidoSinGuion(rutNorm)) { etRut.setError("RUT inválido. Revisa dígitos y DV."); return; }
            if (TextUtils.isEmpty(pass) || pass.length() < 6) { etPassword.setError("Mínimo 6 caracteres"); return; }

            btnIngresar.setEnabled(false);

            // Busca documento por RUT en upper y, si no existe, prueba lower (por si quedó con 'k' minúscula)
            db.collection("rut_index").document(rutNorm).get()
                    .addOnSuccessListener(doc -> {
                        if (doc != null && doc.exists()) {
                            String email = doc.getString("email");
                            if (email == null || email.trim().isEmpty()) {
                                btnIngresar.setEnabled(true);
                                Toast.makeText(this, "No hay email asociado al RUT. Contacte CESFAM.", Toast.LENGTH_LONG).show();
                            } else {
                                iniciarSesion(email.trim(), pass);
                            }
                        } else {
                            // reintenta en minúsculas
                            db.collection("rut_index").document(rutNorm.toLowerCase()).get()
                                    .addOnSuccessListener(doc2 -> {
                                        if (doc2 != null && doc2.exists()) {
                                            String email2 = doc2.getString("email");
                                            if (email2 == null || email2.trim().isEmpty()) {
                                                btnIngresar.setEnabled(true);
                                                Toast.makeText(this, "No hay email asociado al RUT. Contacte CESFAM.", Toast.LENGTH_LONG).show();
                                            } else {
                                                iniciarSesion(email2.trim(), pass);
                                            }
                                        } else {
                                            btnIngresar.setEnabled(true);
                                            Toast.makeText(this, "RUT no registrado. Contacte CESFAM.", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        btnIngresar.setEnabled(true);
                                        Toast.makeText(this, "Error consultando RUT: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        btnIngresar.setEnabled(true);
                        Toast.makeText(this, "Error consultando RUT: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current = auth.getCurrentUser();
        if (current != null) irAInformacion();
    }

    private void iniciarSesion(String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {
                    Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
                    irAInformacion();
                })
                .addOnFailureListener(e -> {
                    btnIngresar.setEnabled(true);
                    Toast.makeText(this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void irAInformacion() {
        startActivity(new Intent(MainActivity.this, InformacionActivity.class));
        finish(); // no volver al login con back
    }

    // ----------------- Utilidades de RUT -----------------
    private String normalizarRut(String raw) {
        if (raw == null) return "";
        return raw.replace("-", "").replace(".", "").replace(" ", "").toUpperCase();
    }

    private boolean esRutValidoSinGuion(String rutSinGuion) {
        if (rutSinGuion == null || rutSinGuion.length() < 2) return false;
        char dv = rutSinGuion.charAt(rutSinGuion.length() - 1);
        String cuerpo = rutSinGuion.substring(0, rutSinGuion.length() - 1);
        if (cuerpo.isEmpty()) return false;
        for (int i = 0; i < cuerpo.length(); i++) if (!Character.isDigit(cuerpo.charAt(i))) return false;
        if (!(Character.isDigit(dv) || dv == 'K')) return false;
        return calcularDV(cuerpo).equals(String.valueOf(dv));
    }

    private String calcularDV(String cuerpo) {
        int suma = 0, factor = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * factor;
            factor = (factor == 7) ? 2 : factor + 1;
        }
        int dv = 11 - (suma % 11);
        if (dv == 11) return "0";
        if (dv == 10) return "K";
        return String.valueOf(dv);
    }

    // ----------------- Permisos ubicación helper -----------------
    private boolean tienePermisosUbicacion() {
        int fine   = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED;
    }
}
