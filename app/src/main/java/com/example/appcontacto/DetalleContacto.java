package com.example.appcontacto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetalleContacto extends AppCompatActivity {

    TextView tvNombre, tvTelefono;
    RelativeLayout relativeLayout;
    String numeroContacto;
    Button botonLlamar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contacto);

        Bundle bundle = getIntent().getExtras();
        String nombreContacto= bundle.getString("NOMBRECONTACTO");
        numeroContacto= bundle.getString("NUMERO");

        tvNombre = findViewById(R.id.tvNombre);
        tvTelefono = findViewById(R.id.tvTelefono);
        relativeLayout= findViewById(R.id.relativeCall);
        botonLlamar = findViewById(R.id.botonLlamar);

        tvNombre.setText(nombreContacto);
        tvTelefono.setText(numeroContacto);
        relativeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+numeroContacto));
            startActivity(intent);
        });
    }
    public void llamar(View v){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+numeroContacto));
        startActivity(intent);
    }
}