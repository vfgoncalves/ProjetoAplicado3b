package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Habilidades;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.transform.CircleTransform;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class EditarUsuarioPerfilActivity extends AppCompatActivity {

    private Controller controller;
    private DatabaseReference databaseReference;
    private Usuario usuario;

    //Controles da tela
    private EditText editNome;
    private EditText editEmail;
    private EditText editrelefoneCel;
    private EditText editTelefoneResidencial;
    private EditText editCEP;
    private EditText editPais;
    private EditText editEstado;
    private EditText editCidade;
    private EditText editBairro;
    private EditText editRua;
    private EditText editNumero;
    private EditText editComplemento;
    private EditText editHabilidades;
    private ImageView imgPerfilUser;
    private Button btnSalvar;
    private ArrayList<String> habilidadesArray;
    private ArrayList selectedItemsModal;


    //ProgreesDialog
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogSalvar;
    private ProgressDialog progressDialogCep;

    private AlertDialog dialog;

    //IdUsuario
    private String idUsuario;

    //APi
    private final String URL_API_CEP = "https://viacep.com.br/ws/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario_perfil);

        controller = new Controller(this);
        databaseReference = controller.getDatabaseReference();
        idUsuario = controller.getIdUsuario();
        selectedItemsModal = new ArrayList();

        editNome = (EditText) findViewById(R.id.editUserEditNome);
        editEmail = (EditText) findViewById(R.id.editUserEditEmail);
        editrelefoneCel = (EditText) findViewById(R.id.editUserEditTelCel);
        editTelefoneResidencial = (EditText) findViewById(R.id.editUserEditTelRes);
        editCEP = (EditText) findViewById(R.id.editUserEditCEP);
        editCEP.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editPais = (EditText) findViewById(R.id.editUserEditPais);
        editEstado = (EditText) findViewById(R.id.editUserEditEst);
        editCidade = (EditText) findViewById(R.id.editUserEditCid);
        editBairro = (EditText) findViewById(R.id.editUserEditBairro);
        editRua = (EditText) findViewById(R.id.editUserEditRua);
        editNumero = (EditText) findViewById(R.id.editUserEditNumero);
        editComplemento = (EditText) findViewById(R.id.editUserEditCompl);
        btnSalvar = (Button) findViewById(R.id.btnSalvarPerfilUsuario);
        editHabilidades = (EditText) findViewById(R.id.editUserEditHabilidades);
        imgPerfilUser = (ImageView) findViewById(R.id.imgPerfilUser);

        //Mascaras
        SimpleMaskFormatter maskTel = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        SimpleMaskFormatter maskTelRes = new SimpleMaskFormatter("(NN)NNNN-NNNN");
        SimpleMaskFormatter maskCep = new SimpleMaskFormatter("NNNNN-NNN");

        //Watcher para edição do texto do controle
        MaskTextWatcher watcherTel = new MaskTextWatcher(editrelefoneCel, maskTel);
        MaskTextWatcher watcherTelRes = new MaskTextWatcher(editTelefoneResidencial, maskTelRes);
        MaskTextWatcher watcherCEP = new MaskTextWatcher(editCEP, maskCep);

        //Aplica mascaras aos devidos controles
        editrelefoneCel.addTextChangedListener(watcherTel);
        editTelefoneResidencial.addTextChangedListener(watcherTelRes);
        editCEP.addTextChangedListener(watcherCEP);


        //Configurar Progress Dialog
        progressDialog = new ProgressDialog(EditarUsuarioPerfilActivity.this);
        progressDialog.setTitle("Carregando");
        progressDialog.setMessage("Por favor aguarde, estamos carregando as informações");
        progressDialog.setCancelable(false);

        progressDialogSalvar = new ProgressDialog(EditarUsuarioPerfilActivity.this);
        progressDialogSalvar.setTitle("Salvando");
        progressDialogSalvar.setMessage("Por favor aguarde, estamos salvando as informações");
        progressDialogSalvar.setCancelable(false);

        progressDialogCep = new ProgressDialog(EditarUsuarioPerfilActivity.this);
        progressDialogCep.setTitle("Aguarde");
        progressDialogCep.setMessage("Por favor aguarde, estamos buscando os dados do seu endereço");
        progressDialogCep.setCancelable(false);

        databaseReference.child(controller.NODE_USUARIO).child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.show();
                usuario = dataSnapshot.getValue(Usuario.class);
                if (usuario != null){
                    usuario.setID(idUsuario);
                    preencherCampos();
                }
                progressDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editCEP.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    try{
                        String url = URL_API_CEP + editCEP.getText().toString().replace("-","") + "/json/";
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

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialogSalvar.show();

                usuario.setNome(editNome.getText().toString());
                usuario.setEmail(editEmail.getText().toString());
                usuario.setTelefoneCel(editrelefoneCel.getText().toString());
                usuario.setTelefoneResidencial(editTelefoneResidencial.getText().toString());
                usuario.setCEP(editCEP.getText().toString());
                usuario.setPais(editPais.getText().toString());
                usuario.setEstado(editEstado.getText().toString());
                usuario.setCidade(editCidade.getText().toString());
                usuario.setBairro(editBairro.getText().toString());
                usuario.setRua(editRua.getText().toString());
                usuario.setNumero(editNumero.getText().toString());
                usuario.setComplemento(editComplemento.getText().toString());
                usuario.setHabilidades(editHabilidades.getText().toString());
                //Salvar dados do usuario
                usuario.setController(controller);
                usuario.salvar();
                progressDialogSalvar.hide();

                Toast.makeText(getApplicationContext(), "Dados do perfil salvos com sucesso", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        configurarModal();

        editHabilidades.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    dialog.show();
                }else {
                    dialog.hide();
                }
            }
        });
        editHabilidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }

    private void preencherCampos(){
        //Importa imagem de perfil
        if (!TextUtils.isEmpty(usuario.getPhotoUrl()))
            Picasso.with(EditarUsuarioPerfilActivity.this).load(usuario.getPhotoUrl()).transform(new CircleTransform()).into(imgPerfilUser);

        editNome.setText(usuario.getNome());
        editEmail.setText(usuario.getEmail());
        editrelefoneCel.setText(usuario.getTelefoneCel());
        editTelefoneResidencial.setText(usuario.getTelefoneResidencial());
        editHabilidades.setText(editHabilidades.getText().toString());
        editCEP.setText(usuario.getCEP());
        editPais.setText(usuario.getPais());
        editEstado.setText(usuario.getEstado());
        editCidade.setText(usuario.getCidade());
        editBairro.setText(usuario.getBairro());
        editRua.setText(usuario.getRua());
        editNumero.setText(usuario.getNumero());
        editComplemento.setText(usuario.getComplemento());
    }

    private void preencherCamposEndereco(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);

            editPais.setText("Brasil");
            editEstado.setText(jsonObject.getString("uf"));
            editCidade.setText(jsonObject.getString("localidade"));
            editBairro.setText(jsonObject.getString("bairro"));
            editRua.setText(jsonObject.getString("logradouro"));
            editComplemento.setText(jsonObject.getString("complemento"));

            progressDialogCep.hide();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Erro ao recuperar cep", Toast.LENGTH_LONG).show();
            progressDialogCep.hide();
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
                    dialog = new AlertDialog.Builder(EditarUsuarioPerfilActivity.this)
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

        editHabilidades.setText("");

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
            editHabilidades.setText(hab);
    }
}
