package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class HomeActivity extends AppCompatActivity {
    private Button btnLogar;
    private Button btnCadastrar;
    private FirebaseAuth autenticador;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private Usuario usuario;
    private Empresa empresa;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnCadastrar = (Button) findViewById(R.id.btnHomeCadastrar);
        btnLogar = (Button) findViewById(R.id.btnHomeLogar);

        //Configurar Progress Dialog
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setTitle("Identificando usuário logado");
        progressDialog.setMessage("Por favor aguarde, estamos identificando o usuário logado");
        progressDialog.setCancelable(false);

        controller = new Controller(HomeActivity.this);
        autenticador = controller.getAutenticador();
        databaseReference = controller.getDatabaseReference();

        verificarUsuarioLogado();

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ContextoActivity.class);
                startActivity(intent);
            }
        });

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void verificarUsuarioLogado(){
        if (autenticador.getCurrentUser() != null){
            progressDialog.show();
            abrirTelaPrincipal();
        }
    }

    private void abrirTelaPrincipal(){
        controller.salvarPrefIdUsuario(autenticador.getCurrentUser().getUid());

        databaseReference.child(controller.NODE_USUARIO).child(autenticador.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuario = dataSnapshot.getValue(Usuario.class);
                        if (usuario != null){
                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            progressDialog.hide();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //buscar empresa
        databaseReference.child(controller.NODE_EMPRESA).child(autenticador.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        empresa = dataSnapshot.getValue(Empresa.class);
                        if (empresa != null){
                            Intent intent = new Intent(HomeActivity.this, MainEmpresaActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            progressDialog.hide();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
