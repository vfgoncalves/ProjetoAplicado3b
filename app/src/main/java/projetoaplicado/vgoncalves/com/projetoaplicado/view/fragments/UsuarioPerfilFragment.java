package projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import projetoaplicado.vgoncalves.com.projetoaplicado.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsuarioPerfilFragment extends Fragment {


    public UsuarioPerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usuario_perfil, container, false);
    }

}
