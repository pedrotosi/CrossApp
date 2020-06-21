package com.tosi.crossapp.activity.Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class configFireBase {

    private static DatabaseReference referenciaDatabase;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getFirebase(){

        if (referenciaDatabase == null) {
            referenciaDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaDatabase;
    }

    public static FirebaseAuth getAutenticacao(){
        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;


    }

}
