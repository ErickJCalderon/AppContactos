package com.example.appcontacto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lvContactos;
    ArrayList<Contacto> contactos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvContactos=(ListView) findViewById(R.id.lvContactos);
        contactos = new ArrayList<>();
        contactos.add(new Contacto("Carlos","Alvarez", "carlos.alvarez@meloinvento.com",632542123));
        contactos.add(new Contacto("Andrea","Gomez", "andrea.gomez@meloinvento.com",654789123));
        contactos.add(new Contacto("Marcos","Calderon", "marcos.calderon@meloinvento.com",698753456));
        contactos.add(new Contacto("Pedro","Nuñez", "pedro.nuñez@meloinvento.com",624542123));
        contactos.add(new Contacto("Sara","Nogales", "sara.nogales@meloinvento.com",618100000));

        ArrayList<String> nombreContactos= new ArrayList<>();

        for (Contacto contacto: contactos){
            nombreContactos.add(contacto.getNombre()+" "+contacto.getApellido());
        }
        lvContactos.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nombreContactos));
    }
}