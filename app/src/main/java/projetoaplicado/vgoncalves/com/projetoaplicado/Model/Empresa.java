package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;

/**
 * Created by vgoncalves on 29/03/2017.
 */

public class Empresa {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String ID;
    private String photoUrl;

    public Empresa() {
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    @Exclude
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public void salvar(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getReferenceFirebase();
        databaseReference.child(ConfiguracaoFirebase.NODE_EMPRESA).child(getID()).setValue(this);
    }
}
