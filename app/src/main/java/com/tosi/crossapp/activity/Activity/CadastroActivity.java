package com.tosi.crossapp.activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.tosi.crossapp.R;
import com.tosi.crossapp.activity.Config.configFireBase;
import com.tosi.crossapp.activity.Model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button botaoCadastro;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

    nome = findViewById(R.id.campo_nome);
    email = findViewById(R.id.campo_email2);
    senha = findViewById(R.id.campo_senha2);
    botaoCadastro = findViewById(R.id.button_cadastro);

    botaoCadastro.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            usuario = new Usuario();

            usuario.setNome(nome.getText().toString());
            usuario.setEmail(email.getText().toString());
            usuario.setSenha(senha.getText().toString());

            cadastrarUsuario();
        }
    });

    }

    private void cadastrarUsuario(){
        autenticacao = configFireBase.getAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CadastroActivity.this, "Cadastro realizado!", Toast.LENGTH_LONG).show();

                    FirebaseUser usuarioFirebase = task.getResult().getUser();
                    usuario.setId(usuarioFirebase.getUid());
                    usuario.salvar();

                    autenticacao.signOut();
                    finish();

                } else {

                    String erroExcecao = "";

                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Senha muito curta e/ou fraca!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Email incorreto!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Email já cadastrado!";
                    } catch (Exception e) {
                        erroExcecao = "Erro no cadastro!";
                        e.printStackTrace();
                    }


                    Toast.makeText(CadastroActivity.this, "Erro: "+ erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
