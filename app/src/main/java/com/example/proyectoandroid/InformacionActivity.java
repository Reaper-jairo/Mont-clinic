package com.example.proyectoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class InformacionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion);

        // Volver a MainActivity
        Button volver = findViewById(R.id.volver_2);
        volver.setOnClickListener(v -> finish());

        // Agenda de horas
        Button agenda = findViewById(R.id.agendaDeHora);
        agenda.setOnClickListener(v ->
                startActivity(new Intent(this, AgendarHoraActivity.class)));

        // Solicitar hora
        Button solicitar = findViewById(R.id.SolicitarHora);
        solicitar.setOnClickListener(v ->
                startActivity(new Intent(this, SolicitarHoraActivity.class)));

        // Medicamentos
        Button medicamentos = findViewById(R.id.ListaDeMedicamentos);
        medicamentos.setOnClickListener(v ->
                startActivity(new Intent(this, MedicamentosActivity.class)));
    }
}
