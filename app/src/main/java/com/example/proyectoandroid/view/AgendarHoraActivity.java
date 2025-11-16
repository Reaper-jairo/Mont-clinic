package com.example.proyectoandroid.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoandroid.R;

public class AgendarHoraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_hora);
        
        // Botón para agendar nueva cita
        Button btnAgendarNueva = findViewById(R.id.btnAgendarNueva);
        if (btnAgendarNueva != null) {
            btnAgendarNueva.setOnClickListener(v -> {
                startActivity(new Intent(this, AgendarCitaActivity.class));
            });
        }
        
        Button volver = findViewById(R.id.volver_1);
        if (volver != null) volver.setOnClickListener(v -> finish());
        
        // Vincular GPS footer
        android.widget.TextView tvGps = findViewById(R.id.tvGpsEstado);
        if (tvGps != null) {
            com.example.proyectoandroid.util.LocationTracker.get().bindTextView(tvGps);
        }
    }
}

