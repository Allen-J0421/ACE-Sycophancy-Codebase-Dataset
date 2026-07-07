import java.util.List;

/**
 * Strategy interface for reproduction. Each implementation creates offspring
 * of the appropriate concrete type, removing the need for Animal and Predator
 * to each override reproduce().
 *
 * @version 2022.03.01
 */
public interface ReproductionStrategy
{
    /**
     * Create offspring of the parent animal and add them to the species list.
     *
     * @param parent (Animal) The reproducing animal.
     * @param newOfThisKind (List<Species>) List to add the newborns to.
     */
    void reproduce(Animal parent, List<Species> newOfThisKind);
}
