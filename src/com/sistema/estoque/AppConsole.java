package com.sistema.estoque;

import java.util.List;

public class AppConsole {
    public static void main(String[] args) {
        System.out.println("=== Sistema de Estoque (Console) — MongoDB + Redis ===");
        ProdutoDAO dao = new ProdutoDAO();

        Produto novo = new Produto("Mouse Gamer", 159.90, 20, "Periféricos");
        String id = dao.criar(novo);
        System.out.println("Criado: " + novo);

        List<Produto> lista = dao.listarTodos();
        System.out.println("\nLista inicial (" + lista.size() + "):");
        lista.forEach(System.out::println);

        Produto upd = new Produto("Mouse Gamer RGB", 179.90, 18, "Periféricos");
        boolean ok = dao.atualizar(id, upd);
        System.out.println("\nAtualização " + (ok ? "OK" : "falhou") + ": " + id);

        lista = dao.listarTodos();
        System.out.println("\nApós atualizar (" + lista.size() + "):");
        lista.forEach(System.out::println);

        boolean del = dao.excluir(id);
        System.out.println("\nExclusão " + (del ? "OK" : "falhou") + ": " + id);

        lista = dao.listarTodos();
        System.out.println("\nApós excluir (" + lista.size() + "):");
        lista.forEach(System.out::println);

        System.out.println("\nFim da demonstração.");
    }
}
