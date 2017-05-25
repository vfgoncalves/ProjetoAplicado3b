package projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.VagasAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.CadastrarVagaActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.DetalheVagaActivity;

public class VagasEmpresaFragment extends Fragment {

    private FloatingActionButton btnCadastrarVaga;
    private ListView listVagas;
    private ArrayAdapter adapter;
    private ArrayList<Vaga> listaVagas;
    private DatabaseReference databaseReference;
    private String idUsuarioLogado;
    private Controller controller;

    public VagasEmpresaFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_vagas_empresa, container, false);
        //Recuperar instancia do firebase
        controller = new Controller(getContext());
        databaseReference = controller.getDatabaseReference();

        //Armazenar id do usuário logado
        idUsuarioLogado = controller.getIdUsuario();

        //Instanciar objetos
        listaVagas = new ArrayList<>();

        //inicializar controles de tela
        btnCadastrarVaga = (FloatingActionButton) view.findViewById(R.id.floatCadastrarVaga);
        listVagas = (ListView) view.findViewById(R.id.listaFragmentVagas);
        listVagas.setDivider(null);
        listVagas.setDividerHeight(0);
        //adapter = new ArrayAdapter(getActivity(), R.layout.lista_vagas, tituloVaga);
        adapter = new VagasAdapter(getActivity(), listaVagas);

        //Instanciar lista
        listVagas.setAdapter(adapter);


        databaseReference.child(controller.NODE_VAGA).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{
                            listaVagas.clear();
                            //loop por estados
                            for(DataSnapshot estados: dataSnapshot.getChildren()){
                                //loop por cidades
                                for(DataSnapshot cidades: estados.getChildren()){
                                    //loop por cargos
                                    for(DataSnapshot cargos: cidades.getChildren()){
                                        //loop por vagas
                                        for(DataSnapshot vagas: cargos.getChildren()){
                                            Vaga vaga = vagas.getValue(Vaga.class);
                                            if (idUsuarioLogado.toString().equals(vaga.getIdEmpresa().toString())){
                                                listaVagas.add(vaga);
                                            }
                                        }
                                    }
                                }

                            }
                            adapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.getMessage();
                        }
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

        listVagas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    Vaga vaga = listaVagas.get(position);
                    Intent intent = new Intent(getActivity(), DetalheVagaActivity.class);
                    //Converter dados da classe em json
                    JSONObject jsonObject = vaga.convertToJson();
                    //enviar dados para activity
                    intent.putExtra("vagaatual", jsonObject.toString());

                    startActivity(intent);
                }catch (Exception e){
                    e.getMessage();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
