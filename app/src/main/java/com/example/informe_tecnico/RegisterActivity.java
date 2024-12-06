package com.example.informe_tecnico;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTxtNombre, editTxtApellido, editTxtEmailR, editTxtPasswordR, editTxtPasswordRepeatR;
    private Button btnGuardarUsuario, btnCancelUsuario;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerlogin_main);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Enlazar vistas con los elementos del layout
        editTxtNombre = findViewById(R.id.EditTxtNombre);
        editTxtApellido = findViewById(R.id.EditTxtApellido);
        editTxtEmailR = findViewById(R.id.EditTxtEmailR);
        editTxtPasswordR = findViewById(R.id.EditTxtPasswordR);
        editTxtPasswordRepeatR = findViewById(R.id.EditTxtPasswordRepeatR);
        btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        btnCancelUsuario = findViewById(R.id.btnCancelUsuario);

        // Botón para cancelar el registro y volver al login
        btnCancelUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finaliza el RegisterActivity
            }
        });

        // Botón para guardar el nuevo usuario
        btnGuardarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        String nombre = editTxtNombre.getText().toString().trim();
        String apellido = editTxtApellido.getText().toString().trim();
        String email = editTxtEmailR.getText().toString().trim();
        String password = editTxtPasswordR.getText().toString().trim();
        String passwordRepeat = editTxtPasswordRepeatR.getText().toString().trim();

        // Validar campos vacíos
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(passwordRepeat)) {
            Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario en Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Usuario registrado con éxito, guardar datos en Firestore
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid(), nombre, apellido, email);
                        }
                    } else {
                        // Si falla el registro, mostrar un mensaje al usuario
                        Toast.makeText(RegisterActivity.this, "Registro fallido: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String uid, String nombre, String apellido, String email) {
        // Crear un mapa con los datos del usuario
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Nombre", nombre);
        userMap.put("Apellido", apellido);
        userMap.put("Correo", email);

        // Guardar los datos en la colección "Usuario" en Firestore
        db.collection("Usuario").document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                    // Volver a la pantalla de presentación
                    Intent intent = new Intent(RegisterActivity.this, PresentationActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
