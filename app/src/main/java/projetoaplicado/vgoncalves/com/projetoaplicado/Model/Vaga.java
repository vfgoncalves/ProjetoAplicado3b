package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import com.google.firebase.database.DatabaseReference;

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

    public void salvar(){
        DatabaseReference databaseReference = controller.getDatabaseReference();
        databaseReference.child(controller.NODE_VAGA).push().setValue(this);
    }
}
