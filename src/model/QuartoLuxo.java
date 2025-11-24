package model;

/**
 * Subclasse QuartoLuxo: Herda de Quarto.
 */
public class QuartoLuxo extends Quarto {
    private static final long serialVersionUID = 1L;

    public QuartoLuxo(String numero) {
        // Preço base R$ 150,00
        super(numero, 150.00, "Quarto Luxo: Cama queen, vista privilegiada, frigobar completo.");
    }

    /**
     * Polimorfismo: Diária = Preço Base + 50% de acréscimo.
     */
    @Override
    public double calcularDiaria() {
        // Exemplo: 50% de acréscimo
        return getPrecoBase() * 1.5;
    }
}
