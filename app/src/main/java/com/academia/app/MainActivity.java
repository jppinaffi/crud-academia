package com.academia.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/** Tela inicial: menu de navegação. */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        Button btnListar = findViewById(R.id.btnListar);

        btnCadastrar.setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));
        btnListar.setOnClickListener(v -> startActivity(new Intent(this, ListaActivity.class)));
    }
}
