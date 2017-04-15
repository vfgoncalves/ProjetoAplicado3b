package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import projetoaplicado.vgoncalves.com.projetoaplicado.R;

public class ContextoActivity extends AppCompatActivity {
    private ImageButton imgCandidato;
    private ImageButton imgEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contexto);

        imgCandidato = (ImageButton) findViewById(R.id.imgCandidato);
        imgEmpresa = (ImageButton) findViewById(R.id.imgEmpresa);

        imgCandidato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContextoActivity.this, CadastrarActivity.class);
                startActivity(intent);
            }
        });

        imgEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContextoActivity.this, CadastroEmpresaActivity.class);
                startActivity(intent);
            }
        });

    }
}
