package com.example.appcontacto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AuthActivity extends AppCompatActivity {
 Button boton_entrar,boton_registrar,boton_olvidar;
 EditText email,contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        email = findViewById(R.id.editTextTextEmailAddress);
        contraseña= findViewById(R.id.editTextTextPassword);
        boton_entrar = findViewById(R.id.botonEntrar);
        boton_registrar = findViewById(R.id.botonRegistrarse);
        boton_olvidar = findViewById(R.id.botonOlvidar);

        boton_registrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent( AuthActivity. this, RegistrarActivity.class);
                startActivity(i);
            }
        });

        boton_entrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent( AuthActivity. this, RegistrarActivity.class);
            }
        });

        boton_olvidar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent( AuthActivity. this, RegistrarActivity.class);
            }
        });
    }
}