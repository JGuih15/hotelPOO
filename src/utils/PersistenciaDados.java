package utils;

import control.ControleHotel;
import java.io.*;

/**
 * Utilitário para salvar e carregar o estado do sistema via Serialização.
 */
public class PersistenciaDados {
    private static final String NOME_ARQUIVO = "dados_hotel.dat";

    /**
     * Salva o objeto ControleHotel (todo o estado) no disco.
     */
    public static void salvar(ControleHotel controle) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(NOME_ARQUIVO))) {
            oos.writeObject(controle);
            System.out.println("\n[INFO] Dados do hotel salvos com sucesso.");
        } catch (IOException e) {
            System.err.println("\n[ERRO] Falha ao salvar dados: " + e.getMessage());
        }
    }

    /**
     * Carrega o objeto ControleHotel do disco.
     */
    public static ControleHotel carregar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NOME_ARQUIVO))) {
            ControleHotel controle = (ControleHotel) ois.readObject();
            System.out.println("[INFO] Dados do hotel carregados com sucesso.");
            return controle;
        } catch (FileNotFoundException e) {
            System.out.println("[INFO] Arquivo de dados não encontrado. Iniciando novo sistema.");
            return null; // Retorna null se for a primeira execução
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[ERRO] Falha ao carregar dados. Iniciando novo sistema: " + e.getMessage());
            return null;
        }
    }
}