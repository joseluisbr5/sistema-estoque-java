package com.sistema.estoque;

public class Produto {
    private String id; // _id do Mongo em Hex
    private String nome;
    private double preco;
    private int quantidade;
    private String categoria;

    public Produto() {}

    public Produto(String nome, double preco, int quantidade, String categoria) {
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.categoria = categoria;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return String.format("[%s] %s | R$%.2f | Qtd: %d | %s", id != null ? id : "-", nome, preco, quantidade, categoria);
    }
}
