package projetoaplicado.vgoncalves.com.projetoaplicado.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
    public static final String NODE_HABILIDADES = "HABILIDADES";
    public static final String NODE_FILTROS = "FILTROS";
    public static final String NODE_CANDIDATURAS = "CANDIDATURAS";

    //URL API GEONAMES
    public static final String URL_API_CIDADES = "http://educacao.dadosabertosbr.com/api/cidades/";

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
    public String montarUrlBuscaCidades(String codEst){
        return URL_API_CIDADES + codEst;
    }

    public String getUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String geraSenhaLinkedin(String email){
        return Base64.encodeToString(email.getBytes(), Base64.DEFAULT);
    }

    public static boolean validaPermissoes(int requestCode , Activity activity, String[] permissoes){
        List<String> listaPermissoes = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23){
            //percorrer permissões e verificar se possuir permissão
            for (String permissao: permissoes){
                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!validaPermissao) listaPermissoes.add(permissao);
            }
            if (listaPermissoes.isEmpty()) return true;

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //Solicita permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes , requestCode);
        }

        return true;
    }

    public Double calcularPorcentagem(int qtdTotal, int qtdHabUser){
        return Double.valueOf((qtdHabUser*100)/qtdTotal);
    }

}
