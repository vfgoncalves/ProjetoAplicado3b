package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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

public class LoginActivity extends AppCompatActivity {

    //Controles da tela
    private TextView lnkCadastrar;
    private Button btnLogar;
    private EditText email;
    private EditText senha;
    private ImageButton imgLoginGoogle;
    private ImageButton imgLoginLinkedin;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogLinkedin;

    //Constantes
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth autenticador;
    private DatabaseReference databaseReference;
    private GoogleApiClient mGoogleApiClient;
    private Usuario usuario;
    private Empresa empresa;
    private Controller controller;

    private boolean authGoogle = false;
    private boolean authLinkedin = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarControlesConfiguracao();

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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        imgLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authGoogle = true;
                authLinkedin = false;
                signIn();
            }
        });

        imgLoginLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authGoogle = false;
                authLinkedin = true;
                signInLinkedin();
            }
        });
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
                handleSignInResult(result);
            }
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            progressDialog.show();
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            mostraMensagem("Erro ao logar com a conta Google");
        }
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            autenticador.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.show();
                        abrirTelaPrincipal();
                        mostraMensagem("Login com a conta google efetuado com sucesso");
                        progressDialog.hide();
                    } else {
                        mostraMensagem("Erro ao logar com a conta google");
                        progressDialog.hide();
                    }
                }
            });
        }catch (Exception e){
            mostraMensagem("Erro ao efetuar login com conta Google: " + e.getMessage());
        }
    }
    private void abrirTelaPrincipal(){
        controller.salvarPrefIdUsuario(autenticador.getCurrentUser().getUid());

        //Verificar se o usuário logado é uma empresa ou um candidato
        //bucar usuário
        databaseReference.child(controller.NODE_USUARIO).child(autenticador.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario = dataSnapshot.getValue(Usuario.class);
                if (usuario != null){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
        //Buscar empresa
        databaseReference.child(controller.NODE_EMPRESA).child(autenticador.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /*
    INÍCIO LOGIN COM LINKEDIN
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
            autenticador.signInWithEmailAndPassword(user.getString("emailAddress"),senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        abrirTelaPrincipal();
                        mostraMensagem("Login com a conta Linkedin efetuado com sucesso");
                        progressDialogLinkedin.hide();
                    } else {
                        mostraMensagem("Erro ao logar com a conta google");
                        progressDialogLinkedin.hide();
                    }
                }
            });
        }catch (Exception e){
            mostraMensagem("Erro ao logar com a conta Linkedin");
            progressDialogLinkedin.hide();
            e.getMessage();
        }
    }
    private void inicializarControlesConfiguracao(){
        //Receber controles da Activity
        lnkCadastrar = (TextView) findViewById(R.id.lnkCadastrar);
        btnLogar = (Button) findViewById(R.id.btnLogin);
        imgLoginGoogle = (ImageButton) findViewById(R.id.imgLoginGoogle);
        imgLoginLinkedin = (ImageButton) findViewById(R.id.imgLoginLinkedin);
        email = (EditText) findViewById(R.id.editLoginEmail);

        senha = (EditText) findViewById(R.id.editLoginSenha);

        //Configurando progressDialog
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Efetuando login com a conta Google");
        progressDialog.setCancelable(false);//Configurando progressDialog

        progressDialogLinkedin = new ProgressDialog(LoginActivity.this);
        progressDialogLinkedin.setMessage("Efetuando login com a conta Linkedin");
        progressDialogLinkedin.setCancelable(false);

        controller = new Controller(LoginActivity.this);
        databaseReference = controller.getDatabaseReference();
        autenticador = controller.getAutenticador();
    }
}
