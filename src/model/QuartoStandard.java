package model;

/**
 * Subclasse QuartoStandard: Herda de Quarto.
 */
public class QuartoStandard extends Quarto {
    private static final long serialVersionUID = 1L;

    public QuartoStandard(String numero) {
        super(numero, 100.00, "Quarto Standard: Cama de casal, banheiro, TV.");
    }

    /**
     * Polimorfismo: Diária = Preço Base.
     */
    @Override
    public double calcularDiaria() {
        return getPrecoBase();
    }
}