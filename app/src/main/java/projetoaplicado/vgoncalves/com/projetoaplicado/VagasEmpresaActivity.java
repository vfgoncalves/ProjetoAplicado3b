package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class VagasEmpresaActivity extends AppCompatActivity {

    private FloatingActionButton btnCadastrarVaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vagas_empresa);

        btnCadastrarVaga = (FloatingActionButton) findViewById(R.id.btnCadastrarVaga);

        btnCadastrarVaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navegar até tela de edição de perfil
                Intent intent = new Intent(VagasEmpresaActivity.this, CadastrarVagaActivity.class);
                startActivity(intent);
            }
        });

    }
}
