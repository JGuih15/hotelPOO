package exceptions;

/**
 * Exceção personalizada para erros de regras de negócio de reserva.
 */
public class ReservaInvalidaException extends Exception {
    public ReservaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
