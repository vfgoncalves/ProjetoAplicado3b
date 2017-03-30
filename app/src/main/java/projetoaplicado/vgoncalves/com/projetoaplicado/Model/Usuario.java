package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;

/**
 * Created by vgoncalves on 29/03/2017.
 */

public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String ID;

    public Usuario() {
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
        DatabaseReference databaseReference = ConfiguracaoFirebase.getReferenceFirebase();
        databaseReference.child("usuario").child(getID()).setValue(this);
    }
}
