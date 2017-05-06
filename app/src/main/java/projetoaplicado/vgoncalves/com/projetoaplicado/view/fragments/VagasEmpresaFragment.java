package projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.VagasAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.CadastrarVagaActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;

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

        // Inflate the layout for this fragment
        return view;
    }
}
