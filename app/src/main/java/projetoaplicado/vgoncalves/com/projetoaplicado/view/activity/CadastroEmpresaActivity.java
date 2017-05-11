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

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
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
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class CadastroEmpresaActivity extends AppCompatActivity {
    private EditText nome;
    private EditText email;
    private EditText senha;
    private EditText telefone;
    private Button btnCadastrar;
    private Empresa empresa;
    private ImageButton imgSignGoogleEmp;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth autenticacao;
    private Controller controller;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogLinkedin;
    private ImageButton imgSignLinkedinEmp;

    private boolean authGoogle = false;
    private boolean authLinkedin = false;

    //Constantes
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_empresa);

        incializarControlesConfiguracao();

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    empresa = new Empresa(CadastroEmpresaActivity.this);
                    empresa.setEmail(email.getText().toString());
                    empresa.setNome(nome.getText().toString());
                    empresa.setSenha(senha.getText().toString());
                    empresa.setTelefone(telefone.getText().toString());
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

        imgSignGoogleEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authGoogle = true;
                authLinkedin = false;
                signIn();
            }
        });

        imgSignLinkedinEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authGoogle = false;
                authLinkedin = true;
                signInLinkedin();
            }
        });

    }
    public void cadastrarUsuario(){
        autenticacao.createUserWithEmailAndPassword(empresa.getEmail(),empresa.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mostraMensagem("Empresa cadastrada com sucesso");
                    //salvar dados do usuário
                    empresa.setID(task.getResult().getUser().getUid());
                    empresa.salvar();

                    //Navegar até tela main - Tela principal
                    Intent intent = new Intent(CadastroEmpresaActivity.this, MainEmpresaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    progressDialog.hide();
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
        if (TextUtils.isEmpty(telefone.getText())){
            mostraMensagem("Favor preencher o campo telefone!");
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

            //AUTENTICAÇÃO COM GOOGLE

            Log.d("ProjetoAplicado", "Método onActivityResult");
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                progressDialog.show();
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.]
            Log.d("ProjetoAplicado", "Método handleSignInResult - Sucesso");
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
                        empresa = new Empresa(CadastroEmpresaActivity.this);
                        empresa.setNome(acct.getDisplayName());
                        empresa.setEmail(acct.getEmail());
                        empresa.setID(autenticacao.getCurrentUser().getUid());
                        empresa.setPhotoUrl(acct.getPhotoUrl().toString());
                        empresa.salvar();

                        mostraMensagem("Login com a conta google efetuado com sucesso");

                        //Navegar até tela main - Tela principal
                        Intent intent = new Intent(CadastroEmpresaActivity.this, MainEmpresaActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        progressDialog.hide();
                    } else {
                        progressDialog.hide();
                        mostraMensagem("Erro ao logar com a conta google");
                    }
                }
            });
        }catch (Exception e){
            Log.d("ProjetoAplicado", "Erro Método firebaseAuthWithGoogle - " + e.getMessage());
            mostraMensagem("Erro ao efetuar login com conta Google: " + e.getMessage());
        }
    }

    /*
    INÍCIO DO CADASTRO COM CONTA LINKEDIN
     */
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
                            mostraMensagem("Empresa cadastrada com sucesso");
                            //salvar dados do usuário
                            empresa = new Empresa(CadastroEmpresaActivity.this);
                            empresa.setID(task.getResult().getUser().getUid());
                            empresa.setNome(user.getString("firstName") + " " + user.getString("lastName"));
                            empresa.setEmail(user.getString("emailAddress"));
                            empresa.setPhotoUrl(user.getString("pictureUrl"));
                            empresa.salvar();

                            //Navegar até tela main - Tela principal
                            Intent intent = new Intent(CadastroEmpresaActivity.this, MainEmpresaActivity.class);
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
    private void incializarControlesConfiguracao(){
        nome = (EditText) findViewById(R.id.editCadNome);
        email = (EditText) findViewById(R.id.editCadEmail);
        telefone = (EditText) findViewById(R.id.editCadTel);
        senha = (EditText) findViewById(R.id.editCadSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        imgSignGoogleEmp = (ImageButton) findViewById(R.id.imgSignGoogleEmp);
        imgSignLinkedinEmp = (ImageButton) findViewById(R.id.imgSignLinkedinEmp);

        //Mascaras
        SimpleMaskFormatter maskTel = new SimpleMaskFormatter("(NN)NNNN-NNNN");
        MaskTextWatcher watcherTel = new MaskTextWatcher(telefone, maskTel);

        //Aplica mascaras aos devidos controles
        telefone.addTextChangedListener(watcherTel);

        //Configurando progressDialog
        progressDialog = new ProgressDialog(CadastroEmpresaActivity.this);
        progressDialog.setMessage("Aguarde, estamos efetuando o cadastro");
        progressDialog.setCancelable(false);

        progressDialogLinkedin = new ProgressDialog(CadastroEmpresaActivity.this);
        progressDialogLinkedin.setMessage("Aguarde, estamos efetuando o cadastro com a conta Linkedin");
        progressDialogLinkedin.setCancelable(false);

        controller = new Controller(CadastroEmpresaActivity.this);
        autenticacao = controller.getAutenticador();
    }
}
