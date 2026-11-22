package com.academia.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/** UPDATE e DELETE: edita ou exclui um aluno existente. */
public class EditarActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private FormHelper form;
    private int idAluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        db = new DatabaseHelper(this);
        form = new FormHelper(this, findViewById(android.R.id.content));

        idAluno = getIntent().getIntExtra("id_aluno", -1);
        Aluno aluno = db.buscarPorId(idAluno);
        if (aluno == null) {
            Toast.makeText(this, "Aluno não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        form.preencher(aluno);

        Button btnSalvar = findViewById(R.id.btnSalvar);
        Button btnExcluir = findViewById(R.id.btnExcluir);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        btnSalvar.setOnClickListener(v -> {
            Aluno a = form.lerEValidar();
            if (a == null) return;
            a.setId(idAluno);
            int linhas = db.atualizar(a);
            if (linhas > 0) {
                Toast.makeText(this, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erro ao atualizar (CPF duplicado?)", Toast.LENGTH_LONG).show();
            }
        });

        btnExcluir.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Excluir aluno")
                        .setMessage("Deseja realmente excluir este aluno?")
                        .setPositiveButton("Excluir", (d, w) -> {
                            if (db.excluir(idAluno) > 0) {
                                Toast.makeText(this, "Aluno excluído!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show());

        btnVoltar.setOnClickListener(v -> finish());
    }
}
