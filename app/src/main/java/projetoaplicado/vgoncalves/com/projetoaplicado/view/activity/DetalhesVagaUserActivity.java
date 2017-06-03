package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Candidatura;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Vaga;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class DetalhesVagaUserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Vaga vaga;
    private String stringJson;
    private JSONObject jsonObject;
    private String idUsuario;
    private Controller controller;
    private DatabaseReference databaseReference;

    private EditText editDetVagaEmailContato;
    private EditText editDetCargo;
    private EditText editDetEstado;
    private EditText editDetCidade;
    private EditText editDetSelecHab;
    private EditText editDetVagaDescricao;

    private Button btnCandidatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_vaga_user);


        try{

            Bundle extra = getIntent().getExtras();

            //testar dados passados pelo fragment
            if(extra != null){
                stringJson = (String) extra.get("vagaatual");
                jsonObject = new JSONObject(stringJson);
                vaga = new Vaga();
                vaga.readJson(jsonObject);
            }
            toolbar = (Toolbar) findViewById(R.id.tb_vagaEmp);
            toolbar.setTitle(vaga.getTitulo());
            toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
            setSupportActionBar(toolbar);
            inicializarControles();
            preencherCampos();
            habilitaDesabilitaCampos(false);

            verificarCandidatura();

            btnCandidatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    candidatarAVaga();
                    Toast.makeText(DetalhesVagaUserActivity.this, "Candidatura enviada com sucesso!", Toast.LENGTH_LONG).show();
                }
            });

        }catch (Exception e){
            e.getMessage();
        }

    }
    private void  inicializarControles(){
        controller = new Controller(DetalhesVagaUserActivity.this);
        idUsuario = controller.getIdUsuario();
        databaseReference = controller.getDatabaseReference();
        editDetVagaEmailContato = (EditText) findViewById(R.id.editDetVagaEmailContato);
        editDetCargo = (EditText) findViewById(R.id.editDetCargo);
        editDetEstado = (EditText) findViewById(R.id.editDetEstado);
        editDetCidade = (EditText) findViewById(R.id.editDetCidade);
        editDetSelecHab = (EditText) findViewById(R.id.editDetSelecHab);
        editDetVagaDescricao = (EditText) findViewById(R.id.editDetVagaDescricao);
        btnCandidatar = (Button) findViewById(R.id.btnEnviarCandidatura);

    }
    private void preencherCampos(){
        editDetVagaEmailContato.setText(vaga.getEmailContato());
        editDetCargo.setText(vaga.getCargo());
        editDetEstado.setText(vaga.getEstado());
        editDetCidade.setText(vaga.getCidade());
        editDetSelecHab.setText(vaga.getHabilidades());
        editDetVagaDescricao.setText(vaga.getDescricao());
    }
    private void habilitaDesabilitaCampos(boolean habilita){
        editDetVagaEmailContato.setEnabled(habilita);
        editDetCargo.setEnabled(habilita);
        editDetEstado.setEnabled(habilita);
        editDetCidade.setEnabled(habilita);
        editDetSelecHab.setEnabled(habilita);
        editDetVagaDescricao.setEnabled(habilita);
    }
    private void candidatarAVaga(){
        try{
            databaseReference.child(controller.NODE_USUARIO).child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{

                        Candidatura candidatura = new Candidatura();
                        candidatura.setIdUsuario(idUsuario);
                        candidatura.setCandidato(dataSnapshot.getValue(Usuario.class));
                        candidatura.salvar(vaga);
                        desabilitarBotao();

                    }catch (Exception e){
                        e.getMessage();
                        Toast.makeText(DetalhesVagaUserActivity.this, "Não foi possível efetuar a candidatura! Tente novamente", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }catch (Exception e){
            Toast.makeText(this, "Não foi possível efetuar a candidatura, tente novamente!", Toast.LENGTH_LONG).show();
            e.getMessage();
        }
    }
    private void desabilitarBotao(){
        try{
            //Altera botão de candidatura para que não seja possível se candidatar duas vezes
            btnCandidatar.setText("VOCÊ JÁ É CANDIDATO DESTA VAGA");
            btnCandidatar.setEnabled(false);
            btnCandidatar.setBackgroundColor(getResources().getColor(R.color.verde));
        }catch (Exception e){
            e.getMessage();
        }
    }
    private void verificarCandidatura(){
        try {

                    databaseReference.child(controller.NODE_VAGA)
                    .child(vaga.getEstado())
                    .child(vaga.getCidade())
                    .child(vaga.getCargo())
                    .child(vaga.getIdVaga())
                    .child(controller.NODE_CANDIDATURAS)
                    .child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        desabilitarBotao();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }catch (Exception e){
            e.getMessage();
        }
    }
}
