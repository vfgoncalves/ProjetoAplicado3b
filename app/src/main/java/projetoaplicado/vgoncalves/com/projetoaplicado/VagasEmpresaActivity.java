package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;

public class VagasEmpresaActivity extends AppCompatActivity {

    private FloatingActionButton btnCadastrarVaga;
    private ListView listVagas;
    private ArrayAdapter adapter;
    private ArrayList<String> tituloVaga;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vagas_empresa);

        btnCadastrarVaga = (FloatingActionButton) findViewById(R.id.btnCadastrarVaga);
        databaseReference = ConfiguracaoFirebase.getReferenceFirebase().child(ConfiguracaoFirebase.NODE_VAGA);

        //Iniciar listview para mostrar vagas
        listVagas = (ListView) findViewById(R.id.listaVagas);
        tituloVaga = new ArrayList<>();
        adapter = new ArrayAdapter(
                getApplicationContext(),
                R.layout.lista_vagas,
                tituloVaga
        );
        listVagas.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //limpar lista
                tituloVaga.clear();

                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Vaga vaga = dados.getValue(Vaga.class);
                    tituloVaga.add(vaga.getTitulo());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
