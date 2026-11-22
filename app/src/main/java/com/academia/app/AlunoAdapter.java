package com.academia.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

/** Adapter: conecta a lista de Alunos (vinda do banco) ao ListView. */
public class AlunoAdapter extends ArrayAdapter<Aluno> {

    public interface OnExcluirListener { void onExcluir(Aluno aluno); }

    private final OnExcluirListener listener;

    public AlunoAdapter(Context ctx, List<Aluno> alunos, OnExcluirListener listener) {
        super(ctx, 0, alunos);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_aluno, parent, false);

        final Aluno a = getItem(position);
        ((TextView) convertView.findViewById(R.id.tvNome)).setText(a.getNome());
        ((TextView) convertView.findViewById(R.id.tvDetalhe)).setText(
                a.getModalidade() + " • " + a.getTipoPlano() + " • " +
                String.format(Locale.US, "R$ %.2f", a.getValorMensalidade()));
        ((TextView) convertView.findViewById(R.id.tvStatus)).setText(
                "Situação: " + a.getSituacao() + " | Validade: " + a.getValidadePlano());

        Button btn = convertView.findViewById(R.id.btnExcluirItem);
        btn.setOnClickListener(v -> listener.onExcluir(a));
        return convertView;
    }
}
