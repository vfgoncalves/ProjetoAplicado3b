package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Candidatura;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.CandidatoAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class CandidaturasActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Vaga vaga;
    private String stringJson;
    private JSONObject jsonObject;
    private Controller controller;
    private DatabaseReference databaseReference;
    private ArrayAdapter adapter;
    private ListView listCandidatos;
    private ArrayList<Usuario> listUsuario;
    private ArrayList<Double> listHabPct;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidaturas);
        try {
            Bundle extra = getIntent().getExtras();

            //testar dados passados pelo fragment
            if(extra != null){
                stringJson = (String) extra.get("vagaatual");
                jsonObject = new JSONObject(stringJson);
                vaga = new Vaga();
                vaga.readJson(jsonObject);
            }
            toolbar = (Toolbar) findViewById(R.id.tb_vagaEmp);
            toolbar.setTitle(vaga.getTitulo());
            toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
            setSupportActionBar(toolbar);

            //Inicializar controles
            inicializarControles();

            //Buscar Candidatos
            buscarCandidato();


        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void inicializarControles(){
        try {
            controller = new Controller(this);
            databaseReference = controller.getDatabaseReference();

            listCandidatos = (ListView) findViewById(R.id.listCandidatosEmp);
            listCandidatos.setDivider(null);
            listCandidatos.setDividerHeight(0);

            //Configurar Progress Dialog
            progressDialog = new ProgressDialog(CandidaturasActivity.this);
            progressDialog.setMessage("Por favor aguarde, estamos buscando candidados da vaga...");
            progressDialog.setCancelable(false);

            listUsuario = new ArrayList<>();
            listHabPct = new ArrayList<>();

            adapter = new CandidatoAdapter(CandidaturasActivity.this,listUsuario, listHabPct);
            listCandidatos.setAdapter(adapter);

        }catch (Exception e){
            e.getMessage();
        }
    }
    private void buscarCandidato(){
        try{
            progressDialog.show();
            final String[] hab = vaga.getHabilidades().split(",");

            databaseReference.child(controller.NODE_VAGA)
                    .child(vaga.getEstado())
                    .child(vaga.getCidade())
                    .child(vaga.getCargo())
                    .child(vaga.getIdVaga())
                    .child(controller.NODE_CANDIDATURAS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        for(DataSnapshot candidaturas: dataSnapshot.getChildren()){
                            Candidatura candidatura = candidaturas.getValue(Candidatura.class);
                            Usuario usuario = candidatura.getCandidato();
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
                                listHabPct.add(controller.calcularPorcentagem(qtdHabVaga,qtdHabUserVaga));
                                listUsuario.add(usuario);
                            }

                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.hide();
                    }catch (Exception e){
                        e.getMessage();
                        progressDialog.hide();
                        Toast.makeText(CandidaturasActivity.this, "Não foi possível buscar os candidatos desta vaga!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            e.getMessage();
            progressDialog.hide();
            Toast.makeText(CandidaturasActivity.this, "Não foi possível buscar os candidatos desta vaga!", Toast.LENGTH_LONG).show();
        }




    }
}
