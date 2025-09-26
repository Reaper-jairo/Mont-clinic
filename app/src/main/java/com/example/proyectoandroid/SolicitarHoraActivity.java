package com.example.proyectoandroid;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SolicitarHoraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solicitar_hora);

        Button volver = findViewById(R.id.volver_4);
        volver.setOnClickListener(v -> finish());
    }
}
