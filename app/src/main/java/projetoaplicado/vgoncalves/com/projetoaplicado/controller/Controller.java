package projetoaplicado.vgoncalves.com.projetoaplicado.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;

public class Controller {
    //Atributos privados
    private DatabaseReference databaseReference;
    private FirebaseAuth autenticador;
    private Context contexto;
    private SharedPreferences sharedPreferences;

    //Constantes
    public static final String NOME_ARQUIVO = "projetoaplicado.pref";
    private final String CHAVE_IDENTIFICADOR = "idUsuarioLogado";
    private final String CHAVE_MANTER_CONECTADO = "idUsuarioLogado";
    public static final String NODE_USUARIO = "USUARIO";
    public static final String NODE_EMPRESA = "EMPRESA";
    public static final String NODE_VAGA = "VAGAS";

    public Controller(Context context) {
        contexto = context;
        sharedPreferences = context.getSharedPreferences(NOME_ARQUIVO, 0);
    }
    public Controller(){

    }
    public DatabaseReference getDatabaseReference(){
        if (databaseReference == null)
            databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference;
    }
    public FirebaseAuth getAutenticador(){
        if (autenticador == null)
            autenticador = FirebaseAuth.getInstance();
        return autenticador;
    }
    public void salvarPrefIdUsuario(String idUsuario){
        try{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CHAVE_IDENTIFICADOR, idUsuario);
            editor.commit();
        } catch (Exception e){
            throw e;
        }
    }
    public String getIdUsuario(){
        return sharedPreferences.getString(CHAVE_IDENTIFICADOR, "");
    }
    public void limpaIdUsuario(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
