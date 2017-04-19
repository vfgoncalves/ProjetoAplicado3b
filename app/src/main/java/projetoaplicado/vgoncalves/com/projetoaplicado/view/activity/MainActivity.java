package projetoaplicado.vgoncalves.com.projetoaplicado.view.activity;

import android.app.ProgressDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import projetoaplicado.vgoncalves.com.projetoaplicado.R;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.TabAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.adapter.UsuarioTabAdapter;
import projetoaplicado.vgoncalves.com.projetoaplicado.componente.tabs.SlidingTabLayout;
import projetoaplicado.vgoncalves.com.projetoaplicado.controller.Controller;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private Controller controller;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configurar Progress Dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Carregando");
        progressDialog.setMessage("Por favor aguarde, estamos carregando informações do usuário");
        progressDialog.setCancelable(false);

        //Inicializar controles
        toolbar = (Toolbar) findViewById(R.id.toolbarEmpresa);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sltl_abas);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        //Configurar Toolbar
        toolbar.setTitle("UNAJob");
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.branco));
        setSupportActionBar(toolbar); //Método de suporte ao ActionBar

        //configurar adapter e tabs
        UsuarioTabAdapter tabAdapter = new UsuarioTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        //configura sliding tab
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));
        slidingTabLayout.setViewPager(viewPager);

        //Armazenar id do usuário logado
        idUsuario = controller.getIdUsuario();

    }
}
