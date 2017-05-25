package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.CandidatoAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.VagasAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class BuscaCandidatoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Vaga vaga;
    private String stringJson;
    private JSONObject jsonObject;
    private ProgressDialog progressDialog;
    private Controller controller;
    private DatabaseReference databaseReference;
    private ArrayAdapter adapter;
    private ListView listCandidatos;
    private ArrayList<Usuario> listUsuario;
    private ArrayList<Double> listHabPct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_candidato);

        try{
            Bundle extra = getIntent().getExtras();
            //testar dados passados pelo fragment
            if(extra != null){
                stringJson = (String) extra.get("vagaatual");
                jsonObject = new JSONObject(stringJson);
                vaga = new Vaga();
                vaga.readJson(jsonObject);
            }

            incializarControlesConfiguracao();

            iniciarBuscaPorCandidatos();

        }catch (Exception e){
            e.getMessage();
        }
    }

    private void incializarControlesConfiguracao(){
        try{
            toolbar = (Toolbar) findViewById(R.id.tb_vaga);
            toolbar.setTitle(vaga.getTitulo());
            //toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
            setSupportActionBar(toolbar);

            listCandidatos = (ListView) findViewById(R.id.listCandidatos);
            listCandidatos.setDivider(null);
            listCandidatos.setDividerHeight(0);

            controller = new Controller(BuscaCandidatoActivity.this);
            databaseReference = controller.getDatabaseReference();

            //Configurar Progress Dialog
            progressDialog = new ProgressDialog(BuscaCandidatoActivity.this);
            progressDialog.setMessage("Por favor aguarde, estamos buscando candidados para a vaga...");
            progressDialog.setCancelable(false);

            listUsuario = new ArrayList<>();
            listHabPct = new ArrayList<>();

            adapter = new CandidatoAdapter(BuscaCandidatoActivity.this,listUsuario,listHabPct);
            listCandidatos.setAdapter(adapter);


        }catch (Exception e){
            e.getMessage();
        }
    }

    private void iniciarBuscaPorCandidatos(){
        try{
            progressDialog.show();
            final String[] hab = vaga.getHabilidades().split(",");

            //Buscar candidatos
            databaseReference.child(controller.NODE_USUARIO).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        //Percorre todos os usuários
                        for(DataSnapshot users: dataSnapshot.getChildren()){
                            Usuario usuario = users.getValue(Usuario.class);
                            if (usuario.getHabilidades() != null){
                                int qtdHabUserVaga = 0;
                                int qtdHabVaga = hab.length;

                                //Percorre habilidades da vaga por usuário
                                for (int i = 0; i < hab.length; i++) {
                                    if (usuario.getHabilidades().contains(hab[i].trim().replace(",",""))){
                                        qtdHabUserVaga ++;
                                    }
                                }
                                //Verifica se o usuário corrente possui pelo menos uma habilidade da vaga
                                if (qtdHabUserVaga != 0){
                                    listHabPct.add(controller.calcularPorcentagem(qtdHabVaga,qtdHabUserVaga));
                                    listUsuario.add(usuario);
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                        progressDialog.hide();
                    }catch (Exception e){
                        e.getMessage();
                        Toast.makeText(BuscaCandidatoActivity.this,"Erro ao buscar candidatos para a vaga", Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            e.getMessage();
            Toast.makeText(this,"Não foi possível buscar candidatos para a vaga", Toast.LENGTH_LONG);
        }
    }

}
