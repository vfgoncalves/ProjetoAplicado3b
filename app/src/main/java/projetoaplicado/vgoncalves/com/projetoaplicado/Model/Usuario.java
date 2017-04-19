package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

/**
 * Created by vgoncalves on 29/03/2017.
 */

public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String ID;
    private String photoUrl;
    private String telefoneCel;
    private String telefoneResidencial;
    private String CEP;
    private String Pais;
    private String Estado;
    private String Cidade;
    private String Bairro;
    private String Rua;
    private String Numero;
    private String Complemento;

    private Controller controller;

    public Usuario() {
        controller = new Controller();
    }
    public Usuario(Context context) {
        controller = new Controller(context);
    }

    public String getTelefoneCel() {
        return telefoneCel;
    }

    public void setTelefoneCel(String telefoneCel) {
        this.telefoneCel = telefoneCel;
    }

    public String getTelefoneResidencial() {
        return telefoneResidencial;
    }

    public void setTelefoneResidencial(String telefoneResidencial) {
        this.telefoneResidencial = telefoneResidencial;
    }

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public String getPais() {
        return Pais;
    }

    public void setPais(String pais) {
        Pais = pais;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }

    public String getBairro() {
        return Bairro;
    }

    public void setBairro(String bairro) {
        Bairro = bairro;
    }

    public String getRua() {
        return Rua;
    }

    public void setRua(String rua) {
        Rua = rua;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getComplemento() {
        return Complemento;
    }

    public void setComplemento(String complemento) {
        Complemento = complemento;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
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
        DatabaseReference databaseReference = controller.getDatabaseReference();
        databaseReference.child(controller.NODE_USUARIO).child(getID()).setValue(this);
        controller.salvarPrefIdUsuario(getID());
    }


}
