package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Filtro;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Habilidades;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class FiltroActivity extends AppCompatActivity {

    private String idUsuario;
    private String cidadeRecuperada;
    private boolean selecionouEstado = false;

    private Controller controller;
    private Spinner spnCargos;
    private Spinner spnEstados;
    private AutoCompleteTextView txtCidades;
    private MultiAutoCompleteTextView textSelecHabFiltro;
    private Button btnAplicarFiltro;

    private ArrayList<String> codigoEstado;
    private ArrayList<String> nomeEstado;

    private ArrayList<String> codigoCidade;
    private ArrayList<String> nomeCidade;
    private ArrayList<String> habilidadesArray;
    private ArrayList<String> cargos;

    private ProgressDialog progressDialogCidades;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        controller = new Controller(FiltroActivity.this);
        idUsuario = controller.getIdUsuario();
        databaseReference = controller.getDatabaseReference();

        //Inicializar controles
        spnCargos = (Spinner) findViewById(R.id.spnFiltroCargo);
        spnEstados = (Spinner) findViewById(R.id.spnFiltroEstado);
        txtCidades = (AutoCompleteTextView) findViewById(R.id.textFiltroCidade);
        textSelecHabFiltro = (MultiAutoCompleteTextView) findViewById(R.id.textSelecHabFiltro);
        btnAplicarFiltro = (Button) findViewById(R.id.btnAplicarFiltro);

        progressDialogCidades = new ProgressDialog(FiltroActivity.this);
        progressDialogCidades.setTitle("Buscando cidades");
        progressDialogCidades.setMessage("Carregando cidades...");
        progressDialogCidades.setCancelable(false);

        progressDialog = new ProgressDialog(FiltroActivity.this);
        progressDialog.setMessage("Salvando filtros...");
        progressDialog.setCancelable(false);

        //Instancia do adapter para preencher spinner de cargos
        ArrayAdapter<CharSequence> adapterCargos = ArrayAdapter.createFromResource(this,R.array.cargos, android.R.layout.simple_spinner_item);
        adapterCargos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCargos.setAdapter(adapterCargos);

        //preenche arraylist de cargos para recuperação de filtro
        String[] cargo = getResources().getStringArray(R.array.cargos);
        cargos = new ArrayList<String>(Arrays.asList(cargo));

        //Método utilizado para preencher spinner com os estados brasileiros
        preencherEstados();

        //Desabilitar spinner de cidades até a seleção de um estado
        //txtCidades.setEnabled(false);

        spnEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 ){
                    txtCidades.setText("");
                    txtCidades.setEnabled(false);
                }else{
                    //preencher spinner de cidades
                    selecionouEstado = true;
                    preencherCidade(position);
                    if (!TextUtils.isEmpty(txtCidades.getText()))
                        txtCidades.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        databaseReference.child(controller.NODE_HABILIDADES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    if (habilidadesArray == null)
                        habilidadesArray = new ArrayList<String>();

                    habilidadesArray.clear();
                    for(DataSnapshot hablidade: dataSnapshot.getChildren()){
                        Habilidades hab = hablidade.getValue(Habilidades.class);
                        habilidadesArray.add(hab.getDescricao());
                    }

                    String[] items = habilidadesArray.toArray(new String[habilidadesArray.size()]);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(FiltroActivity.this,android.R.layout.simple_dropdown_item_1line, items);
                    textSelecHabFiltro.setAdapter(adapter);
                    textSelecHabFiltro.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

                }catch (Exception e){
                    e.getMessage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child(controller.NODE_FILTROS).child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Filtro filtro = dataSnapshot.getValue(Filtro.class);

                    if (filtro.getCargo() != null){
                        if(!TextUtils.isEmpty(filtro.getCargo().toString())){
                            spnCargos.setSelection(cargos.indexOf(filtro.getCargo().toString()));
                        }
                    }

                    if (filtro.getEstado() != null){
                        if(!TextUtils.isEmpty(filtro.getEstado().toString())){
                            //preencher estado
                            spnEstados.setSelection(codigoEstado.indexOf(filtro.getEstado().toString()));
                        }
                    }

                    if (filtro.getCidade() != null){
                        if(!TextUtils.isEmpty(filtro.getCidade().toString())){
                            txtCidades.setEnabled(true);
                            txtCidades.setText(filtro.getCidade().toString());
                            cidadeRecuperada = filtro.getCidade().toString();
                        }
                    }

                    if (filtro.getHabilidades() != null){
                        if(!TextUtils.isEmpty(filtro.getHabilidades().toString())){
                            textSelecHabFiltro.setText(filtro.getHabilidades().toString());
                        }
                    }

                }catch (Exception e){
                    e.getMessage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnAplicarFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    progressDialog.show();
                    Filtro filtro = new Filtro(FiltroActivity.this);
                    filtro.setCargo(spnCargos.getSelectedItem().toString());
                    filtro.setEstado(codigoEstado.get(spnEstados.getSelectedItemPosition()).toString());
                    filtro.setCidade(txtCidades.getText().toString());
                    filtro.setHabilidades(textSelecHabFiltro.getText().toString());
                    filtro.setIdUsuario(idUsuario);
                    filtro.salvar();
                    progressDialog.hide();
                    mostraMensagem("Filtro salvo com sucesso");

                    finish();
                }catch (Exception e){
                    e.getMessage();
                    progressDialog.hide();
                }
            }
        });

        txtCidades.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtCidades.setText("");
                }else{
                    if(!TextUtils.isEmpty(cidadeRecuperada))
                        txtCidades.setText(cidadeRecuperada);
                }
            }
        });
    }
    private void preencherEstados(){
        try {
            codigoEstado = new ArrayList<>();
            nomeEstado = new ArrayList<>();

            //Adicionar primeira linha
            codigoEstado.add("0");
            nomeEstado.add("Selecione um estado");
            //Acre	AC
            codigoEstado.add("AC");
            nomeEstado.add("Acre");
            //Alagoas	AL
            codigoEstado.add("AL");
            nomeEstado.add("Alagoas");
            //Amapá	AP
            codigoEstado.add("AP");
            nomeEstado.add("Amapá");
            //Amazonas	AM
            codigoEstado.add("AM");
            nomeEstado.add("Amazonas");
            //Bahia	BA
            codigoEstado.add("BA");
            nomeEstado.add("Bahia");
            //Ceará	CE
            codigoEstado.add("CE");
            nomeEstado.add("Ceará");
            //Distrito Federal	DF
            codigoEstado.add("DF");
            nomeEstado.add("Distrito Federal");
            //Espírito Santo	ES
            codigoEstado.add("ES");
            nomeEstado.add("Espírito Santo");
            //Goiás	GO
            codigoEstado.add("GO");
            nomeEstado.add("Goiás");
            //Maranhão	MA
            codigoEstado.add("MA");
            nomeEstado.add("Maranhão");
            //Mato Grosso	MT
            codigoEstado.add("MT");
            nomeEstado.add("Mato Grosso");
            //Mato Grosso do Sul	MS
            codigoEstado.add("MS");
            nomeEstado.add("Mato Grosso do Sul");
            //Minas Gerais	MG
            codigoEstado.add("MG");
            nomeEstado.add("Minas Gerais");
            //Pará	PA
            codigoEstado.add("PA");
            nomeEstado.add("Pará");
            //Paraíba	PB
            codigoEstado.add("PB");
            nomeEstado.add("Paraíba");
            //Paraná	PR
            codigoEstado.add("PR");
            nomeEstado.add("Paraná");
            //Pernambuco	PE
            codigoEstado.add("PE");
            nomeEstado.add("Pernambuco");
            //Piauí	PI
            codigoEstado.add("PI");
            nomeEstado.add("Piauí");
            //Rio de Janeiro	RJ
            codigoEstado.add("RJ");
            nomeEstado.add("Rio de Janeiro");
            //Rio Grande do Norte	RN
            codigoEstado.add("RN");
            nomeEstado.add("Rio Grande do Norte");
            //Rio Grande do Sul	RS
            codigoEstado.add("RS");
            nomeEstado.add("Rio Grande do Sul");
            //Rondônia	RO
            codigoEstado.add("RO");
            nomeEstado.add("Rondônia");
            //Roraima	RR
            codigoEstado.add("RR");
            nomeEstado.add("Raraima");
            //Santa Catarina	SC
            codigoEstado.add("SC");
            nomeEstado.add("Santa Catarina");
            //São Paulo	SP
            codigoEstado.add("SP");
            nomeEstado.add("São Paulo");
            //Sergipe	SE
            codigoEstado.add("SE");
            nomeEstado.add("Sergipe");
            //Tocantins	TO
            codigoEstado.add("TO");
            nomeEstado.add("Tocantins");

            ArrayAdapter adapterEstados = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, nomeEstado);
            spnEstados.setAdapter(adapterEstados);

        }catch (Exception e){
            mostraMensagem("Não foi possível recuperar os estados!");
        }
    }
    private void mostraMensagem(String mensagem){
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
    }
    private void preencherCidade(int posicao){
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
            String[] arrayCidades = cidades.split(",");
            codigoCidade = new ArrayList<>();
            nomeCidade = new ArrayList<>();
            //Adicionar primeira linha
            codigoCidade.add("0");
            nomeCidade.add("Selecione uma cidade");

            //Estrutura de repetição para preencher dois arrays list, o primeiro contem o código da cidade fornecido pela API
            //O segundo contem o nome da cidade
            for (int i = 0; i < arrayCidades.length; i++){
                String[]cidade = arrayCidades[i].split(":");
                codigoCidade.add(cidade[0].replaceAll("\"", ""));
                nomeCidade.add(cidade[1].replaceAll("\"", ""));
            }
            ArrayAdapter adapterCidades = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, nomeCidade);
            txtCidades.setAdapter(adapterCidades);

            txtCidades.setEnabled(true);
            if (!TextUtils.isEmpty(cidadeRecuperada))
                txtCidades.setText(cidadeRecuperada);
        }catch (Exception e){
            e.getMessage();
        }
    }

}
