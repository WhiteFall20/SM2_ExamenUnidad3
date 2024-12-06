package com.example.informe_tecnico;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import androidx.core.view.WindowInsetsCompat;

public class PresentationActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.presentation_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Encontrar el botón por su ID
        Button btnIngresar = findViewById(R.id.btnIngresar);

        // Establecer un OnClickListener para el botón
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar LoginActivity
                Intent intent = new Intent(PresentationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Encontrar el botón para salir por su ID
        Button btnSalir = findViewById(R.id.btnSalirApp);

        // Establecer un OnClickListener para el botón de salir
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar la aplicación de forma definitiva
                finishAffinity();  // Cierra todas las actividades
                System.exit(0);    // Termina el proceso para asegurarse de que la app no se ejecuta en segundo plano
            }
        });

    }
}
