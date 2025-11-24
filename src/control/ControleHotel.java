package control;

import exceptions.ReservaInvalidaException;
import model.*;
import utils.PersistenciaDados;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe de Controle: Centraliza a lógica de negócio do hotel.
 */
public class ControleHotel implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Quarto> quartos;
    private Map<String, Hospede> hospedes; // CPF como chave
    private List<Reserva> reservasAtivas;
    private List<Reserva> historicoEstadias;
    private List<Servico> menuServicos;

    public ControleHotel() {
        this.quartos = new ArrayList<>();
        this.hospedes = new HashMap<>();
        this.reservasAtivas = new ArrayList<>();
        this.historicoEstadias = new ArrayList<>();
        this.menuServicos = new ArrayList<>();
        inicializarDados();
    }

    private void inicializarDados() {
        // Inicializa 10 Quartos
        for (int i = 101; i <= 104; i++) {
            quartos.add(new QuartoStandard(String.valueOf(i)));
        }
        for (int i = 201; i <= 204; i++) {
            quartos.add(new QuartoLuxo(String.valueOf(i)));
        }
        for (int i = 301; i <= 302; i++) {
            quartos.add(new QuartoSuite(String.valueOf(i)));
        }

        // Inicializa Menu de Serviços
        menuServicos.add(new Servico("Café da Manhã Completo", 35.00));
        menuServicos.add(new Servico("Jantar Executivo", 60.00));
        menuServicos.add(new Servico("Lavanderia (5 peças)", 45.00));
        menuServicos.add(new Servico("Massagem Terapêutica", 120.00));
    }

    // --- Métodos de Cadastro ---

    public void cadastrarHospede(Hospede h) {
        if (!hospedes.containsKey(h.getCpf())) {
            hospedes.put(h.getCpf(), h);
            System.out.println("[INFO] Hóspede " + h.getNome() + " cadastrado com sucesso.");
        } else {
            System.err.println("[ERRO] Hóspede com CPF " + h.getCpf() + " já cadastrado.");
        }
    }

    // --- Métodos de Busca ---

    public Quarto buscarQuarto(String numero) {
        return quartos.stream()
                .filter(q -> q.getNumero().equals(numero))
                .findFirst()
                .orElse(null);
    }

    public Hospede buscarHospede(String cpf) {
        return hospedes.get(cpf);
    }

    public Reserva buscarReservaPorQuarto(String numeroQuarto) {
        return reservasAtivas.stream()
                .filter(r -> r.getQuarto().getNumero().equals(numeroQuarto))
                .findFirst()
                .orElse(null);
    }

    // --- Métodos de Reserva e Check-in ---

    public void realizarCheckin(Hospede h, Quarto q, int diasEstadia) throws ReservaInvalidaException {
        if (q.isOcupado()) {
            throw new ReservaInvalidaException("Quarto " + q.getNumero() + " está ocupado.");
        }
        if (diasEstadia <= 0) {
            throw new ReservaInvalidaException("O número de dias da estadia deve ser maior que zero.");
        }

        if (!hospedes.containsKey(h.getCpf())) {
            cadastrarHospede(h);
        }

        Reserva novaReserva = new Reserva(h, q, diasEstadia);
        reservasAtivas.add(novaReserva);
        PersistenciaDados.salvar(this); // Salva o estado após o check-in
        System.out.println("\n[SUCESSO] Check-in realizado para " + h.getNome() + " no Quarto " + q.getNumero());
    }

    // --- Métodos de Serviços ---

    public void adicionarServicoAQuarto(String numeroQuarto, Servico servico) throws ReservaInvalidaException {
        Reserva r = buscarReservaPorQuarto(numeroQuarto);
        if (r == null) {
            throw new ReservaInvalidaException("Não há reserva ativa no Quarto " + numeroQuarto);
        }

        r.adicionarServico(servico);
        PersistenciaDados.salvar(this);
        System.out.println("[SUCESSO] Serviço '" + servico.getNome() + "' adicionado ao Quarto " + numeroQuarto);
    }

    // --- Métodos de Check-out e Pagamento ---

    public double realizarCheckout(String numeroQuarto, int pontosParaTrocar) throws ReservaInvalidaException {
        Reserva reserva = buscarReservaPorQuarto(numeroQuarto);

        if (reserva == null) {
            throw new ReservaInvalidaException("Nenhuma reserva ativa encontrada no Quarto " + numeroQuarto);
        }

        double valorFinal = reserva.calcularValorTotal();
        Hospede hospede = reserva.getHospede();

        // 1. Troca de Pontos
        if (pontosParaTrocar > 0) {
            int descontoMaximo = (int) (valorFinal * 0.5); // Limite de 50%
            int pontosMaximo = descontoMaximo * 10;

            int pontosUteis = Math.min(pontosParaTrocar, hospede.getPontosFidelidade());
            pontosUteis = Math.min(pontosUteis, pontosMaximo);

            if (hospede.trocarPontosPorDesconto(pontosUteis)) {
                double valorDesconto = pontosUteis / 10.0;
                valorFinal -= valorDesconto;
                System.out.printf("[FIDELIDADE] Desconto de R$ %.2f aplicado com %d pontos.\n", valorDesconto, pontosUteis);
            } else {
                System.out.println("[FIDELIDADE] Pontos insuficientes ou valor final muito baixo para troca.");
            }
        }

        // 2. Acúmulo de Pontos pela estadia
        hospede.acumularPontos(valorFinal);

        // 3. Finalização
        reserva.getQuarto().setOcupado(false);
        reservasAtivas.remove(reserva);
        historicoEstadias.add(reserva);

        PersistenciaDados.salvar(this); // Salva o estado final


        System.out.println("[SUCESSO] Check-out do Quarto " + numeroQuarto + " finalizado.");

        return valorFinal;
    }

    // --- Getters de Coleções ---

    public List<Quarto> getQuartosDisponiveis() {
        return quartos.stream()
                .filter(q -> !q.isOcupado())
                .collect(Collectors.toList());
    }

    public List<Reserva> getReservasAtivas() {
        return reservasAtivas;
    }

    public List<Servico> getMenuServicos() {
        return menuServicos;
    }

    public Map<String, Hospede> getHospedes() {
        return hospedes;
    }

    public List<Reserva> getHistoricoEstadias() {
        return historicoEstadias;
    }
}