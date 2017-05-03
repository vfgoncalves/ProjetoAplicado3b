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

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.CadastrarVagaActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.FiltroActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class VagasUsuarioFragment extends Fragment {

    private FloatingActionButton btnFiltro;
    private ListView listVagas;
    private ArrayAdapter adapter;
    private ArrayList<String> tituloVaga;
    private DatabaseReference databaseReference;
    private String idUsuarioLogado;
    private Controller controller;

    public VagasUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vagas_usuario, container, false);

        //Recuperar instancia do firebase
        controller = new Controller(getContext());
        databaseReference = controller.getDatabaseReference();

        //Armazenar id do usuário logado
        idUsuarioLogado = controller.getIdUsuario();

        //Instanciar objetos
        tituloVaga = new ArrayList<>();

        //inicializar controles de tela
        btnFiltro = (FloatingActionButton) view.findViewById(R.id.floatFiltrosVaga);
        listVagas = (ListView) view.findViewById(R.id.listaFragmentVagasUser);
        adapter = new ArrayAdapter(getActivity(), R.layout.lista_vagas, tituloVaga);

        //Instanciar lista
        listVagas.setAdapter(adapter);


        databaseReference.child(controller.NODE_VAGA).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    tituloVaga.clear();
                    //loop por estados
                    for(DataSnapshot estados: dataSnapshot.getChildren()){
                        //loop por cidades
                        for(DataSnapshot cidades: estados.getChildren()){
                            //loop por cargos
                            for(DataSnapshot cargos: cidades.getChildren()){
                                //loop por vagas
                                for(DataSnapshot vagas: cargos.getChildren()){
                                    Vaga vaga = vagas.getValue(Vaga.class);
                                    tituloVaga.add(vaga.getTitulo());
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

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Iniciar tela para aplicar filtros nas vagas
                Intent intent = new Intent(getActivity(), FiltroActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
