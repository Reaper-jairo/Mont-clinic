package com.example.proyectoandroid.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Medicamento;
import com.example.proyectoandroid.view.adapter.MedicamentoAdapter;
import com.example.proyectoandroid.viewmodel.MedicamentosViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MedicamentosActivity extends BaseActivity {
    private Button btnVolver;
    private MaterialButton btnModoNoche;
    private RecyclerView rvMedicamentos;
    private TextView tvSinMedicamentos;
    private MedicamentoAdapter adapter;
    private MedicamentosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(MedicamentosViewModel.class);

        // Inicializar UI
        btnVolver = findViewById(R.id.volver_1);
        btnModoNoche = findViewById(R.id.btnModoNoche);
        rvMedicamentos = findViewById(R.id.rvMedicamentos);
        tvSinMedicamentos = findViewById(R.id.tvSinMedicamentos);

        // Configurar RecyclerView
        adapter = new MedicamentoAdapter();
        rvMedicamentos.setLayoutManager(new LinearLayoutManager(this));
        rvMedicamentos.setAdapter(adapter);

        // Botón volver
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
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

        // Vincular GPS footer
        TextView tvGps = findViewById(R.id.tvGpsEstado);
        if (tvGps != null) {
            com.example.proyectoandroid.util.LocationTracker.get().bindTextView(tvGps);
        }

        // Observar ViewModel
        observarViewModel();

        // Cargar medicamentos (con actualización en tiempo real)
        viewModel.cargarMedicamentos();
    }

    private void observarViewModel() {
        viewModel.getMedicamentos().observe(this, medicamentos -> {
            if (medicamentos != null && !medicamentos.isEmpty()) {
                adapter.setMedicamentos(medicamentos);
                tvSinMedicamentos.setVisibility(View.GONE);
                rvMedicamentos.setVisibility(View.VISIBLE);
            } else {
                tvSinMedicamentos.setVisibility(View.VISIBLE);
                rvMedicamentos.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            // Puedes mostrar un ProgressBar si quieres
        });

        // Observar errores usando BaseActivity
        observarErrores(viewModel.getErrorMessage());
    }
}

