package projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.transform.CircleTransform;

public class CandidatoAdapter extends ArrayAdapter<Usuario> {

    private ArrayList<Usuario> listCandidatos;
    private ArrayList<Double> listPct;
    private Context context;

    public CandidatoAdapter(Context c, ArrayList<Usuario> objects, ArrayList<Double> listPct) {
        super(c, 0, objects);

        this.listCandidatos = objects;
        this.listPct = listPct;
        this.context = c;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        try{
            //Verifica se a lista está vazia
            if( listCandidatos != null){
                //Inicializar objeto para montagem da view
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

                //Montar view a partir do xml
                view = inflater.inflate(R.layout.lista_candidatos, parent,false);

                //recupera elemento para exibição
                TextView nomeUsuario = (TextView) view.findViewById(R.id.tv_busca_NomeCandidato);
                TextView pctHab = (TextView) view.findViewById(R.id.tv_busca_Pct);
                ImageView imgPerfil = (ImageView) view.findViewById(R.id.tv_busca_ImgPerfil);

                Usuario usuario = listCandidatos.get(position);

                if (usuario.getPhotoUrl() != null){
                    if (!TextUtils.isEmpty(usuario.getPhotoUrl())){
                        Picasso.with(context).load(usuario.getPhotoUrl()).transform(new CircleTransform()).into(imgPerfil);
                    }else{
                        Picasso.with(context).load(R.drawable.ic_usuario_new).transform(new CircleTransform()).into(imgPerfil);
                    }
                }else{
                    Picasso.with(context).load(R.drawable.ic_usuario_new).transform(new CircleTransform()).into(imgPerfil);
                }

                nomeUsuario.setText(usuario.getNome().toString());
                pctHab.setText("Atende " + listPct.get(position).toString() + "% das habilidades técnicas");
            }
        }catch (Exception e){
            e.getMessage();
        }
        return view;
    }

}
