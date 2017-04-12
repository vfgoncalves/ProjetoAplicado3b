package projetoaplicado.vgoncalves.com.projetoaplicado.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import projetoaplicado.vgoncalves.com.projetoaplicado.CadastrarVagaActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.Helper;
import projetoaplicado.vgoncalves.com.projetoaplicado.EditarPerfilActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmpresaPerfilFragment extends Fragment {

    private EditText editEmpCompl;
    private EditText editEmpNumero;
    private EditText editEmpRua;
    private EditText editEmpBairro;
    private EditText editEmpCid;
    private EditText editEmpEst;
    private EditText editEmpPais;
    private EditText editEmpCEP;
    private EditText editEmpTel;
    private EditText editEmpEmail;
    private EditText editEmpNome;
    private DatabaseReference databaseReference;
    private String idEmpresa;
    private Empresa empresa;
    private FloatingActionButton editarPerfilEmpresa;

    public EmpresaPerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empresa_perfil, container, false);

        //Receber instância do Firebase
        databaseReference = ConfiguracaoFirebase.getReferenceFirebase();

        //Armazenar id do usuário logado
        Helper helper = new Helper();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(helper.NOME_ARQUIVO,0);
        helper.sharedPreferences = sharedPreferences;
        idEmpresa = helper.getIdUsuario();

        //Inicializar controle
        editEmpCompl = (EditText) view.findViewById(R.id.editEmpCompl);
        editEmpNumero= (EditText) view.findViewById(R.id.editEmpNumero);
        editEmpRua= (EditText) view.findViewById(R.id.editEmpRua);
        editEmpBairro= (EditText) view.findViewById(R.id.editEmpBairro);
        editEmpCid= (EditText) view.findViewById(R.id.editEmpCid);
        editEmpEst= (EditText) view.findViewById(R.id.editEmpEst);
        editEmpPais= (EditText) view.findViewById(R.id.editEmpPais);
        editEmpCEP= (EditText) view.findViewById(R.id.editEmpCEP);
        editEmpTel= (EditText) view.findViewById(R.id.editEmpTel);
        editEmpEmail= (EditText) view.findViewById(R.id.editEmpEmail);
        editEmpNome= (EditText) view.findViewById(R.id.editEmpNome);
        editarPerfilEmpresa = (FloatingActionButton) view.findViewById(R.id.editarPerfilEmpresa);

        databaseReference
                .child(ConfiguracaoFirebase.NODE_EMPRESA)
                .child(idEmpresa)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        empresa = dataSnapshot.getValue(Empresa.class);
                        if (empresa != null){
                            empresa.setID(idEmpresa);
                            preencherCampos();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        editarPerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navegar até tela de edição de perfil
                Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void preencherCampos(){
        editEmpCompl.setText(empresa.getComplemento());
        editEmpNumero.setText(empresa.getNumero());
        editEmpRua.setText(empresa.getRua());
        editEmpBairro.setText(empresa.getBairro());
        editEmpCid.setText(empresa.getCidade());
        editEmpEst.setText(empresa.getEstado());
        editEmpPais.setText(empresa.getPais());
        editEmpCEP.setText(empresa.getCEP());
        editEmpTel.setText(empresa.getTelefone());
        editEmpEmail.setText(empresa.getEmail());
        editEmpNome.setText(empresa.getNome());
    }

}
