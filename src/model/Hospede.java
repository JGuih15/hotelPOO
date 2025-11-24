package model;

import java.io.Serializable;

/**
 * Entidade Hóspede, implementando Encapsulamento e Programa de Fidelidade.
 */
public class Hospede implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cpf;
    private String nome;
    private int pontosFidelidade;
    private boolean isVip; // Requisito: descontos e personalização para VIPs

    public Hospede(String cpf, String nome) {
        this.cpf = cpf;
        this.nome = nome;
        this.pontosFidelidade = 0;
        this.isVip = false; // Inicialmente não é VIP
    }

    // --- Métodos de Negócio ---

    public void acumularPontos(double gastoTotal) {
        // Acumula 1 ponto a cada R$ 10,00 gastos (exemplo de regra)
        int novosPontos = (int) (gastoTotal / 10.0);
        this.pontosFidelidade += novosPontos;
        if (this.pontosFidelidade >= 500 && !this.isVip) {
            this.isVip = true;
            System.out.println("Parabéns! O hóspede " + this.nome + " atingiu o status VIP!");
        }
    }

    public boolean trocarPontosPorDesconto(int pontosParaTrocar) {
        if (this.pontosFidelidade >= pontosParaTrocar) {
            this.pontosFidelidade -= pontosParaTrocar;
            return true;
        }
        return false;
    }

    // --- Getters e Setters (Encapsulamento) ---

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public int getPontosFidelidade() {
        return pontosFidelidade;
    }

    public boolean isVip() {
        return isVip;
    }

    @Override
    public String toString() {
        return "Nome: " + nome + ", CPF: " + cpf + ", Pontos: " + pontosFidelidade + (isVip ? " (VIP)" : "");
    }
}