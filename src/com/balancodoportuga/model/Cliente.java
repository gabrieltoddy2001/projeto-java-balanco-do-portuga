package com.balancodoportuga.model;

public class Cliente {
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String endereco;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cnh;

    // Construtor completo
    public Cliente(int id, String nome, String cpf, String email, String telefone,
                   String endereco, String numero, String bairro, String cidade,
                   String estado, String cnh) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cnh = cnh;
    }

    // Construtor reduzido
    public Cliente(int id, String nome, String cpf, String email, String telefone) {
        this(id, nome, cpf, email, telefone, "", "", "", "", "", "");
    }

    public Cliente(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getEndereco() { return endereco; }
    public String getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }

    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }

    @Override
    public String toString() {
        return nome;
    }
}