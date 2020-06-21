package com.tosi.crossapp.activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tosi.crossapp.R;
import com.tosi.crossapp.activity.Config.configFireBase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Toolbar toolbar;
    private Button botaoCheckin;
    private Button botaoCancelar;
    private CalendarView calendario;
    private String usuario_checkin;
    private SeekBar barra_horario;
    private int turma;

    private int data_dia;
    private int data_mes;
    private int data_ano;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Checkin");
        setSupportActionBar(toolbar);
        calendario = findViewById(R.id.calendario_app);
        botaoCheckin = findViewById(R.id.botao_checkin);
        botaoCancelar = findViewById(R.id.botao_cancelar);
        autenticacao = configFireBase.getAutenticacao();

        barra_horario = findViewById(R.id.bar_horario);

        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int ano, int mes, int dia) {
                data_dia = dia;
                data_mes = mes + 1;
                data_ano = ano;
            }
        });

        botaoCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference referenciaUser = configFireBase.getFirebase();
                usuario_checkin = autenticacao.getUid();
                turma = barra_horario.getProgress();

                referenciaUser = referenciaUser.child("checkin")
                                                .child(String.valueOf(data_ano))
                                                .child(String.valueOf(data_mes))
                                                .child(String.valueOf(data_dia));
                if(turma > 0) {
                    referenciaUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            int checagem=Check_Checkin(turma,dataSnapshot);

                            if (checagem == 0) {
                                long count = dataSnapshot.getChildrenCount();
                                if (count < 5) {
                                    DatabaseReference referenciaFirebase = configFireBase.getFirebase();

                                    referenciaFirebase.child("checkin")
                                            .child(String.valueOf(data_ano))
                                            .child(String.valueOf(data_mes))
                                            .child(String.valueOf(data_dia))
                                            .child(String.valueOf(turma))
                                            .child(usuario_checkin).setValue(usuario_checkin);

                                    Toast.makeText(MainActivity.this, "Checkin realizado!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Turma lotada!", Toast.LENGTH_LONG).show();
                                }
                            } else if(checagem == 1) {
                                Toast.makeText(MainActivity.this, "Checkin já realizado em outra turma", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(MainActivity.this, "Checkin já realizado!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "Favor escolher um horário!", Toast.LENGTH_LONG).show();
                }
            }
        });

        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference referenciaUser = configFireBase.getFirebase();
                usuario_checkin = autenticacao.getUid();
                turma = barra_horario.getProgress();

                referenciaUser = referenciaUser.child("checkin")
                        .child(String.valueOf(data_ano))
                        .child(String.valueOf(data_mes))
                        .child(String.valueOf(data_dia));
                if(turma > 0) {
                    referenciaUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            int checagem=Check_Checkin(turma,dataSnapshot);

                            if ((checagem == 0)|| (checagem == 1) ) {
                                Toast.makeText(MainActivity.this, "Checkin inexistente nesta turma", Toast.LENGTH_LONG).show();
                            }else {
                                DatabaseReference referenciaUser = configFireBase.getFirebase();

                                referenciaUser = referenciaUser.child("checkin")
                                        .child(String.valueOf(data_ano))
                                        .child(String.valueOf(data_mes))
                                        .child(String.valueOf(data_dia));

                                referenciaUser.child(String.valueOf(turma))
                                        .child(usuario_checkin)
                                        .removeValue();

                                Toast.makeText(MainActivity.this, "Checkin cancelado", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "Favor escolher um horário!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.action_sobre:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int Check_Checkin(int turma, DataSnapshot dataSnapshot) {

        /*Se 0, náo tem checkin, se 1, checkin outro horário, se 2, checkin no horário  */

        if(dataSnapshot.child(String.valueOf(1)).child(usuario_checkin).getValue() != null){
            if(turma == 1){
                return 2;
            }else{
                return 1;
            }
        }else if(dataSnapshot.child(String.valueOf(2)).child(usuario_checkin).getValue() != null){
            if(turma == 2){
                return 2;
            }else{
                return 1;
            }
        }else if(dataSnapshot.child(String.valueOf(3)).child(usuario_checkin).getValue() != null){
            if(turma == 3){
                return 2;
            }else{
                return 1;
            }
        }else if(dataSnapshot.child(String.valueOf(4)).child(usuario_checkin).getValue() != null){
            if(turma == 4){
                return 2;
            }else{
                return 1;
            }
        }else if(dataSnapshot.child(String.valueOf(5)).child(usuario_checkin).getValue() != null){
            if(turma == 5){
                return 2;
            }else{
                return 1;
            }
        }else{
            return 0;
        }
    }

    public void deslogarUsuario(){
        autenticacao.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
