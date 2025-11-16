package com.example.proyectoandroid.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Atencion;
import com.example.proyectoandroid.model.Cita;
import com.example.proyectoandroid.model.Paciente;
import com.example.proyectoandroid.view.adapter.AtencionAdapter;
import com.example.proyectoandroid.view.adapter.CitaAdapter;
import com.example.proyectoandroid.viewmodel.InformacionViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.firebase.auth.FirebaseAuth;

import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;

public class InformacionActivity extends BaseActivity {

    // --- UI (footer GPS) ---
    private TextView tvGpsEstado;
    
    // --- UI (modo noche y brillo) ---
    private MaterialButton btnModoNoche;
    private MaterialButton btnBrilloAuto;

    // --- UI (datos del paciente) ---
    private TextView tvBienvenida;
    private TextView tvNombrePaciente;
    private TextView tvRutPaciente;
    private TextView tvCorreoPaciente;
    private RecyclerView rvUltimasAtenciones;
    private RecyclerView rvProximasCitas;
    private TextView tvSinAtenciones;
    private TextView tvSinCitas;

    // --- Adapters ---
    private AtencionAdapter atencionAdapter;
    private CitaAdapter citaAdapter;

    // --- ViewModel ---
    private InformacionViewModel viewModel;

    // LocationTracker maneja todo automáticamente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(InformacionViewModel.class);

        // --- Inicializar UI ---
        inicializarUI();

        // --- Nav Rail ---
        NavigationRailView nav = findViewById(R.id.navRail);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.itAgenda) {
                startActivity(new Intent(this, AgendarCitaActivity.class));
                return true;
            } else if (id == R.id.itSolicitar) {
                startActivity(new Intent(this, SolicitarHoraActivity.class));
                return true;
            } else if (id == R.id.itMedicamentos) {
                startActivity(new Intent(this, MedicamentosActivity.class));
                return true;
            } else if (id == R.id.itVolver) {
                volverAlLogin();
                return true;
            }
            return false;
        });

        // Botón físico "atrás"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { volverAlLogin(); }
        });

        // --- Footer GPS views ---
        tvGpsEstado = findViewById(R.id.tvGpsEstado);
        if (tvGpsEstado != null) {
            // Vincular con LocationTracker para obtener direcciones automáticamente
            com.example.proyectoandroid.util.LocationTracker.get().bindTextView(tvGpsEstado);
        }

        // --- Botón brillo automático ---
        btnBrilloAuto = findViewById(R.id.btnBrilloAuto);
        if (btnBrilloAuto != null && brightnessManager != null) {
            // Actualizar icono según el estado actual
            boolean isAutoBrightnessEnabled = brightnessManager.getUserPreference();
            btnBrilloAuto.setText(isAutoBrightnessEnabled ? "☀️" : "🌑");
            
            btnBrilloAuto.setOnClickListener(v -> {
                boolean currentState = brightnessManager.getUserPreference();
                boolean newState = !currentState;
                brightnessManager.setUserPreference(newState);
                
                // Actualizar icono
                btnBrilloAuto.setText(newState ? "☀️" : "🌑");
                
                // Mostrar mensaje informativo
                if (newState) {
                    mostrarInfo(getString(R.string.brillo_auto_activado));
                } else {
                    mostrarInfo(getString(R.string.brillo_auto_desactivado));
                }
            });
        }
        
        // --- Botón modo noche ---
        btnModoNoche = findViewById(R.id.btnModoNoche);
        if (btnModoNoche != null) {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
            btnModoNoche.setText(isNightMode ? "☀️" : "🌙");
            
            btnModoNoche.setOnClickListener(v -> {
                int nightMode = AppCompatDelegate.getDefaultNightMode();
                if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    btnModoNoche.setText("🌙");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    btnModoNoche.setText("☀️");
                }
            });
        }

        // Observar cambios del ViewModel
        observarViewModel();

        // Cargar datos del paciente
        viewModel.cargarDatosPaciente();
    }

    /**
     * Inicializa los componentes de UI
     */
    private void inicializarUI() {
        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvNombrePaciente = findViewById(R.id.tvNombrePaciente);
        tvRutPaciente = findViewById(R.id.tvRutPaciente);
        tvCorreoPaciente = findViewById(R.id.tvCorreoPaciente);
        rvUltimasAtenciones = findViewById(R.id.rvUltimasAtenciones);
        rvProximasCitas = findViewById(R.id.rvProximasCitas);
        tvSinAtenciones = findViewById(R.id.tvSinAtenciones);
        tvSinCitas = findViewById(R.id.tvSinCitas);

        // Configurar RecyclerViews
        atencionAdapter = new AtencionAdapter();
        rvUltimasAtenciones.setLayoutManager(new LinearLayoutManager(this));
        rvUltimasAtenciones.setAdapter(atencionAdapter);

        citaAdapter = new CitaAdapter();
        rvProximasCitas.setLayoutManager(new LinearLayoutManager(this));
        rvProximasCitas.setAdapter(citaAdapter);
    }

    /**
     * Observa los cambios del ViewModel y actualiza la UI
     */
    private void observarViewModel() {
        // LocationTracker ya maneja las actualizaciones de ubicación automáticamente
        // No necesitamos observar LocationState aquí

            // Observar datos del paciente
            viewModel.getPaciente().observe(this, paciente -> {
                if (paciente != null) {
                    String nombre = paciente.getNombre() != null ? paciente.getNombre() : "Usuario";
                    tvBienvenida.setText(getString(R.string.bienvenido_usuario, nombre));
                    // Los labels ya están en el layout, solo mostrar los valores
                    tvNombrePaciente.setText(paciente.getNombre() != null ? paciente.getNombre() : getString(R.string.na));
                    tvRutPaciente.setText(paciente.getRut() != null ? paciente.getRut() : getString(R.string.na));
                    tvCorreoPaciente.setText(paciente.getEmail() != null ? paciente.getEmail() : getString(R.string.na));
                }
            });

        // Observar atenciones
        viewModel.getAtenciones().observe(this, atenciones -> {
            if (atenciones != null && !atenciones.isEmpty()) {
                atencionAdapter.setAtenciones(atenciones);
                tvSinAtenciones.setVisibility(android.view.View.GONE);
                rvUltimasAtenciones.setVisibility(android.view.View.VISIBLE);
            } else {
                tvSinAtenciones.setVisibility(android.view.View.VISIBLE);
                rvUltimasAtenciones.setVisibility(android.view.View.GONE);
            }
        });

        // Observar citas
        viewModel.getCitas().observe(this, citas -> {
            if (citas != null && !citas.isEmpty()) {
                citaAdapter.setCitas(citas);
                tvSinCitas.setVisibility(android.view.View.GONE);
                rvProximasCitas.setVisibility(android.view.View.VISIBLE);
            } else {
                tvSinCitas.setVisibility(android.view.View.VISIBLE);
                rvProximasCitas.setVisibility(android.view.View.GONE);
            }
        });
    }

    // ---------- Navegación: volver al login ----------
    private void volverAlLogin() {
        viewModel.cerrarSesion();
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    // ---------- Flujo GPS ----------
    // LocationTracker maneja todo automáticamente, no necesitamos estos métodos
}

