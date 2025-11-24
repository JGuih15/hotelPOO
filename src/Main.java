import control.ControleHotel;
import exceptions.ReservaInvalidaException;
import model.Hospede;
import model.Quarto;
import model.Reserva;
import model.Servico;
import utils.PersistenciaDados;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static ControleHotel controle;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Tenta carregar dados persistidos. Se falhar, inicia um novo controle.
        controle = PersistenciaDados.carregar();
        if (controle == null) {
            controle = new ControleHotel();
        }

        int opcao;
        do {
            exibirMenu();
            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consome a linha pendente
                processarOpcao(opcao);
            } catch (InputMismatchException e) {
                System.err.println("\n[ERRO] Entrada inválida. Digite um número correspondente à opção.");
                scanner.nextLine(); // Limpa o buffer
                opcao = 0;
            } catch (Exception e) {
                System.err.println("\n[ERRO] Ocorreu um erro: " + e.getMessage());
                opcao = 0;
            }
        } while (opcao != 7);

        // Garante que o último estado seja salvo ao sair
        PersistenciaDados.salvar(controle);
        System.out.println("Sistema encerrado. Obrigado!");
    }

    private static void exibirMenu() {
        System.out.println("\n--- Sistema Hotel Estadia Confortável ---");
        System.out.println("1. Listar Quartos Disponíveis");
        System.out.println("2. Realizar Check-in (Nova Reserva)");
        System.out.println("3. Solicitar Serviço de Quarto");
        System.out.println("4. Realizar Check-out e Pagamento");
        System.out.println("5. Consultar Reservas Ativas");
        System.out.println("6. Consultar Programa de Fidelidade (Hóspede)");
        System.out.println("7. Sair e Salvar");
        System.out.print("Escolha uma opção: ");
    }

    private static void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                listarQuartosDisponiveis();
                break;
            case 2:
                realizarCheckin();
                break;
            case 3:
                solicitarServico();
                break;
            case 4:
                realizarCheckout();
                break;
            case 5:
                consultarReservasAtivas();
                break;
            case 6:
                consultarProgramaFidelidade();
                break;
            case 7:
                System.out.println("Encerrando...");
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
    }

    private static void listarQuartosDisponiveis() {
        List<Quarto> disponiveis = controle.getQuartosDisponiveis();
        if (disponiveis.isEmpty()) {
            System.out.println("\n[INFO] Todos os quartos estão ocupados.");
            return;
        }
        System.out.println("\n--- Quartos Disponíveis ---");
        disponiveis.forEach(System.out::println);
    }

    private static void realizarCheckin() {
        System.out.print("CPF do Hóspede: ");
        String cpf = scanner.nextLine();

        Hospede hospede = controle.buscarHospede(cpf);
        if (hospede == null) {
            System.out.print("Hóspede não encontrado. Digite o Nome para cadastro: ");
            String nome = scanner.nextLine();
            hospede = new Hospede(cpf, nome);
        }

        listarQuartosDisponiveis();
        System.out.print("Número do Quarto para a reserva: ");
        String numQuarto = scanner.nextLine();
        Quarto quarto = controle.buscarQuarto(numQuarto);

        if (quarto == null) {
            System.err.println("[ERRO] Quarto não encontrado.");
            return;
        }

        try {
            System.out.print("Quantos dias de estadia: ");
            int dias = scanner.nextInt();
            scanner.nextLine(); // Consome a linha

            controle.realizarCheckin(hospede, quarto, dias);
        } catch (InputMismatchException e) {
            System.err.println("[ERRO] Número de dias inválido.");
            scanner.nextLine();
        } catch (ReservaInvalidaException e) {
            System.err.println("[ERRO] " + e.getMessage());
        }
    }

    private static void solicitarServico() {
        if (controle.getReservasAtivas().isEmpty()) {
            System.out.println("[INFO] Não há reservas ativas para adicionar serviços.");
            return;
        }

        System.out.print("Número do Quarto para adicionar o serviço: ");
        String numQuarto = scanner.nextLine();

        try {
            Reserva reserva = controle.buscarReservaPorQuarto(numQuarto);
            if (reserva == null) {
                throw new ReservaInvalidaException("Quarto sem reserva ativa.");
            }

            System.out.println("\n--- Menu de Serviços ---");
            List<Servico> menu = controle.getMenuServicos();
            for (int i = 0; i < menu.size(); i++) {
                System.out.println((i + 1) + ". " + menu.get(i));
            }

            System.out.print("Escolha o número do serviço: ");
            int idxServico = scanner.nextInt();
            scanner.nextLine();

            if (idxServico > 0 && idxServico <= menu.size()) {
                Servico servicoEscolhido = menu.get(idxServico - 1);
                controle.adicionarServicoAQuarto(numQuarto, servicoEscolhido);
            } else {
                System.err.println("[ERRO] Opção de serviço inválida.");
            }
        } catch (InputMismatchException e) {
            System.err.println("[ERRO] Entrada inválida. Digite o número do serviço.");
            scanner.nextLine();
        } catch (ReservaInvalidaException e) {
            System.err.println("[ERRO] " + e.getMessage());
        }
    }

    private static void realizarCheckout() {
        System.out.print("Número do Quarto para Check-out: ");
        String numQuarto = scanner.nextLine();

        try {
            Reserva reserva = controle.buscarReservaPorQuarto(numQuarto);
            if (reserva == null) {
                throw new ReservaInvalidaException("Não há check-out ativo no quarto " + numQuarto);
            }

            // Exibição pré-fatura
            System.out.printf("\n--- Fatura Preliminar do Quarto %s ---\n", numQuarto);
            System.out.printf("Total Diárias (R$ %.2f x %d dias): R$ %.2f\n",
                    reserva.getQuarto().calcularDiaria(), reserva.getDiasEstadia(),
                    reserva.getQuarto().calcularDiaria() * reserva.getDiasEstadia());

            double totalServicosBruto = reserva.getConsumoServicos().stream().mapToDouble(Servico::getPreco).sum();
            System.out.printf("Total Serviços: R$ %.2f\n", totalServicosBruto);
            if (reserva.getHospede().isVip()) {
                System.out.println("Desconto VIP de 10% aplicado nos serviços.");
            }

            double valorTotalAPagar = reserva.calcularValorTotal();
            System.out.printf("VALOR BRUTO FINAL: R$ %.2f\n", valorTotalAPagar);

            // Pergunta sobre pontos
            System.out.printf("\nPrograma de Fidelidade: Hóspede possui %d pontos.\n", reserva.getHospede().getPontosFidelidade());
            System.out.print("Quantos pontos deseja trocar por desconto (10 pontos = R$ 1,00): ");
            int pontos = scanner.nextInt();
            scanner.nextLine();

            double valorFinalComDesconto = controle.realizarCheckout(numQuarto, pontos);

            System.out.printf("\n============================================\n");
            System.out.printf("VALOR FINAL (PAGAMENTO): R$ %.2f\n", valorFinalComDesconto);
            System.out.println("Formas de Pagamento: Pix, Cartão (suporte a pix e cartão) - Implementação simulada.");
            System.out.println("============================================\n");

        } catch (InputMismatchException e) {
            System.err.println("[ERRO] Entrada de pontos inválida.");
            scanner.nextLine();
        } catch (ReservaInvalidaException e) {
            System.err.println("[ERRO] " + e.getMessage());
        }
    }

    private static void consultarReservasAtivas() {
        List<Reserva> ativas = controle.getReservasAtivas();
        if (ativas.isEmpty()) {
            System.out.println("\n[INFO] Nenhuma reserva ativa no momento.");
            return;
        }
        System.out.println("\n--- Reservas Ativas ---");
        ativas.forEach(r -> System.out.println(r.getQuarto().getNumero() + " | Hóspede: " + r.getHospede().getNome() + " | Consumo: R$ " + String.format("%.2f", r.calcularValorTotal())));
    }

    private static void consultarProgramaFidelidade() {
        System.out.print("CPF do Hóspede para consulta de pontos: ");
        String cpf = scanner.nextLine();

        Hospede h = controle.buscarHospede(cpf);

        if (h != null) {
            System.out.println("\n--- Programa de Fidelidade ---");
            System.out.println("Hóspede: " + h.getNome());
            System.out.println("Status: " + (h.isVip() ? "VIP" : "Regular"));
            System.out.println("Pontos Acumulados: " + h.getPontosFidelidade());
            System.out.println("Regra de Troca: 10 pontos = R$ 1,00 de desconto (limitado a 50% da fatura).");
        } else {
            System.out.println("[INFO] Hóspede não encontrado no sistema.");
        }
    }
}