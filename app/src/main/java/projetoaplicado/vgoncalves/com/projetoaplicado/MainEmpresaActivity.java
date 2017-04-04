package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.Helper;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;

public class MainEmpresaActivity extends AppCompatActivity {
    private String idUsuario;
    private DatabaseReference databaseReference;
    private Empresa empresa;
    private TextView txtTitulo;
    private ProgressDialog progressDialog;
    private ImageButton imgPerfil;
    private ImageButton imgLogout;
    private FirebaseAuth autenticador;
    private Bitmap imagemPerfil;
    private ImageButton imgVagas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_empresa);

        //Configurar Progress Dialog
        progressDialog = new ProgressDialog(MainEmpresaActivity.this);
        progressDialog.setTitle("Carregando");
        progressDialog.setMessage("Por favor aguarde, estamos carregando informações do usuário");
        progressDialog.setCancelable(false);

        //Inicializar controles
        txtTitulo = (TextView) findViewById(R.id.txtTitulo);
        imgPerfil = (ImageButton) findViewById(R.id.imgEditPerfil);
        imgLogout= (ImageButton) findViewById(R.id.imgLogout);
        imgVagas = (ImageButton) findViewById(R.id.imgVagas);
        autenticador = ConfiguracaoFirebase.getAutenticador();

        //Armazenar id do usuário logado
        final Helper helper = new Helper();
        SharedPreferences sharedPreferences = getSharedPreferences(helper.NOME_ARQUIVO,0);
        helper.sharedPreferences = sharedPreferences;
        idUsuario = helper.getIdUsuario();

        //Recuperar instância do Firebase
        databaseReference = ConfiguracaoFirebase.getReferenceFirebase();

        //Recuperar dados do usuário logado
        databaseReference
                .child(ConfiguracaoFirebase.NODE_EMPRESA)
                .child(idUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.show();
                empresa = dataSnapshot.getValue(Empresa.class);
                txtTitulo.setText("Bem vindo " + empresa.getNome().toString());
                if (!TextUtils.isEmpty(empresa.getPhotoUrl())){
                    //Carregar Imagem de perfil
                    Helper helperPerfil = new Helper();
                    imagemPerfil = helperPerfil.baixarImagem(empresa.getPhotoUrl());
                    if (imagemPerfil != null)
                        imgPerfil.setImageBitmap(imagemPerfil);
                    }
                progressDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Evento de logout
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                autenticador.signOut();
                //Navegar até tela de login
                Intent intent = new Intent(MainEmpresaActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                progressDialog.hide();
            }
        });

        //Evento para navegação até tela de edição de perfil
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navegar até tela de edição de perfil
                Intent intent = new Intent(MainEmpresaActivity.this, EditarPerfilActivity.class);
                startActivity(intent);
            }
        });

        //Evento de Click para tela de Vagas
        imgVagas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navegar até tela de cadastro de vagas
                Intent intent = new Intent(MainEmpresaActivity.this, VagasEmpresaActivity.class);
                startActivity(intent);
            }
        });

    }
}
