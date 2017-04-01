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
    private String CEP;
    private String Pais;
    private String Estado;
    private String Cidade;
    private String Bairro;
    private String Rua;
    private String Numero;
    private String Complemento;

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

    public void setID(String ID) {
        this.ID = ID;
    }
    public void salvar(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getReferenceFirebase();
        databaseReference.child(ConfiguracaoFirebase.NODE_EMPRESA).child(getID()).setValue(this);
    }
}
