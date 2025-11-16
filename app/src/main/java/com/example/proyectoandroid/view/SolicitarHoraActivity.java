package com.example.proyectoandroid.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoandroid.R;

public class SolicitarHoraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_hora);
        
        // Botón para solicitar nueva cita
        Button btnSolicitarNueva = findViewById(R.id.btnSolicitarNueva);
        if (btnSolicitarNueva != null) {
            btnSolicitarNueva.setOnClickListener(v -> {
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

