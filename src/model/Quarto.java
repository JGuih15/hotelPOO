package model;

import java.io.Serializable;

/**
 * Classe Abstrata Quarto: Define a estrutura e métodos comuns (Abstração).
 */
public abstract class Quarto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String numero;
    private double precoBase;
    private boolean ocupado;
    private String descricao;

    public Quarto(String numero, double precoBase, String descricao) {
        this.numero = numero;
        this.precoBase = precoBase;
        this.ocupado = false; // Inicialmente desocupado
        this.descricao = descricao;
    }

    /**
     * Polimorfismo: Método abstrato para calcular a diária.
     * Deve ser implementado por todas as subclasses.
     */
    public abstract double calcularDiaria();

    // --- Getters e Setters (Encapsulamento) ---

    public String getNumero() {
        return numero;
    }

    public double getPrecoBase() {
        return precoBase;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipo() {
        // Retorna o nome da classe, que indica o tipo do quarto.
        return this.getClass().getSimpleName().replace("Quarto", "");
    }

    @Override
    public String toString() {
        return "Nº: " + numero + " | Tipo: " + getTipo() + " | Diária: R$ "
                + String.format("%.2f", calcularDiaria()) + " | Status: "
                + (ocupado ? "OCUPADO" : "LIVRE");
    }
}
