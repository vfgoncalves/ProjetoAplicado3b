package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.Helper;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;

public class HomeActivity extends AppCompatActivity {
    private Button btnLogar;
    private Button btnCadastrar;
    private FirebaseAuth autenticador;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private Usuario usuario;
    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        autenticador = ConfiguracaoFirebase.getAutenticador();
        databaseReference = ConfiguracaoFirebase.getReferenceFirebase();

        //Configurar Progress Dialog
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setTitle("Identificando usuário logado");
        progressDialog.setMessage("Por favor aguarde, estamos identificando o usuário logado");
        progressDialog.setCancelable(false);

        verificarUsuarioLogado();

        btnCadastrar = (Button) findViewById(R.id.btnHomeCadastrar);
        btnLogar = (Button) findViewById(R.id.btnHomeLogar);

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
        Log.d("ProjetoAplicado", "Método - verificarUsuarioLogado");
        if (autenticador.getCurrentUser() != null){
            progressDialog.show();
            Log.d("ProjetoAplicado", "Método - verificarUsuarioLogado - Usuário logado identificado");
            abrirTelaPrincipal();
        }else{
            Log.d("ProjetoAplicado", "Método - verificarUsuarioLogado - Usuário logado não identificado");
        }
    }

    private void abrirTelaPrincipal(){
        Log.d("ProjetoAplicado", "Método - abrirTelaPrincipal");
        Helper helper = new Helper();

        Log.d("ProjetoAplicado", "Método - abrirTelaPrincipal - salvarPreferencia");
        SharedPreferences sharedPreferences = getSharedPreferences(helper.NOME_ARQUIVO,0);
        helper.editor = sharedPreferences.edit();
        helper.salvarPreferencia(autenticador.getCurrentUser().getUid());

        Log.d("ProjetoAplicado", "Método - abrirTelaPrincipal - verificar usuario ou empresa");
        //Verificar se o usuário logado é uma empresa ou um candidato
        //bucar usuário
        databaseReference
                .child(ConfiguracaoFirebase.NODE_USUARIO)
                .child(autenticador.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuario = dataSnapshot.getValue(Usuario.class);
                        if (usuario != null){
                            Log.d("ProjetoAplicado", "Método - abrirTelaPrincipal - usuário encontrado");
                            //Navegar até menu principal de usuáriio
                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            progressDialog.hide();
                        }else{
                            Log.d("ProjetoAplicado", "Método - abrirTelaPrincipal - usuário não encontrado");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //buscar empresa
        databaseReference
                .child(ConfiguracaoFirebase.NODE_EMPRESA)
                .child(autenticador.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        empresa = dataSnapshot.getValue(Empresa.class);
                        if (empresa != null){
                            Log.d("ProjetoAplicado", "Método - abrirTelaPrincipal - empresa encontrado");
                            //Navegar até menu principal de empresa
                            Intent intent = new Intent(HomeActivity.this, MainEmpresaActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            progressDialog.hide();
                        }else{
                            Log.d("ProjetoAplicado", "Método - abrirTelaPrincipal - empresa não encontrado");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
