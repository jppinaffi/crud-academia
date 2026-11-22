package com.academia.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** Cria/abre/atualiza o banco SQLite e concentra as operações CRUD. */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "academia.db";
    private static final int DB_VERSION = 1;
    private static final String TABELA = "alunos";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABELA + " (" +
            "id_aluno INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome TEXT NOT NULL, " +
            "cpf TEXT NOT NULL UNIQUE, " +
            "telefone TEXT NOT NULL, " +
            "email TEXT, " +
            "endereco TEXT, " +
            "data_nascimento TEXT NOT NULL, " +
            "modalidade TEXT NOT NULL, " +
            "tipo_plano TEXT NOT NULL, " +
            "valor_mensalidade REAL NOT NULL, " +
            "data_matricula TEXT NOT NULL, " +
            "validade_plano TEXT NOT NULL, " +
            "situacao TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }

    // CREATE - retorna o id gerado, ou -1 em caso de erro (ex.: CPF duplicado)
    public long inserir(Aluno a) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABELA, null, toValues(a));
    }

    // READ - lista todos (ou filtra por nome/CPF)
    public List<Aluno> listar(String busca) {
        List<Aluno> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        if (busca == null || busca.trim().isEmpty()) {
            c = db.query(TABELA, null, null, null, null, null, "nome COLLATE NOCASE ASC");
        } else {
            String like = "%" + busca.trim() + "%";
            c = db.query(TABELA, null, "nome LIKE ? OR cpf LIKE ?",
                    new String[]{like, like}, null, null, "nome COLLATE NOCASE ASC");
        }
        while (c.moveToNext()) lista.add(fromCursor(c));
        c.close();
        return lista;
    }

    public Aluno buscarPorId(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABELA, null, "id_aluno = ?", new String[]{String.valueOf(id)},
                null, null, null);
        Aluno a = c.moveToFirst() ? fromCursor(c) : null;
        c.close();
        return a;
    }

    // UPDATE - retorna número de linhas afetadas
    public int atualizar(Aluno a) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.update(TABELA, toValues(a), "id_aluno = ?",
                    new String[]{String.valueOf(a.getId())});
        } catch (Exception e) { // ex.: CPF duplicado
            return -1;
        }
    }

    // DELETE - retorna número de linhas removidas
    public int excluir(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABELA, "id_aluno = ?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(Aluno a) {
        ContentValues v = new ContentValues();
        v.put("nome", a.getNome());
        v.put("cpf", a.getCpf());
        v.put("telefone", a.getTelefone());
        v.put("email", a.getEmail());
        v.put("endereco", a.getEndereco());
        v.put("data_nascimento", a.getDataNascimento());
        v.put("modalidade", a.getModalidade());
        v.put("tipo_plano", a.getTipoPlano());
        v.put("valor_mensalidade", a.getValorMensalidade());
        v.put("data_matricula", a.getDataMatricula());
        v.put("validade_plano", a.getValidadePlano());
        v.put("situacao", a.getSituacao());
        return v;
    }

    private Aluno fromCursor(Cursor c) {
        Aluno a = new Aluno();
        a.setId(c.getInt(c.getColumnIndexOrThrow("id_aluno")));
        a.setNome(c.getString(c.getColumnIndexOrThrow("nome")));
        a.setCpf(c.getString(c.getColumnIndexOrThrow("cpf")));
        a.setTelefone(c.getString(c.getColumnIndexOrThrow("telefone")));
        a.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
        a.setEndereco(c.getString(c.getColumnIndexOrThrow("endereco")));
        a.setDataNascimento(c.getString(c.getColumnIndexOrThrow("data_nascimento")));
        a.setModalidade(c.getString(c.getColumnIndexOrThrow("modalidade")));
        a.setTipoPlano(c.getString(c.getColumnIndexOrThrow("tipo_plano")));
        a.setValorMensalidade(c.getDouble(c.getColumnIndexOrThrow("valor_mensalidade")));
        a.setDataMatricula(c.getString(c.getColumnIndexOrThrow("data_matricula")));
        a.setValidadePlano(c.getString(c.getColumnIndexOrThrow("validade_plano")));
        a.setSituacao(c.getString(c.getColumnIndexOrThrow("situacao")));
        return a;
    }
}
