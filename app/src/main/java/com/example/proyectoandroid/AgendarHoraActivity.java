// AgendarHoraActivity.java
package com.example.proyectoandroid;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AgendarHoraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_hora);
        Button volver = findViewById(R.id.volver_1);
        if (volver != null) volver.setOnClickListener(v -> finish());
    }
}
