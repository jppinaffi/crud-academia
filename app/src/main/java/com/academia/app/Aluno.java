package com.academia.app;

/** Model: representa um aluno da academia (uma linha da tabela "alunos"). */
public class Aluno {
    private int id;
    private String nome, cpf, telefone, email, endereco, dataNascimento;
    private String modalidade, tipoPlano, dataMatricula, validadePlano, situacao;
    private double valorMensalidade;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String d) { this.dataNascimento = d; }
    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }
    public String getTipoPlano() { return tipoPlano; }
    public void setTipoPlano(String tipoPlano) { this.tipoPlano = tipoPlano; }
    public double getValorMensalidade() { return valorMensalidade; }
    public void setValorMensalidade(double v) { this.valorMensalidade = v; }
    public String getDataMatricula() { return dataMatricula; }
    public void setDataMatricula(String d) { this.dataMatricula = d; }
    public String getValidadePlano() { return validadePlano; }
    public void setValidadePlano(String v) { this.validadePlano = v; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}
