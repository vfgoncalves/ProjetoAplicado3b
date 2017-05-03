package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class Filtro {

    private String estado;
    private String cidade;
    private String cargo;
    private String habilidades;
    private String idUsuario;
    private Controller controller;

    public Filtro() {
        controller = new Controller();
    }

    public Filtro(Context context) {
        controller = new Controller(context);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(String habilidades) {
        this.habilidades = habilidades;
    }
    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Exclude
    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void salvar(){
        try{
            DatabaseReference databaseReference = controller.getDatabaseReference();
            databaseReference.child(controller.NODE_FILTROS).child(getIdUsuario()).setValue(this);
        }catch (Exception e){
            Log.d("ProjetoAplicado", e.getMessage());
            throw  e;
        }
    }
}
