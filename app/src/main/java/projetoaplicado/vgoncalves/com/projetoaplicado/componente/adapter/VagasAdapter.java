package projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;

public class VagasAdapter extends ArrayAdapter<Vaga> {

    private ArrayList<Vaga> vagas;
    private Context context;

    public VagasAdapter(Context c, ArrayList<Vaga> objects) {
        super(c, 0, objects);

        this.vagas = objects;
        this.context = c;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        try{
            //Verifica se a lista está vazia
            if( vagas != null){
                //Inicializar objeto para montagem da view
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

                //Montar view a partir do xml
                view = inflater.inflate(R.layout.lista_vagas, parent,false);

                //recupera elemento para exibição
                TextView tituloVaga = (TextView) view.findViewById(R.id.tv_Titulo);
                TextView nomeEmpresa = (TextView) view.findViewById(R.id.tv_Empresa);

                Vaga vaga = vagas.get(position);
                tituloVaga.setText(vaga.getTitulo().toString());
                if (vaga.getNomeEmpresa() != null){
                    nomeEmpresa.setText(vaga.getNomeEmpresa().toString());
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return view;
    }
}
