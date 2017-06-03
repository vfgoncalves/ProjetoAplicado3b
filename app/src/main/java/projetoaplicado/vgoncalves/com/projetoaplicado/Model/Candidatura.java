package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import com.google.firebase.database.DatabaseReference;

import java.security.spec.ECField;

import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

/**
 * Created by vgoncalves on 02/06/2017.
 */

public class Candidatura {

    private String idUsuario;
    private Usuario candidato;

    public Candidatura() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario getCandidato() {
        return candidato;
    }

    public void setCandidato(Usuario candidato) {
        this.candidato = candidato;
    }

    public void salvar(Vaga vaga){
        try{
            Controller controller = new Controller();
            DatabaseReference databaseReference = controller.getDatabaseReference();
            //Adiciona candidatura na vaga
            databaseReference.child(controller.NODE_VAGA)
                    .child(vaga.getEstado())
                    .child(vaga.getCidade())
                    .child(vaga.getCargo())
                    .child(vaga.getIdVaga())
                    .child(controller.NODE_CANDIDATURAS).child(idUsuario).setValue(this);
        }catch (Exception e){
            throw e;
        }
    }
}
