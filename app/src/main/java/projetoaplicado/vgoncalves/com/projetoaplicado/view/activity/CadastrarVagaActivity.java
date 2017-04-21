package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class CadastrarVagaActivity extends AppCompatActivity {

    private EditText editVagaTitulo;
    private EditText editVagaEmailContato;
    private EditText editVagaDescricao;
    private FloatingActionButton floatCadastrarVaga;
    private Vaga vaga;
    private String idUsuario;
    private Controller controller;
    private Spinner spnCargos;
    private Spinner spnEstados;
    private Spinner spnCidades;

    private ArrayList<String> codigoEstado;
    private ArrayList<String> nomeEstado;

    private ArrayList<String> codigoCidade;
    private ArrayList<String> nomeCidade;

    ProgressDialog progressDialogEstados;
    ProgressDialog progressDialogCidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_vaga);

        controller = new Controller(CadastrarVagaActivity.this);
        idUsuario = controller.getIdUsuario();

        //Inicializar controles
        editVagaTitulo = (EditText) findViewById(R.id.editVagaTitulo);
        editVagaEmailContato = (EditText) findViewById(R.id.editVagaEmailContato);
        editVagaDescricao = (EditText) findViewById(R.id.editVagaDescricao);
        floatCadastrarVaga = (FloatingActionButton) findViewById(R.id.floatCadastrarVaga);
        spnCargos = (Spinner) findViewById(R.id.spnCargo);
        spnEstados = (Spinner) findViewById(R.id.spnEstado);
        spnCidades = (Spinner) findViewById(R.id.spnCidade);

        //Configurar ProgressDialogs
        progressDialogEstados = new ProgressDialog(CadastrarVagaActivity.this);
        progressDialogEstados.setTitle("Buscando estados");
        progressDialogEstados.setMessage("Carregando estados brasileiros...");
        progressDialogEstados.setCancelable(false);

        progressDialogCidades = new ProgressDialog(CadastrarVagaActivity.this);
        progressDialogCidades.setTitle("Buscando cidades");
        progressDialogCidades.setMessage("Carregando cidades...");
        progressDialogCidades.setCancelable(false);

        //Instancia do adapter para preencher spinner de cargos
        ArrayAdapter<CharSequence> adapterCargos = ArrayAdapter.createFromResource(this,R.array.cargos, android.R.layout.simple_spinner_item);
        adapterCargos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCargos.setAdapter(adapterCargos);

        //Método utilizado para preencher spinner com os estados brasileiros
        preencherEstados();

        //Desabilitar spinner de cidades até a seleção de um estado
        spnCidades.setEnabled(false);

        floatCadastrarVaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    try{
                        vaga = new Vaga();
                        //Preencher campos do objeto vaga
                        vaga.setTitulo(editVagaTitulo.getText().toString());
                        //vaga.setCidade(editVagaCidade.getText().toString());
                        vaga.setEmailContato(editVagaEmailContato.getText().toString());
                        vaga.setDescricao(editVagaDescricao.getText().toString());
                        vaga.setIdEmpresa(idUsuario);
                        vaga.salvar();
                        mostraMensagem("Vaga cadastrada com sucesso");
                        finish();

                    }catch (Exception e){
                        mostraMensagem("Erro ao cadastrar vaga");
                    }
                }
            }
        });

        spnEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 ){
                    spnCidades.setEnabled(false);
                }else{
                    //preencher spinner de cidades
                    preencherCidade(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean validarCampos(){
        if (TextUtils.isEmpty(editVagaTitulo.getText())){
            mostraMensagem("Favor preencher o campo de título da vaga!");
            return false;
        }

        if (TextUtils.isEmpty(editVagaEmailContato.getText())){
            mostraMensagem("Favor preencher o campo e-mail de contato da vaga!");
            return false;
        }
        if (spnCargos.getSelectedItemPosition() == 0){
            mostraMensagem("Favor selecionar o cargo da vaga");
            return false;
        }
        if (spnEstados.getSelectedItemPosition() == 0){
            mostraMensagem("Favor selecionar o estado em que a vaga está disponível");
            return false;
        }
        if (spnCidades.getSelectedItemPosition() == 0){
            mostraMensagem("Favor selecionar a cidade em que a vaga está disponível");
            return false;
        }
        if (TextUtils.isEmpty(editVagaDescricao.getText())){
            mostraMensagem("Favor preencher o campo descrição da vaga!");
            return false;
        }

        return true;
    }
    private void mostraMensagem(String mensagem){
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
    }
    private void preencherEstados(){
        try {
            progressDialogEstados.show();
            String url = controller.montarUrlBuscaEstados();

            //CONSUMIR API PARA CONSULTAR CEP
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    montaSpinnerEstado(response);
                    progressDialogEstados.hide();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialogEstados.hide();
                    mostraMensagem("Não foi possível recuperar os estados!");
                }
            });
            queue.add(stringRequest);

        }catch (Exception e){
            mostraMensagem("Não foi possível recuperar os estados!");
        }
    }
    private void montaSpinnerEstado(String estados){
        try{
            JSONObject jsonObject = new JSONObject(estados);
            JSONArray jsonEstados = jsonObject.getJSONArray("geonames");
            JSONObject estado;

            codigoEstado = new ArrayList<>();
            nomeEstado = new ArrayList<>();

            //Adicionar primeira linha
            codigoEstado.add("0");
            nomeEstado.add("Selecione um estado");

            //Estrutura de repetição para preencher dois arrays list, o primeiro contem o código do estado fornecido pela API
            //O segundo contem o nome do estado, utilizando estes dois arrays, será possível buscar as cidades do estado
            for (int i = 0; i < jsonEstados.length(); i++){
                estado = new JSONObject(jsonEstados.getString(i));
                codigoEstado.add(estado.getString("geonameId"));
                nomeEstado.add(estado.getString("toponymName"));
            }
            ArrayAdapter adapterEstados = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, nomeEstado);
            spnEstados.setAdapter(adapterEstados);

        }catch (Exception e){
            e.getMessage();
        }
    }
    private void  preencherCidade(int posicao){
        try {
            progressDialogCidades.show();
            String codEst = codigoEstado.get(posicao);
            String url = controller.montarUrlBuscaCidades(codEst);

            //CONSUMIR API PARA CONSULTAR CEP
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    montaSpinnerCidade(response);
                    progressDialogCidades.hide();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialogCidades.hide();
                    mostraMensagem("Não foi possível recuperar as cidades!");
                }
            });
            queue.add(stringRequest);

        }catch (Exception e){
            mostraMensagem("Não foi possível recuperar as cidades!");
        }
    }
    private void montaSpinnerCidade(String cidades){
        try{
            JSONObject jsonObject = new JSONObject(cidades);
            JSONArray jsonCidades = jsonObject.getJSONArray("geonames");
            JSONObject cidade;

            codigoCidade = new ArrayList<>();
            nomeCidade = new ArrayList<>();

            //Adicionar primeira linha
            codigoCidade.add("0");
            nomeCidade.add("Selecione uma cidade");

            //Estrutura de repetição para preencher dois arrays list, o primeiro contem o código da cidade fornecido pela API
            //O segundo contem o nome da cidade
            for (int i = 0; i < jsonCidades.length(); i++){
                cidade = new JSONObject(jsonCidades.getString(i));
                codigoCidade.add(cidade.getString("geonameId"));
                nomeCidade.add(cidade.getString("toponymName"));
            }
            ArrayAdapter adapterCidades = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, nomeCidade);
            spnCidades.setAdapter(adapterCidades);

            spnCidades.setEnabled(true);
        }catch (Exception e){
            e.getMessage();
        }
    }
}
