package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class CadastrarVagaActivity extends AppCompatActivity {

    private EditText editVagaTitulo;
    private EditText editVagaCidade;
    private EditText editVagaEmailContato;
    private EditText editVagaDescricao;
    private Button btnVagaCadastrar;
    private Vaga vaga;
    private String idUsuario;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_vaga);

        //Inicializar controles
        editVagaTitulo = (EditText) findViewById(R.id.editVagaTitulo);
        editVagaCidade = (EditText) findViewById(R.id.editVagaCidade);
        editVagaEmailContato = (EditText) findViewById(R.id.editVagaEmailContato);
        editVagaDescricao = (EditText) findViewById(R.id.editVagaDescricao);
        btnVagaCadastrar = (Button) findViewById(R.id.btnVagaCadastrar);

        controller = new Controller(CadastrarVagaActivity.this);
        idUsuario = controller.getIdUsuario();

        btnVagaCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validar campos obrigatórios para o cadastro da vaga
                if (validarCampos()){
                    try{
                        vaga = new Vaga();
                        //Preencher campos do objeto vaga
                        vaga.setTitulo(editVagaTitulo.getText().toString());
                        vaga.setCidade(editVagaCidade.getText().toString());
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
    }

    private boolean validarCampos(){
        if (TextUtils.isEmpty(editVagaTitulo.getText())){
            mostraMensagem("Favor preencher o campo de título da vaga!");
            return false;
        }

        if (TextUtils.isEmpty(editVagaCidade.getText())){
            mostraMensagem("Favor preencher o campo cidade da vaga!");
            return false;
        }

        if (TextUtils.isEmpty(editVagaEmailContato.getText())){
            mostraMensagem("Favor preencher o campo e-mail de contato da vaga!");
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

}
