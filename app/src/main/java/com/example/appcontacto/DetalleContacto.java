package com.example.appcontacto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetalleContacto extends AppCompatActivity {

    TextView tvNombre, tvApellido, tvEmail, tvTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contacto);

        Bundle bundle = getIntent().getExtras();
        String nombreContacto= bundle.getString("NOMBRECONTACTO");
        String apellidoContacto= bundle.getString("APELLIDOCONTACTO");
        String emailContacto= bundle.getString("EMAILCONTACTO");
        String numeroContacto= bundle.getString("NUMERO");

        tvNombre = findViewById(R.id.tvNombre);
        tvApellido = findViewById(R.id.tvApellido);
        tvEmail = findViewById(R.id.tvEmail);
        tvTelefono = findViewById(R.id.tvTelefono);

        tvNombre.setText(nombreContacto);
        tvApellido.setText(apellidoContacto);
        tvEmail.setText(emailContacto);
        tvTelefono.setText(numeroContacto);
    }
}