package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.Helper;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextView lnkCadastrar;
    private Button btnLogar;
    private FirebaseAuth autenticador;
    private DatabaseReference databaseReference;
    private EditText email;
    private EditText senha;
    private Usuario usuario;
    private Empresa empresa;
    private ProgressDialog progressDialog;
    private SignInButton btnGoogle;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticador = ConfiguracaoFirebase.getAutenticador();
        databaseReference = ConfiguracaoFirebase.getReferenceFirebase();
        //Configurar Progress Dialog
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Identificando usuário logado");
        progressDialog.setMessage("Por favor aguarde, estamos identificando o usuário logado");
        progressDialog.setCancelable(false);

        verificarUsuarioLogado();

        //Receber controles da Activity
        lnkCadastrar = (TextView) findViewById(R.id.lnkCadastrar);
        btnLogar = (Button) findViewById(R.id.btnLogin);
        btnGoogle = (SignInButton) findViewById(R.id.btnSigInGoogle);


        //Método de click no link Cadastrar, levando à próxima activity
        lnkCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ContextoActivity.class);
                startActivity(intent);
            }
        });

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    autenticador.signInWithEmailAndPassword(
                            email.getText().toString(),
                            senha.getText().toString()
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mostraMensagem("Login efetuado com sucesso");
                                abrirTelaPrincipal();
                            }else{
                                mostraMensagem("Erro ao fazer login");
                            }
                        }
                    });
                }
            }
        });

        /*INÍCIO: LOGIN COM GOOOGLE*/
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("ProjetoAplicado", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("ProjetoAplicado", "onAuthStateChanged:signed_out");
                }
            }
        };

        /*FIM: LOGIN COM GOOOGLE*/
    }

    private boolean validarCampos(){
        if (TextUtils.isEmpty(email.getText())){
            mostraMensagem("Favor preencher o campo e-mail!");
            return false;
        }

        if (TextUtils.isEmpty(senha.getText())){
            mostraMensagem("Favor preencher o campo senha!");
            return false;
        }
        return true;
    }
    private void mostraMensagem(String mensagem){
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
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

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ProjetoAplicado", "Método onActivityResult");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.]
            Log.d("ProjetoAplicado", "Método handleSignInResult - Sucesso");
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            // Signed out, show unauthenticated UI.
            Log.d("ProjetoAplicado", "Método handleSignInResult - Falhou");
            mostraMensagem("Erro ao logar com a conta Google");
        }
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        try {
            Log.d("ProjetoAplicado", "Método firebaseAuthWithGoogle");
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            autenticador.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.show();
                        abrirTelaPrincipal();
                        mostraMensagem("Login com a conta google efetuado com sucesso");
                    } else {
                        Log.d("ProjetoAplicado", "Erro ao logar");
                        mostraMensagem("Erro ao logar com a conta google");
                    }
                }
            });
        }catch (Exception e){
            Log.d("ProjetoAplicado", "Erro Método firebaseAuthWithGoogle - " + e.getMessage());
            mostraMensagem("Erro ao efetuar login com conta Google: " + e.getMessage());
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                            Intent intent = new Intent(LoginActivity.this, MainEmpresaActivity.class);
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
