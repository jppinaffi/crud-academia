package com.academia.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/** READ: lista os alunos; toque = editar; botão Excluir = remover. */
public class ListaActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView listView;
    private TextView tvVazio;
    private EditText etBusca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        db = new DatabaseHelper(this);
        listView = findViewById(R.id.listAlunos);
        tvVazio = findViewById(R.id.tvVazio);
        etBusca = findViewById(R.id.etBusca);
        Button btnNovo = findViewById(R.id.btnNovo);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        btnNovo.setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));
        btnVoltar.setOnClickListener(v -> finish());

        listView.setOnItemClickListener((parent, view, pos, id) -> {
            Aluno a = (Aluno) parent.getItemAtPosition(pos);
            Intent i = new Intent(this, EditarActivity.class);
            i.putExtra("id_aluno", a.getId());
            startActivity(i);
        });

        etBusca.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) { carregar(); }
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar(); // atualiza a lista ao voltar das outras telas
    }

    private void carregar() {
        List<Aluno> alunos = db.listar(etBusca.getText().toString());
        listView.setAdapter(new AlunoAdapter(this, alunos, this::confirmarExclusao));
        tvVazio.setVisibility(alunos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void confirmarExclusao(final Aluno aluno) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir aluno")
                .setMessage("Excluir " + aluno.getNome() + "?")
                .setPositiveButton("Excluir", (d, w) -> {
                    if (db.excluir(aluno.getId()) > 0) {
                        Toast.makeText(this, "Aluno excluído!", Toast.LENGTH_SHORT).show();
                        carregar();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
