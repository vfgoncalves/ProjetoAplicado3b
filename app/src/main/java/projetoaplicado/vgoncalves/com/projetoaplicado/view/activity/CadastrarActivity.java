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
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONObject;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class CadastrarActivity extends AppCompatActivity {
    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button btnCadastrar;
    private ImageButton imgSignGoogle;
    private ImageButton imgSignLinkedin;
    private GoogleApiClient mGoogleApiClient;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private Controller controller;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogLinkedin;

    private boolean authGoogle = false;
    private boolean authLinkedin = false;

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
        imgSignLinkedin = (ImageButton) findViewById(R.id.imgSignLinkedin);

        //Configurando progressDialog
        progressDialog = new ProgressDialog(CadastrarActivity.this);
        progressDialog.setMessage("Aguarde, estamos efetuando o cadastro");
        progressDialog.setCancelable(false);

        progressDialogLinkedin = new ProgressDialog(CadastrarActivity.this);
        progressDialogLinkedin.setMessage("Aguarde, estamos efetuando o cadastro com a conta Linkedin");
        progressDialogLinkedin.setCancelable(false);

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
                authGoogle = true;
                authLinkedin = false;
                signIn();
            }
        });

        imgSignLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authGoogle = false;
                authLinkedin = true;
                signInLinkedin();
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
                    progressDialog.hide();
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

        if(authLinkedin){
            //AUTENTICAÇÃO COM LINKEDIN

            // Add this line to your existing onActivityResult() method
            progressDialogLinkedin.show();
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        }else if(authGoogle){
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                progressDialog.show();
                handleSignInResult(result);
            }
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
    private void signInLinkedin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                buscarInfoPessoal();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                mostraMensagem("Erro ao logar com linkedin");
                progressDialogLinkedin.hide();
                Log.d("AuthLinkedin", error.toString());
            }
        }, true);
    }
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }
    private void buscarInfoPessoal(){
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address)";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                try{
                    cadastrarLinkedin(apiResponse.getResponseDataAsJson());
                }catch (Exception e){
                    e.getMessage();
                }
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
            }
        });
    }
    private void cadastrarLinkedin(final JSONObject user){
        try{
            String senha = controller.geraSenhaLinkedin(user.getString("emailAddress"));
            autenticacao.createUserWithEmailAndPassword(user.getString("emailAddress"),senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    try{
                        if (task.isSuccessful()){
                            mostraMensagem("Usuário cadastrada com sucesso");
                            //salvar dados do usuário
                            usuario = new Usuario(CadastrarActivity.this);
                            usuario.setID(task.getResult().getUser().getUid());
                            usuario.setNome(user.getString("firstName") + " " + user.getString("lastName"));
                            usuario.setEmail(user.getString("emailAddress"));
                            usuario.setPhotoUrl(user.getString("pictureUrl"));
                            usuario.salvar();

                            //Navegar até tela main - Tela principal
                            Intent intent = new Intent(CadastrarActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            progressDialogLinkedin.hide();
                        }else{
                            try{
                                progressDialog.hide();
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
                    }catch (Exception e){
                        e.getMessage();
                    }
                }
            });
        }catch (Exception e){
            mostraMensagem("Erro ao logar com a conta Linkedin");
            progressDialogLinkedin.hide();
            e.getMessage();
        }
    }

}
