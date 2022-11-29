package com.example.appcontacto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Acitividad inicial para poder ingresar a la aplicacion
 */
public class AuthActivity extends AppCompatActivity {
    /**
     * The Boton entrar.
     */
    Button boton_entrar, /**
     * The Boton registrar.
     */
    boton_registrar, /**
     * The Boton olvidar.
     */
    boton_olvidar;
    /**
     * The Email.
     */
    EditText email, /**
     * The Contraseña.
     */
    contraseña;
    /**
     * The Firebase auth.
     */
    FirebaseAuth firebaseAuth;
    /**
     * The Awesome validation.
     */
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        email = findViewById(R.id.editTextTextEmailAddress);
        contraseña= findViewById(R.id.editTextTextPassword);
        boton_entrar = findViewById(R.id.botonEntrar);
        boton_registrar = findViewById(R.id.botonRegistrarse);
        boton_olvidar = findViewById(R.id.botonOlvidar);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth usuarioActual = FirebaseAuth.getInstance();
        FirebaseUser usuario = usuarioActual.getCurrentUser();

        if (usuario != null){
            pantallaPrincipal();
        }

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.emailRegistro, Patterns.EMAIL_ADDRESS, R.string.mail_invalido);
        awesomeValidation.addValidation(this, R.id.contraseñaRegistro, ".{6,}", R.string.contraseña_invalida);


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
                if (awesomeValidation.validate()){
                    String mail = email.getText().toString();
                    String pass = contraseña.getText().toString();

                    firebaseAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                pantallaPrincipal();
                            }else{
                                String errorCode = ((FirebaseAuthException)task.getException()).getErrorCode();
                                posiblesToastErrors(errorCode);
                            }
                        }
                    });
                }
            }
        });

        boton_olvidar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent( AuthActivity. this, OlvidarActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Metodo que te lleva a la pantalla inicial de la app donde se gestionan los usuarios del contacto
     */
    private void pantallaPrincipal(){
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("email", email.getText().toString());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /**
     * Metodo que controla todos los posibles erroes de escritura con respecto al awesomeValidation
     * @param error
     */
    private void posiblesToastErrors(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(AuthActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(AuthActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(AuthActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(AuthActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                email.setError("La dirección de correo electrónico está mal formateada.");
                email.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(AuthActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                contraseña.setError("la contraseña es incorrecta ");
                contraseña.requestFocus();
                contraseña.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(AuthActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(AuthActivity.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(AuthActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(AuthActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                email.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                email.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(AuthActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(AuthActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(AuthActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(AuthActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(AuthActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(AuthActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(AuthActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                contraseña.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                contraseña.requestFocus();
                break;

        }

    }
}