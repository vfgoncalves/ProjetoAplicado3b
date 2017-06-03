package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;

public class DetalheVagaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Vaga vaga;
    private String stringJson;
    private JSONObject jsonObject;

    private com.github.clans.fab.FloatingActionButton btnRemover;
    private com.github.clans.fab.FloatingActionButton btnBuscarCandidato;
    private com.github.clans.fab.FloatingActionButton item_Candidaturas;

    private EditText editDetVagaEmailContato;
    private EditText editDetCargo;
    private EditText editDetEstado;
    private EditText editDetCidade;
    private EditText editDetSelecHab;
    private EditText editDetVagaDescricao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_vaga);

        try{

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
            inicializarControles();
            preencherCampos();
            habilitaDesabilitaCampos(false);

            btnRemover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vaga.deletar();
                    finish();
                }
            });

            btnBuscarCandidato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent = new Intent(DetalheVagaActivity.this, BuscaCandidatoActivity.class);
                        //Converter dados da classe em json
                        JSONObject jsonObject = vaga.convertToJson();
                        //enviar dados para activity
                        intent.putExtra("vagaatual", jsonObject.toString());

                        startActivity(intent);
                    }catch (Exception e){
                        e.getMessage();
                    }
                }
            });

            item_Candidaturas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent = new Intent(DetalheVagaActivity.this, CandidaturasActivity.class);
                        //Converter dados da classe em json
                        JSONObject jsonObject = vaga.convertToJson();
                        //enviar dados para activity
                        intent.putExtra("vagaatual", jsonObject.toString());

                        startActivity(intent);
                    }catch (Exception e){
                        e.getMessage();
                    }
                }
            });

        }catch (Exception e){
            e.getMessage();
        }
    }

    private void  inicializarControles(){

        btnRemover = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.item_Remover);
        btnBuscarCandidato = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.item_BuscarCandidatos);
        item_Candidaturas = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.item_Candidaturas);
        editDetVagaEmailContato = (EditText) findViewById(R.id.editDetVagaEmailContatoEmp);
        editDetCargo = (EditText) findViewById(R.id.editDetCargoEmp);
        editDetEstado = (EditText) findViewById(R.id.editDetEstadoEmp);
        editDetCidade = (EditText) findViewById(R.id.editDetCidadeEmp);
        editDetSelecHab = (EditText) findViewById(R.id.editDetSelecHabEmp);
        editDetVagaDescricao = (EditText) findViewById(R.id.editDetVagaDescricaoEmp);

    }
    private void preencherCampos(){
        editDetVagaEmailContato.setText(vaga.getEmailContato());
        editDetCargo.setText(vaga.getCargo());
        editDetEstado.setText(vaga.getEstado());
        editDetCidade.setText(vaga.getCidade());
        editDetSelecHab.setText(vaga.getHabilidades());
        editDetVagaDescricao.setText(vaga.getDescricao());
    }
    private void habilitaDesabilitaCampos(boolean habilita){
        editDetVagaEmailContato.setEnabled(habilita);
        editDetCargo.setEnabled(habilita);
        editDetEstado.setEnabled(habilita);
        editDetCidade.setEnabled(habilita);
        editDetSelecHab.setEnabled(habilita);
        editDetVagaDescricao.setEnabled(habilita);
    }
}
