package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.TabAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.UsuarioTabAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.tabs.SlidingTabLayout;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.transform.CircleTransform;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class MainActivity extends AppCompatActivity {
    //private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Controller controller;
    private String idUsuario;
    private DatabaseReference databaseReference;
    private FirebaseAuth autenticador;
    private Usuario usuario;
    private ImageView imgPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            //Configurar Progress Dialog
            /*
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Carregando");
            progressDialog.setMessage("Por favor aguarde, estamos carregando informações do usuário");
            progressDialog.setCancelable(false);
             */

            //Inicializar controles
            toolbar = (Toolbar) findViewById(R.id.toolbarUsuario);
            slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sltl_abasUsuario);
            viewPager = (ViewPager) findViewById(R.id.vp_paginaUsuario);

            controller = new Controller(this);
            databaseReference = controller.getDatabaseReference();
            autenticador = controller.getAutenticador();

            //Configurar Toolbar
            toolbar.setTitle("UNAJob");
            toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.branco));
            setSupportActionBar(toolbar); //Método de suporte ao ActionBar

            imgPerfil = (ImageView) toolbar.findViewById(R.id.imgPerfil);

            //configurar adapter e tabs
            UsuarioTabAdapter tabAdapter = new UsuarioTabAdapter(getSupportFragmentManager());
            viewPager.setAdapter(tabAdapter);
            //configura sliding tab
            slidingTabLayout.setDistributeEvenly(true);
            slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));
            slidingTabLayout.setViewPager(viewPager);

            //Armazenar id do usuário logado
            idUsuario = controller.getIdUsuario();

            imgPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    direcionarPerfil();;
                }
            });

            databaseReference.child(controller.NODE_USUARIO).child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usuario = dataSnapshot.getValue(Usuario.class);
                    if (usuario != null){
                        if (usuario.getPhotoUrl() != null){
                            if (!TextUtils.isEmpty(usuario.getPhotoUrl())){
                                Picasso.with(MainActivity.this).load(usuario.getPhotoUrl()).transform(new CircleTransform()).into(imgPerfil);
                            }else {
                                Picasso.with(MainActivity.this).load(R.drawable.ic_account_circle).into(imgPerfil);
                            }
                        }else{
                            Picasso.with(MainActivity.this).load(R.drawable.ic_account_circle).into(imgPerfil);
                        }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_empresa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_Configuracoes:
                direcionarConfiguracoes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deslogarUsuario(){
        //progressDialog.show();
        autenticador.signOut();
        controller.limpaIdUsuario();

        //Navegar até tela de login
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        //progressDialog.hide();
    }
    private void direcionarConfiguracoes(){

        Intent intent = new Intent(MainActivity.this, ConfiguracoesActivity.class);
        startActivity(intent);
    }
    private void direcionarPerfil(){
        Intent intent = new Intent(MainActivity.this, EditarUsuarioPerfilActivity.class);
        startActivity(intent);
    }
}
