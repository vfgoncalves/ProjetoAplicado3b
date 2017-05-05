package projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments.EmpresaPerfilFragment;
import projetoaplicado.vgoncalves.com.projetoaplicado.view.fragments.VagasEmpresaFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    private String[] titulosAbas = {"VAGAS CADASTRADAS"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;

        switch (i){
            case 0:
                fragment = new VagasEmpresaFragment();
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
