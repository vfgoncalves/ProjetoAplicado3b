package projetoaplicado.vgoncalves.com.projetoaplicado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import projetoaplicado.vgoncalves.com.projetoaplicado.Adapter.TabAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.ConfiguracaoFirebase;
import projetoaplicado.vgoncalves.com.projetoaplicado.Config.SlidingTabLayout;

public class MainEmpresaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ProgressDialog progressDialog;
    private FirebaseAuth autenticador;

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
        toolbar = (Toolbar) findViewById(R.id.toolbarEmpresa);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sltl_abas);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);
        autenticador = ConfiguracaoFirebase.getAutenticador();

        //Configurar Toolbar
        toolbar.setTitle("UNAJob");
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.branco));
        setSupportActionBar(toolbar); //Método de suporte ao ActionBar

        //configurar adapter e tabs
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        //configura sliding tab
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));
        slidingTabLayout.setViewPager(viewPager);

        //Armazenar id do usuário logado
        /*final Helper helper = new Helper();
        SharedPreferences sharedPreferences = getSharedPreferences(helper.NOME_ARQUIVO,0);
        helper.sharedPreferences = sharedPreferences;
        idUsuario = helper.getIdUsuario();*/
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        progressDialog.show();

        autenticador.signOut();
        //Navegar até tela de login
        Intent intent = new Intent(MainEmpresaActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        progressDialog.hide();
    }
}
