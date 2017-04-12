package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import org.json.JSONObject;

import java.util.List;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.Helper;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText editEmpCompl;
    private EditText editEmpNumero;
    private EditText editEmpRua;
    private EditText editEmpBairro;
    private EditText editEmpCid;
    private EditText editEmpEst;
    private EditText editEmpPais;
    private EditText editEmpCEP;
    private EditText editEmpTel;
    private EditText editEmpEmail;
    private EditText editEmpNome;
    private DatabaseReference databaseReference;
    private String idEmpresa;
    private Empresa empresa;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogSalvar;
    private ProgressDialog progressDialogCep;
    private Button btnSalvar;
    private final String URL_API_CEP = "https://viacep.com.br/ws/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Receber instância do Firebase
        databaseReference = ConfiguracaoFirebase.getReferenceFirebase();

        //Configurar Progress Dialog
        progressDialog = new ProgressDialog(EditarPerfilActivity.this);
        progressDialog.setTitle("Carregando");
        progressDialog.setMessage("Por favor aguarde, estamos carregando as informações");
        progressDialog.setCancelable(false);

        progressDialogSalvar = new ProgressDialog(EditarPerfilActivity.this);
        progressDialogSalvar.setTitle("Salvando");
        progressDialogSalvar.setMessage("Por favor aguarde, estamos salvando as informações");
        progressDialogSalvar.setCancelable(false);

        progressDialogCep = new ProgressDialog(EditarPerfilActivity.this);
        progressDialogCep.setTitle("Aguarde");
        progressDialogCep.setMessage("Por favor aguarde, estamos buscando os dados do seu endereço");
        progressDialogCep.setCancelable(false);

        //Armazenar id do usuário logado
        Helper helper = new Helper();
        SharedPreferences sharedPreferences = getSharedPreferences(helper.NOME_ARQUIVO,0);
        helper.sharedPreferences = sharedPreferences;
        idEmpresa = helper.getIdUsuario();

        //Inicializar controle
        editEmpCompl = (EditText) findViewById(R.id.editEmpCompl);
        editEmpNumero= (EditText) findViewById(R.id.editEmpNumero);
        editEmpRua= (EditText) findViewById(R.id.editEmpRua);
        editEmpBairro= (EditText) findViewById(R.id.editEmpBairro);
        editEmpCid= (EditText) findViewById(R.id.editEmpCid);
        editEmpEst= (EditText) findViewById(R.id.editEmpEst);
        editEmpPais= (EditText) findViewById(R.id.editEmpPais);
        editEmpCEP= (EditText) findViewById(R.id.editEmpCEP);
        editEmpCEP.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editEmpTel= (EditText) findViewById(R.id.editEmpTel);
        editEmpEmail= (EditText) findViewById(R.id.editEmpEmail);
        editEmpNome= (EditText) findViewById(R.id.editEmpNome);
        btnSalvar = (Button) findViewById(R.id.btnSalvarPerfil);

        databaseReference
                .child(ConfiguracaoFirebase.NODE_EMPRESA)
                .child(idEmpresa)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.show();
                        empresa = dataSnapshot.getValue(Empresa.class);
                        if (empresa != null){
                            empresa.setID(idEmpresa);
                            preencherCampos();
                        }
                        progressDialog.hide();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //preencher campos para salvar
                empresa.setComplemento(editEmpCompl.getText().toString());
                empresa.setNumero(editEmpNumero.getText().toString());
                empresa.setRua(editEmpRua.getText().toString());
                empresa.setBairro(editEmpBairro.getText().toString());
                empresa.setCidade(editEmpCid.getText().toString());
                empresa.setEstado(editEmpEst.getText().toString());
                empresa.setPais(editEmpPais.getText().toString());
                empresa.setCEP(editEmpCEP.getText().toString());
                empresa.setTelefone(editEmpTel.getText().toString());
                empresa.setEmail(editEmpEmail.getText().toString());
                empresa.setNome(editEmpNome.getText().toString());
                progressDialogSalvar.show();
                empresa.salvar();
                progressDialogSalvar.hide();

                Toast.makeText(getApplicationContext(), "Dados do perfil salvos com sucesso", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        editEmpCEP.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    try{
                        String url = URL_API_CEP + editEmpCEP.getText().toString() + "/json/";
                        progressDialogCep.show();
                        //CONSUMIR API PARA CONSULTAR CEP
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialogCep.show();
                                preencherCamposEndereco(response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        queue.add(stringRequest);
                    }catch (Exception e){
                        progressDialogCep.hide();
                        Toast.makeText(getApplicationContext(), "Ocorreu erro ao buscar dados de endereço", Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
        });
    }

    private void preencherCampos(){
        editEmpCompl.setText(empresa.getComplemento());
        editEmpNumero.setText(empresa.getNumero());
        editEmpRua.setText(empresa.getRua());
        editEmpBairro.setText(empresa.getBairro());
        editEmpCid.setText(empresa.getCidade());
        editEmpEst.setText(empresa.getEstado());
        editEmpPais.setText(empresa.getPais());
        editEmpCEP.setText(empresa.getCEP());
        editEmpTel.setText(empresa.getTelefone());
        editEmpEmail.setText(empresa.getEmail());
        editEmpNome.setText(empresa.getNome());
    }

    private void preencherCamposEndereco(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);

            editEmpRua.setText(jsonObject.getString("logradouro"));
            editEmpBairro.setText(jsonObject.getString("bairro"));
            editEmpCid.setText(jsonObject.getString("localidade"));
            editEmpEst.setText(jsonObject.getString("uf"));
            editEmpCompl.setText(jsonObject.getString("complemento"));
            editEmpPais.setText("Brasil");

            progressDialogCep.hide();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Erro ao recuperar cep", Toast.LENGTH_LONG).show();
            progressDialogCep.hide();
        }
    }

}
