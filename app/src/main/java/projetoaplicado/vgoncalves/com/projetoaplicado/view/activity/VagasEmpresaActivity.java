package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import projetoaplicado.vgoncalves.com.projetoaplicado.R;

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






    }
}
