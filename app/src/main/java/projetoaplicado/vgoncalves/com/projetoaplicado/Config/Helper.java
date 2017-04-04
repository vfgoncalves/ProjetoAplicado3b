package projetoaplicado.vgoncalves.com.projetoaplicado.Config;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
    public Bitmap baixarImagem(String url) {
        try{
            URL endereco;
            InputStream inputStream;
            Bitmap imagem;
            endereco = new URL(url);
            imagem = BitmapFactory.decodeStream(endereco.openConnection().getInputStream());
            //inputStream.close();
            return imagem;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
