package projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.DetalheVagaActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.DetalhesVagaUserActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.FiltroActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class VagasUsuarioFragment extends Fragment {

    private com.github.clans.fab.FloatingActionButton btnRemoverFiltro;
    private com.github.clans.fab.FloatingActionButton btnEditFiltro;

    private ListView listVagas;
    private ArrayAdapter adapter;
    private ArrayList<Vaga> listaVagas;
    private DatabaseReference databaseReference;
    private String idUsuarioLogado;
    private Controller controller;
    private ProgressDialog progressDialog;

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
        btnEditFiltro = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.item_Filtro);
        btnRemoverFiltro = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.item_RemoverFiltro);

        listVagas = (ListView) view.findViewById(R.id.listaFragmentVagasUser);
        //adapter = new ArrayAdapter(getActivity(), R.layout.lista_vagas, listaVagas);
        //Configuração do Adapter personalizado par alitar vagas
        adapter = new VagasAdapter(getActivity(), listaVagas);

        //Instanciar lista
        listVagas.setAdapter(adapter);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Carregando vagas e aplicando filtros...");
        progressDialog.setCancelable(false);

        carregarFiltros();


        listVagas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Vaga vaga = listaVagas.get(position);
                Intent detalharVaga = new Intent(getActivity(), DetalhesVagaUserActivity.class);
                //Converter dados da classe em json
                JSONObject jsonObject = vaga.convertToJson();
                //enviar dados para activity
                detalharVaga.putExtra("vagaatual", jsonObject.toString());
                startActivity(detalharVaga);
            }
        });

        btnEditFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Iniciar tela para aplicar filtros nas vagas
                Intent intent = new Intent(getActivity(), FiltroActivity.class);
                startActivity(intent);
            }
        });

        btnRemoverFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(controller.NODE_FILTROS).child(idUsuarioLogado).removeValue();
                carregarFiltros();
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
        //Filtro por Estados
        boolean filtroEstado = !(dadosFiltros.getEstado() == null || TextUtils.isEmpty(dadosFiltros.getEstado().toString()));
        //Filtro por Cidades
        boolean filtroCidade = !(dadosFiltros.getCidade() == null || TextUtils.isEmpty(dadosFiltros.getCidade().toString()));
        //Filtro por Cargos
        boolean filtroCargos = !(dadosFiltros.getCargo() == null || TextUtils.isEmpty(dadosFiltros.getCargo().toString()));
        //Filtro por Habilidade
        boolean filtroHabilidade = !(dadosFiltros.getHabilidades() == null || TextUtils.isEmpty(dadosFiltros.getHabilidades().toString()));

        //Aplicar filtros em todas as vagas
        for(DataSnapshot estados: dadoslista.getChildren()){
            for(DataSnapshot cidades: estados.getChildren()){
                for(DataSnapshot cargos: cidades.getChildren()){
                    for(DataSnapshot vagas: cargos.getChildren()){
                        Vaga vaga = vagas.getValue(Vaga.class);

                        //Aplica filtro por estado
                        if(filtroEstado && !listaVagas.contains(vaga)){
                            if (vaga.getEstado().equals(dadosFiltros.getEstado()))
                                listaVagas.add(vaga);
                        }
                        //Aplica filtro por cidade
                        if(filtroCidade && !listaVagas.contains(vaga)){
                            if (vaga.getCidade().equals(dadosFiltros.getCidade()))
                                listaVagas.add(vaga);
                        }
                        //Aplica filtro por cargo
                        if(filtroCargos && !listaVagas.contains(vaga)){
                            if (vaga.getCargo().equals(dadosFiltros.getCargo()))
                                listaVagas.add(vaga);
                        }
                        //Aplica filtro por habilidade
                        if (filtroHabilidade && !listaVagas.contains(vaga)){
                            String[] hab = dadosFiltros.getHabilidades().split(",");
                            for (int i = 0; i < hab.length; i++) {
                                if (vaga.getHabilidades().contains(hab[i].trim().replace(",",""))){
                                    listaVagas.add(vaga);
                                    break;
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
                progressDialog.hide();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void carregarFiltros(){
        progressDialog.show();

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
    }
}
