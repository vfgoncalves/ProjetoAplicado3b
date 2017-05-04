package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Habilidades;
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
    private AutoCompleteTextView txtCidades;
    private EditText editSelecHab;

    private ArrayList<String> codigoEstado;
    private ArrayList<String> nomeEstado;

    private ArrayList<String> codigoCidade;
    private ArrayList<String> nomeCidade;
    private ArrayList<String> habilidadesArray;
    private ArrayList selectedItemsModal;

    private ProgressDialog progressDialogCidades;

    private DatabaseReference databaseReference;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_vaga);

        controller = new Controller(CadastrarVagaActivity.this);
        idUsuario = controller.getIdUsuario();
        databaseReference = controller.getDatabaseReference();
        selectedItemsModal = new ArrayList();
        //Inicializar controles
        editVagaTitulo = (EditText) findViewById(R.id.editVagaTitulo);
        editVagaEmailContato = (EditText) findViewById(R.id.editVagaEmailContato);
        editVagaDescricao = (EditText) findViewById(R.id.editVagaDescricao);
        floatCadastrarVaga = (FloatingActionButton) findViewById(R.id.floatCadastrarVaga);
        spnCargos = (Spinner) findViewById(R.id.spnCargo);
        spnEstados = (Spinner) findViewById(R.id.spnEstado);
        txtCidades = (AutoCompleteTextView) findViewById(R.id.textCidade);
        editSelecHab = (EditText) findViewById(R.id.editSelecHab);

        //Configurar ProgressDialogs
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
        txtCidades.setEnabled(false);

        //buscar dados do usuário logado
        databaseReference.child(controller.NODE_EMPRESA).child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Empresa empresa = dataSnapshot.getValue(Empresa.class);
                if(empresa != null)
                    editVagaEmailContato.setText(empresa.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                        vaga.setCidade(txtCidades.getText().toString());
                        vaga.setEstado(codigoEstado.get(spnEstados.getSelectedItemPosition()).toString());
                        vaga.setCargo(spnCargos.getSelectedItem().toString());
                        vaga.setHabilidades(editSelecHab.getText().toString());
                        vaga.setIdVaga(controller.getUUID());
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
                    txtCidades.setText("");
                    txtCidades.setEnabled(false);
                }else{
                    //preencher spinner de cidades
                    preencherCidade(position);
                    txtCidades.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        configurarModal();

        editSelecHab.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    dialog.show();
                }else {
                    dialog.hide();
                }
            }
        });
        editSelecHab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
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
        if (TextUtils.isEmpty(txtCidades.getText())){
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
            adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEstados.setAdapter(adapterEstados);


        }catch (Exception e){
            mostraMensagem("Não foi possível recuperar os estados!");
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
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            txtCidades.setAdapter(adapterCidades);

            txtCidades.setEnabled(true);
        }catch (Exception e){
            e.getMessage();
        }
    }
    private void configurarModal(){
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

                    CharSequence[] items = habilidadesArray.toArray(new CharSequence[habilidadesArray.size()]);

                    //Configuração do Modal
                    dialog = new AlertDialog.Builder(CadastrarVagaActivity.this)
                            .setTitle("Selecionar Habilidades")
                            .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                    if (isChecked) {
                                        selectedItemsModal.add(habilidadesArray.get(indexSelected));
                                    } else if (selectedItemsModal.contains(indexSelected)) {
                                        selectedItemsModal.remove(Integer.valueOf(indexSelected));
                                    }
                                }
                            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    preencherHabilidades();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Your code when user clicked on Cancel
                                }
                            }).create();

                }catch (Exception e){
                    e.getMessage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void preencherHabilidades(){
        String hab = null;

        editSelecHab.setText("");

        //Estrutura de repetição nas habilidades selecionadas
        for (int i = 0; i < selectedItemsModal.size(); i++) {
            if (hab == null){
                hab = selectedItemsModal.get(i).toString();
            }else{
                hab = hab + "," + selectedItemsModal.get(i).toString();
            }
        }

        //Preencher combo de habilidades
        if (hab != null)
            editSelecHab.setText(hab);
    }
}
