package com.tosi.crossapp.activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tosi.crossapp.R;
import com.tosi.crossapp.activity.Config.configFireBase;
import com.tosi.crossapp.activity.Model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button botaoLogar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();

        email = findViewById(R.id.campo_email);
        senha = findViewById(R.id.campo_senha);
        botaoLogar = findViewById(R.id.button_login);

        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 usuario = new Usuario();
                 usuario.setEmail(email.getText().toString());
                 usuario.setSenha(senha.getText().toString());
                 validarLogin();
            }
        });

    }

    private void verificarUsuarioLogado(){
        autenticacao = configFireBase.getAutenticacao();
        if(autenticacao.getCurrentUser()!=null){
            abrirTelaPrincipal();
        }
    }

    private void validarLogin(){
        autenticacao = configFireBase.getAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    abrirTelaPrincipal();
                    Toast.makeText(LoginActivity.this, "Login com Sucesso!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Erro no Login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal(){
         Intent intent = new Intent(LoginActivity.this, CheckinActivity.class);
         startActivity(intent);
         finish();
    }


    public void abrirCadastroUsuario(View view){

        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }


}
