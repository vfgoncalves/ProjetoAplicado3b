package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;

/**
 * Created by vgoncalves on 29/03/2017.
 */

public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String ID;
    private String photoUrl;

    public Usuario() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    @Exclude
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void salvar(){
        Log.d("ProjetoAplicado", "Método Salvar");
        DatabaseReference databaseReference = ConfiguracaoFirebase.getReferenceFirebase();
        databaseReference.child(ConfiguracaoFirebase.NODE_USUARIO).child(getID()).setValue(this);
    }


}
