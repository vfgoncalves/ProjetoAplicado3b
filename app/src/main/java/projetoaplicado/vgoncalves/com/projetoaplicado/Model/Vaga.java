package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

/**
 * Created by vgoncalves on 04/04/2017.
 */

public class Vaga {
    private String titulo;
    private String cidade;
    private String emailContato;
    private String descricao;
    private String idEmpresa;
    private String estado;
    private String cargo;
    private String idVaga;
    private String dataVaga;
    private String habilidades;
    private String nomeEmpresa;

    private Controller controller;

    public Vaga() {
        controller = new Controller();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getIdVaga() {
        return idVaga;
    }

    public void setIdVaga(String idVaga) {
        this.idVaga = idVaga;
    }

    public String getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(String habilidades) {
        this.habilidades = habilidades;
    }

    public String getDataVaga() {
        return dataVaga;
    }

    public void setDataVaga(String dataVaga) {
        this.dataVaga = dataVaga;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public void salvar(){
        try {
            DatabaseReference databaseReference = controller.getDatabaseReference();
            databaseReference.child(controller.NODE_VAGA).child(getEstado()).child(getCidade()).child(getCargo()).child(getIdVaga()).setValue(this);
        }catch (Exception e){
            e.getMessage();
        }
    }
}
