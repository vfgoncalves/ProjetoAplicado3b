package projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments.EmpresaPerfilFragment;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments.UsuarioPerfilFragment;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments.VagasEmpresaFragment;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments.VagasUsuarioFragment;

public class UsuarioTabAdapter extends FragmentStatePagerAdapter {

    private String[] titulosAbas = {"PERFIL","VAGAS"};

    public UsuarioTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;

        switch (i){
            case 0:
                fragment = new UsuarioPerfilFragment();
                break;
            case 1:
                fragment = new VagasUsuarioFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titulosAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titulosAbas[position];
    }
}
