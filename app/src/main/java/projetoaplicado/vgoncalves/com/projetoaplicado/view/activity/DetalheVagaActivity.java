package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

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

    private Button btnRemover;
    private Button btnBuscarCandidato;

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
            toolbar = (Toolbar) findViewById(R.id.tb_vaga);
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

        }catch (Exception e){
            e.getMessage();
        }
    }

    private void  inicializarControles(){

        btnRemover = (Button) findViewById(R.id.btnDetRemoverVaga);
        btnBuscarCandidato = (Button) findViewById(R.id.btnDetBuscaCand);
        editDetVagaEmailContato = (EditText) findViewById(R.id.editDetVagaEmailContato);
        editDetCargo = (EditText) findViewById(R.id.editDetCargo);
        editDetEstado = (EditText) findViewById(R.id.editDetEstado);
        editDetCidade = (EditText) findViewById(R.id.editDetCidade);
        editDetSelecHab = (EditText) findViewById(R.id.editDetSelecHab);
        editDetVagaDescricao = (EditText) findViewById(R.id.editDetVagaDescricao);

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
