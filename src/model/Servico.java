package model;

import java.io.Serializable;

/**
 * Entidade Serviço (Menu de serviços).
 */
public class Servico implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private double preco;

    public Servico(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    // --- Getters ---
    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    @Override
    public String toString() {
        return nome + " (R$ " + String.format("%.2f", preco) + ")";
    }
}
