package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Habilidades;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Controller controller;
    private DatabaseReference databaseReference;
    private EditText descHab;
    private Button btnCadastrarHab;
    private Button btnMostrarHab;
    private Habilidades habilidades;
    private AlertDialog dialog;
    private ArrayList<String> habilidadesArray;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        controller = new Controller(this);
        databaseReference = controller.getDatabaseReference();

        descHab = (EditText) findViewById(R.id.editHabilidade);
        btnCadastrarHab = (Button) findViewById(R.id.btnCadastrarHab);
        btnMostrarHab = (Button) findViewById(R.id.btnMostrarHab);

        //Configurar progress dialog
        progressDialog = new ProgressDialog(ConfiguracoesActivity.this);
        progressDialog.setMessage("Cadastrando habilidades...");
        progressDialog.setCancelable(false);

        btnCadastrarHab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(descHab.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Favor informar a descrição da habilidade", Toast.LENGTH_LONG).show();
                }else{
                    progressDialog.show();
                    verificarHabilidadeExistente();
                }
            }
        });

        configurarDialog();

        btnMostrarHab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }
    private void verificarHabilidadeExistente(){
        databaseReference.child(controller.NODE_HABILIDADES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean parcial = true;
                for(DataSnapshot hablidade: dataSnapshot.getChildren()){
                    Habilidades hab = hablidade.getValue(Habilidades.class);
                    if (hab.getDescricao().equals(descHab.getText().toString().toUpperCase())){
                        parcial = false;
                        break;
                    }
                }

                if (parcial){
                    habilidades = new Habilidades();
                    habilidades.setDescricao(descHab.getText().toString());
                    if (habilidades.salvar()){
                        Toast.makeText(getApplicationContext(), "Habilidade cadastrada com sucesso", Toast.LENGTH_LONG).show();
                        configurarDialog();
                        descHab.setText("");
                        progressDialog.hide();
                    }else{
                        Toast.makeText(getApplicationContext(), "Erro ao cadastrar habilidade", Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Habilidade já cadastrada", Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void configurarDialog(){
        databaseReference.child(controller.NODE_HABILIDADES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if (habilidadesArray == null)
                        habilidadesArray = new ArrayList<String>();

                    habilidadesArray.clear();
                    for(DataSnapshot hablidade: dataSnapshot.getChildren()){
                        Habilidades hab = hablidade.getValue(Habilidades.class);
                        habilidadesArray.add(hab.getDescricao());
                    }

                    CharSequence[] items = habilidadesArray.toArray(new CharSequence[habilidadesArray.size()]);

                    //Cria dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracoesActivity.this);
                    builder.setTitle("Habilidades Cadastradas");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            // Do something with the selection
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();


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
