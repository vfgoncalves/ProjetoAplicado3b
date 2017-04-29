package projetoaplicado.vgoncalves.com.projetoaplicado.Model;

import com.google.firebase.database.DatabaseReference;

import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class Habilidades {

    private Controller controller;
    private String descricao;

    public Habilidades() {
        controller = new Controller();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao.toUpperCase();
    }

    public Boolean salvar(){
        try{
            DatabaseReference databaseReference = controller.getDatabaseReference();
            databaseReference.child(controller.NODE_HABILIDADES).child(controller.getUUID()).setValue(this);
            return  true;
        }catch (Exception e){
            e.getMessage();
            return  false;
        }
    }


}
