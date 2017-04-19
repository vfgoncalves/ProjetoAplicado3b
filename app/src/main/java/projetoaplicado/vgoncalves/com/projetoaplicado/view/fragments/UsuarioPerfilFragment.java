package projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.EditarPerfilActivity;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.activity.EditarUsuarioPerfilActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsuarioPerfilFragment extends Fragment {

    private Controller controller;
    private DatabaseReference databaseReference;
    private String idUsuario;

    //Controles da tela
    private EditText editNome;
    private EditText editEmail;
    private EditText editrelefoneCel;
    private EditText editTelefoneResidencial;
    private EditText editCEP;
    private EditText editPais;
    private EditText editEstado;
    private EditText editCidade;
    private EditText editBairro;
    private EditText editRua;
    private EditText editNumero;
    private EditText editComplemento;
    private FloatingActionButton editarPerfilUsuario;
    private Usuario usuario;


    public UsuarioPerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario_perfil, container, false);

        controller = new Controller(view.getContext());
        databaseReference = controller.getDatabaseReference();
        idUsuario = controller.getIdUsuario();

        //Instânciar controles da tela
        editNome = (EditText) view.findViewById(R.id.editUserNome);
        editEmail = (EditText) view.findViewById(R.id.editUserEmail);
        editrelefoneCel = (EditText) view.findViewById(R.id.editUserTelCel);
        editTelefoneResidencial = (EditText) view.findViewById(R.id.editUserTelRes);
        editCEP = (EditText) view.findViewById(R.id.editUserCEP);
        editPais = (EditText) view.findViewById(R.id.editUserPais);
        editEstado = (EditText) view.findViewById(R.id.editUserEst);
        editCidade = (EditText) view.findViewById(R.id.editUserCid);
        editBairro = (EditText) view.findViewById(R.id.editUserBairro);
        editRua = (EditText) view.findViewById(R.id.editUserRua);
        editNumero = (EditText) view.findViewById(R.id.editUserNumero);
        editComplemento = (EditText) view.findViewById(R.id.editUserCompl);
        editarPerfilUsuario = (FloatingActionButton) view.findViewById(R.id.editarPerfilUsuario);

        databaseReference.child(controller.NODE_USUARIO).child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario = dataSnapshot.getValue(Usuario.class);
                if (usuario != null){
                    usuario.setID(idUsuario);
                    preencherCampos();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editarPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navegar até tela de edição de perfil
                Intent intent = new Intent(getActivity(), EditarUsuarioPerfilActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
    private void preencherCampos(){
        editNome.setText(usuario.getNome());
        editEmail.setText(usuario.getEmail());
        editrelefoneCel.setText(usuario.getTelefoneCel());
        editTelefoneResidencial.setText(usuario.getTelefoneResidencial());
        editCEP.setText(usuario.getCEP());
        editPais.setText(usuario.getPais());
        editEstado.setText(usuario.getEstado());
        editCidade.setText(usuario.getCidade());
        editBairro.setText(usuario.getBairro());
        editRua.setText(usuario.getRua());
        editNumero.setText(usuario.getNumero());
        editComplemento.setText(usuario.getComplemento());
    }

}
