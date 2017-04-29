package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Empresa;
import projetoaplicado.vgoncalves.com.projetoaplicado.Model.Usuario;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.TabAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.tabs.SlidingTabLayout;
import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class MainEmpresaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ProgressDialog progressDialog;
    private FirebaseAuth autenticador;
    private Controller controller;
    private String idUsuario;
    private Empresa empresa;
    private DatabaseReference databaseReference;

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

        controller = new Controller(MainEmpresaActivity.this);
        autenticador = controller.getAutenticador();
        databaseReference = controller.getDatabaseReference();

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
        idUsuario = controller.getIdUsuario();
        databaseReference.child(controller.NODE_EMPRESA).child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresa = dataSnapshot.getValue(Empresa.class);
                toolbar.setTitle("UNAJob - Bem Vindo " + empresa.getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        progressDialog.show();
        autenticador.signOut();
        controller.limpaIdUsuario();

        //Navegar até tela de login
        Intent intent = new Intent(MainEmpresaActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        progressDialog.hide();
    }
    private void direcionarConfiguracoes(){

        Intent intent = new Intent(MainEmpresaActivity.this, ConfiguracoesActivity.class);
        startActivity(intent);
    }
}
