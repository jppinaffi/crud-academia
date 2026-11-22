package com.academia.app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Locale;

/** Liga os campos do formulário (form_aluno.xml), valida e converte para/de Aluno. */
public class FormHelper {

    private static final String[] MODALIDADES =
            {"Musculação", "Crossfit", "Pilates", "Spinning", "Natação", "Muay Thai", "Dança"};
    private static final String[] PLANOS = {"Mensal", "Trimestral", "Semestral", "Anual"};
    private static final String[] SITUACOES = {"Ativo", "Inativo", "Trancado"};

    private final Context ctx;
    private final EditText etNome, etCpf, etTelefone, etEmail, etEndereco,
            etNascimento, etValor, etMatricula, etValidade;
    private final Spinner spModalidade, spPlano, spSituacao;

    public FormHelper(Context ctx, View root) {
        this.ctx = ctx;
        etNome = root.findViewById(R.id.etNome);
        etCpf = root.findViewById(R.id.etCpf);
        etTelefone = root.findViewById(R.id.etTelefone);
        etEmail = root.findViewById(R.id.etEmail);
        etEndereco = root.findViewById(R.id.etEndereco);
        etNascimento = root.findViewById(R.id.etNascimento);
        etValor = root.findViewById(R.id.etValor);
        etMatricula = root.findViewById(R.id.etMatricula);
        etValidade = root.findViewById(R.id.etValidade);
        spModalidade = root.findViewById(R.id.spModalidade);
        spPlano = root.findViewById(R.id.spPlano);
        spSituacao = root.findViewById(R.id.spSituacao);

        configurarSpinner(spModalidade, MODALIDADES);
        configurarSpinner(spPlano, PLANOS);
        configurarSpinner(spSituacao, SITUACOES);
        configurarData(etNascimento);
        configurarData(etMatricula);
        configurarData(etValidade);
    }

    private void configurarSpinner(Spinner sp, String[] itens) {
        sp.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, itens));
    }

    private void configurarData(final EditText et) {
        et.setFocusable(false);
        et.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(ctx, (view, y, m, d) ->
                    et.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    /** Retorna null se houver erro de validação (o erro é exibido no campo). */
    public Aluno lerEValidar() {
        if (vazio(etNome)) return erro(etNome, "Informe o nome");
        String cpf = etCpf.getText().toString().replaceAll("\\D", "");
        if (cpf.length() != 11) return erro(etCpf, "CPF deve ter 11 dígitos");
        if (vazio(etTelefone)) return erro(etTelefone, "Informe o telefone");
        String email = etEmail.getText().toString().trim();
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return erro(etEmail, "E-mail inválido");
        if (vazio(etNascimento)) return erro(etNascimento, "Informe a data de nascimento");
        double valor;
        try {
            valor = Double.parseDouble(etValor.getText().toString().trim().replace(",", "."));
            if (valor <= 0) return erro(etValor, "Valor deve ser maior que zero");
        } catch (NumberFormatException e) {
            return erro(etValor, "Informe um valor válido");
        }
        if (vazio(etMatricula)) return erro(etMatricula, "Informe a data da matrícula");
        if (vazio(etValidade)) return erro(etValidade, "Informe a validade do plano");

        Aluno a = new Aluno();
        a.setNome(etNome.getText().toString().trim());
        a.setCpf(cpf);
        a.setTelefone(etTelefone.getText().toString().trim());
        a.setEmail(email);
        a.setEndereco(etEndereco.getText().toString().trim());
        a.setDataNascimento(etNascimento.getText().toString());
        a.setModalidade(spModalidade.getSelectedItem().toString());
        a.setTipoPlano(spPlano.getSelectedItem().toString());
        a.setValorMensalidade(valor);
        a.setDataMatricula(etMatricula.getText().toString());
        a.setValidadePlano(etValidade.getText().toString());
        a.setSituacao(spSituacao.getSelectedItem().toString());
        return a;
    }

    public void preencher(Aluno a) {
        etNome.setText(a.getNome());
        etCpf.setText(a.getCpf());
        etTelefone.setText(a.getTelefone());
        etEmail.setText(a.getEmail());
        etEndereco.setText(a.getEndereco());
        etNascimento.setText(a.getDataNascimento());
        etValor.setText(String.valueOf(a.getValorMensalidade()));
        etMatricula.setText(a.getDataMatricula());
        etValidade.setText(a.getValidadePlano());
        selecionar(spModalidade, MODALIDADES, a.getModalidade());
        selecionar(spPlano, PLANOS, a.getTipoPlano());
        selecionar(spSituacao, SITUACOES, a.getSituacao());
    }

    private void selecionar(Spinner sp, String[] itens, String valor) {
        for (int i = 0; i < itens.length; i++)
            if (itens[i].equals(valor)) { sp.setSelection(i); return; }
    }

    private boolean vazio(EditText et) { return et.getText().toString().trim().isEmpty(); }

    private Aluno erro(EditText et, String msg) {
        et.setError(msg);
        et.requestFocus();
        return null;
    }
}
