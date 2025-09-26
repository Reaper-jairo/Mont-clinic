package com.example.proyectoandroid;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MedicamentosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicamentos);

        Button volver = findViewById(R.id.volver_3);
        volver.setOnClickListener(v -> finish());
    }
}
