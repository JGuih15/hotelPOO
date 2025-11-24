package model;

/**
 * Subclasse QuartoSuite: Herda de Quarto.
 */
public class QuartoSuite extends Quarto {
    private static final long serialVersionUID = 1L;

    public QuartoSuite(String numero) {
        // Preço base R$ 250,00
        super(numero, 250.00, "Quarto Suíte: Sala de estar separada, hidromassagem, serviços exclusivos.");
    }

    /**
     * Polimorfismo: Diária = Preço Base + 100% de acréscimo.
     */
    @Override
    public double calcularDiaria() {
        // Exemplo: 100% de acréscimo
        return getPrecoBase() * 2.0;
    }
}
