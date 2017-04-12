package projetoaplicado.vgoncalves.com.projetoaplicado.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import projetoaplicado.vgoncalves.com.projetoaplicado.CadastrarVagaActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.Helper;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.VagasEmpresaActivity;

public class VagasEmpresaFragment extends Fragment {

    private FloatingActionButton btnCadastrarVaga;
    private ListView listVagas;
    private ArrayAdapter adapter;
    private ArrayList<String> tituloVaga;
    private DatabaseReference databaseReference;
    private String idUsuarioLogado;

    public VagasEmpresaFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_vagas_empresa, container, false);
        //Recuperar instancia do firebase
        databaseReference = ConfiguracaoFirebase.getReferenceFirebase();

        //Armazenar id do usuário logado
        Helper helper = new Helper();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(helper.NOME_ARQUIVO,0);
        helper.sharedPreferences = sharedPreferences;
        idUsuarioLogado = helper.getIdUsuario();

        //Instanciar objetos
        tituloVaga = new ArrayList<>();

        //inicializar controles de tela
        btnCadastrarVaga = (FloatingActionButton) view.findViewById(R.id.floatCadastrarVaga);
        listVagas = (ListView) view.findViewById(R.id.listaFragmentVagas);
        adapter = new ArrayAdapter(getActivity(), R.layout.lista_vagas, tituloVaga);

        //Instanciar lista
        listVagas.setAdapter(adapter);

        databaseReference
                .child(ConfiguracaoFirebase.NODE_VAGA)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tituloVaga.clear();
                        for(DataSnapshot dados: dataSnapshot.getChildren()){
                            Vaga vaga = dados.getValue(Vaga.class);
                            if (idUsuarioLogado.toString().equals(vaga.getIdEmpresa().toString())){
                                tituloVaga.add(vaga.getTitulo());
                            }
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
                Intent intent = new Intent(getActivity(), CadastrarVagaActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
