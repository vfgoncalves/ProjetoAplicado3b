package projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments;


import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Filtro;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.VagasAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.CadastrarVagaActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.DetalheVagaActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.Detalhes_vaga_usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.FiltroActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.LoginActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class VagasUsuarioFragment extends Fragment {

    private FloatingActionButton btnFiltro;
    private ListView listVagas;
    private ArrayAdapter adapter;
    private ArrayList<Vaga> listaVagas;
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
        listaVagas = new ArrayList<>();

        //inicializar controles de tela
        btnFiltro = (FloatingActionButton) view.findViewById(R.id.floatFiltrosVaga);
        listVagas = (ListView) view.findViewById(R.id.listaFragmentVagasUser);
        //adapter = new ArrayAdapter(getActivity(), R.layout.lista_vagas, listaVagas);
        //Configuração do Adapter personalizado par alitar vagas
        adapter = new VagasAdapter(getActivity(), listaVagas);

        //Instanciar lista
        listVagas.setAdapter(adapter);


        databaseReference.child(controller.NODE_VAGA).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    verificarFiltroAntesPreencher(dataSnapshot);
                }catch (Exception e){
                    e.getMessage();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listVagas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Vaga vaga = listaVagas.get(position);
                Intent detalharVaga = new Intent(getActivity(), Detalhes_vaga_usuario.class);
                //Converter dados da classe em json
                JSONObject jsonObject = vaga.convertToJson();
                //enviar dados para activity
                detalharVaga.putExtra("vagaatual", jsonObject.toString());

                startActivity(detalharVaga);
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

    private void preencheListaVaga(DataSnapshot dataSnapshot){
        //loop por estados
        for(DataSnapshot estados: dataSnapshot.getChildren()){
            //loop por cidades
            for(DataSnapshot cidades: estados.getChildren()){
                //loop por cargos
                for(DataSnapshot cargos: cidades.getChildren()){
                    //loop por vagas
                    for(DataSnapshot vagas: cargos.getChildren()){
                        Vaga vaga = vagas.getValue(Vaga.class);
                        listaVagas.add(vaga);
                    }
                }
            }

        }
    }

    private void preencheListaVagaComFiltro(DataSnapshot dadoslista, Filtro dadosFiltros){
        //loop por estados
        for(DataSnapshot estados: dadoslista.getChildren()){
            if (estados.getKey().toString().equals(dadosFiltros.getEstado().toString())){
                //loop por cidades
                for(DataSnapshot cidades: estados.getChildren()){
                    if (cidades.getKey().toString().equals(dadosFiltros.getCidade().toString())){
                        //loop por cargos
                        for(DataSnapshot cargos: cidades.getChildren()){
                            if (cidades.getKey().toString().equals(dadosFiltros.getCidade().toString())){
                                //loop por vagas
                                for(DataSnapshot vagas: cargos.getChildren()){
                                    Vaga vaga = vagas.getValue(Vaga.class);
                                    //Filtrar habilidades
                                    if (dadosFiltros.getHabilidades() != null){
                                        String[] hab = dadosFiltros.getHabilidades().split(",");
                                        for (int i = 0; i < hab.length; i++) {
                                            if (vaga.getHabilidades().contains(hab[i].trim().replace(",",""))){
                                                listaVagas.add(vaga);
                                                break;
                                            }
                                        }
                                    }else{
                                        listaVagas.add(vaga);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void verificarFiltroAntesPreencher(final DataSnapshot dadosFullLista){
        databaseReference.child(controller.NODE_FILTROS).child(idUsuarioLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaVagas.clear();
                Filtro filtro = dataSnapshot.getValue(Filtro.class);
                if (filtro != null){
                    //preencher lista com filtros
                    preencheListaVagaComFiltro(dadosFullLista,filtro);
                }else{
                    //preencher lista sem filtros
                    preencheListaVaga(dadosFullLista);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
