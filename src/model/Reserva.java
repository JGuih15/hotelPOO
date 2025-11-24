package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Reserva: Representa uma estadia ativa (Check-in).
 */
public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;
    private Hospede hospede;
    private Quarto quarto;
    private LocalDate dataCheckin;
    private int diasEstadia;
    private List<Servico> consumoServicos;

    public Reserva(Hospede hospede, Quarto quarto, int diasEstadia) {
        this.hospede = hospede;
        this.quarto = quarto;
        this.dataCheckin = LocalDate.now();
        this.diasEstadia = diasEstadia;
        this.consumoServicos = new ArrayList<>();
        this.quarto.setOcupado(true); // Marca o quarto como ocupado
    }

    public void adicionarServico(Servico servico) {
        this.consumoServicos.add(servico);
    }

    /**
     * Calcula o valor total da fatura.
     */
    public double calcularValorTotal() {
        // 1. Custo da Estadia
        double custoEstadia = quarto.calcularDiaria() * diasEstadia;

        // 2. Custo dos Serviços
        double custoServicos = consumoServicos.stream().mapToDouble(Servico::getPreco).sum();

        // 3. Aplica desconto VIP de 10% nos serviços
        if (hospede.isVip()) {
            System.out.println("Desconto VIP de 10% aplicado nos serviços.");
            custoServicos *= 0.9;
        }

        return custoEstadia + custoServicos;
    }

    // --- Getters ---
    public Hospede getHospede() {
        return hospede;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public LocalDate getDataCheckin() {
        return dataCheckin;
    }

    public int getDiasEstadia() {
        return diasEstadia;
    }

    public List<Servico> getConsumoServicos() {
        return consumoServicos;
    }

    // ... outros getters
}
