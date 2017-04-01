package projetoaplicado.vgoncalves.com.projetoaplicado.Config;

import android.content.SharedPreferences;

/**
 * Created by vgoncalves on 01/04/2017.
 */

public class Helper {
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;
    public static final String NOME_ARQUIVO = "projetoaplicado.pref";
    private final String CHAVE_IDENTIFICADOR = "idUsuarioLogado";

    public Helper() {
    }

    public void salvarPreferencia(String idUsuario){
        editor.putString(CHAVE_IDENTIFICADOR, idUsuario);
        editor.commit();
    }
    public String getIdUsuario(){
        return sharedPreferences.getString(CHAVE_IDENTIFICADOR, "");
    }
}
