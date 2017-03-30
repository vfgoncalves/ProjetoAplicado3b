package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogar;
    private TextView lnkCadastrar;
    private Button btnLinkedin;
    private EditText editEmail;
    private EditText editSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Receber controles da Activity
        btnLogar = (Button) findViewById(R.id.btnLogin);
        lnkCadastrar = (TextView) findViewById(R.id.lnkCadastrar);

        //Método de click no link Cadastrar, levando à próxima activity
        lnkCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ContextoActivity.class);
                startActivity(intent);
            }
        });

    }
}
