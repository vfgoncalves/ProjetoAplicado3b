package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GoogleAuthProvider;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class CadastrarActivity extends AppCompatActivity {
    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button btnCadastrar;
    private ImageButton imgSignGoogle;
    private GoogleApiClient mGoogleApiClient;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private Controller controller;
    private ProgressDialog progressDialog;

    //Constantes
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        nome = (EditText) findViewById(R.id.editCadNome);
        email = (EditText) findViewById(R.id.editCadEmail);
        senha = (EditText) findViewById(R.id.editCadSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        imgSignGoogle = (ImageButton) findViewById(R.id.imgSignGoogle);

        //Configurando progressDialog
        progressDialog = new ProgressDialog(CadastrarActivity.this);
        progressDialog.setTitle("Cadastrando");
        progressDialog.setMessage("Aguarde, estamos efetuando o cadastro");
        progressDialog.setCancelable(false);

        controller = new Controller(CadastrarActivity.this);
        autenticacao = controller.getAutenticador();

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    usuario = new Usuario(getApplicationContext());
                    //Receber campos da activity
                    usuario.setEmail(email.getText().toString());
                    usuario.setNome(nome.getText().toString());
                    usuario.setSenha(senha.getText().toString());
                    progressDialog.show();
                    cadastrarUsuario();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        imgSignGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    public void cadastrarUsuario(){
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(CadastrarActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mostraMensagem("Usuário cadastrado com sucesso");
                    //salvar dados do usuário
                    usuario.setID(task.getResult().getUser().getUid());
                    usuario.salvar();

                    //Navegar até tela main - Tela principal
                    Intent intent = new Intent(CadastrarActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    progressDialog.hide();
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        mostraMensagem("Digite uma senha mais forte e longa!");
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        mostraMensagem("E-mail inválido, por favor digitar um novo e-mail!");
                    }catch (FirebaseAuthUserCollisionException e){
                        mostraMensagem("E-mail já está em uso, por favor digite outro e-mail");
                    }catch (Exception e){
                        mostraMensagem("Erro ao cadastrar usuário, tente novamente!");
                    }
                }
            }
        });
    }
    private boolean validarCampos(){
        if (TextUtils.isEmpty(nome.getText())){
            mostraMensagem("Favor preencher o campo nome!");
            return false;
        }

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
    private void signIn() {
       Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
       startActivityForResult(signInIntent, RC_SIGN_IN);
   }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            progressDialog.show();
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            progressDialog.hide();
            mostraMensagem("Erro ao logar com a conta Google");
        }
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        try {
        Log.d("ProjetoAplicado", "Método firebaseAuthWithGoogle");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        autenticacao.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("ProjetoAplicado", "Login sucesso");
                    usuario = new Usuario();
                    usuario.setNome(acct.getDisplayName());
                    usuario.setEmail(acct.getEmail());
                    usuario.setID(autenticacao.getCurrentUser().getUid());
                    usuario.setPhotoUrl(acct.getPhotoUrl().toString());
                    usuario.salvar();

                    mostraMensagem("Login com a conta google efetuado com sucesso");

                    //Navegar até tela main - Tela principal
                    Intent intent = new Intent(CadastrarActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    progressDialog.hide();
                }else{
                    progressDialog.hide();
                }
            }
        });
        }catch (Exception e){
            Log.d("ProjetoAplicado", "Erro Método firebaseAuthWithGoogle - " + e.getMessage());
            mostraMensagem("Erro ao efetuar login com conta Google: " + e.getMessage());
        }
    }
}
