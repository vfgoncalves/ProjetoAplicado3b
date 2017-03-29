package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CadastrarActivity extends AppCompatActivity {
    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        nome = (EditText) findViewById(R.id.editCadNome);
        email = (EditText) findViewById(R.id.editCadEmail);
        senha = (EditText) findViewById(R.id.editCadSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    //Criar login no Firebase

                    //Adicionar usuário no banco

                    //Navegar para tela Main
                }
            }
        });

    }

    private boolean validarCampos(){
        if (TextUtils.isEmpty(nome.getText())){
            mostraMensagem("Favor preencher o campo nome!");
            return false;
        }

        if (TextUtils.isEmpty(email.getText())){
            mostraMensagem("Favor preencher o campo nome!");
            return false;
        }

        if (TextUtils.isEmpty(senha.getText())){
            mostraMensagem("Favor preencher o campo nome!");
            return false;
        }

        return true;
    }

    private void mostraMensagem(String mensagem){
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
    }

}
