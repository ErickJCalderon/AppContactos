package com.example.appcontacto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Clase que implementa los detalles del contacto en la aplicacion
 */
public class DetalleContacto extends AppCompatActivity {

    TextView tvNombre, tvTelefono;
    RelativeLayout relativeLayout;
    String numeroContacto;
    Button botonLlamar;


    /**
     * Metodo onCreate del layout activity_detalle_contacto
     * @param savedInstanceState Bundle de la Activity actual
     */
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

    /**
     * Metodo que controla la accion de llamada del boton referente al mismo, dentro del layout
     * @param v View a la que se refiera
     */
    public void llamar(View v){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+numeroContacto));
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(DetalleContacto.this, "Falta activar los permisos de llamada, activelos en la configuracion de la app", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(intent);
    }
}