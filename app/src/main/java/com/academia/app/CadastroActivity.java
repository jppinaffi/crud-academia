package com.academia.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/** CREATE: cadastra um novo aluno. */
public class CadastroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        final FormHelper form = new FormHelper(this, findViewById(android.R.id.content));
        final DatabaseHelper db = new DatabaseHelper(this);

        Button btnSalvar = findViewById(R.id.btnSalvar);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        btnSalvar.setOnClickListener(v -> {
            Aluno a = form.lerEValidar();
            if (a == null) return;
            long id = db.inserir(a);
            if (id == -1) {
                Toast.makeText(this, "Erro: CPF já cadastrado!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // volta para a tela anterior
            }
        });

        btnVoltar.setOnClickListener(v -> finish());
    }
}
