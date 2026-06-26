/**
 * Creates a new organism instance for births and population setup.
 */
public interface OrganismFactory {

    /**
     * Create a new organism instance.
     *
     * @param randomAge Whether to assign a random age.
     * @param field The field in which the organism resides.
     * @param location The organism's location.
     * @return A new organism.
     */
    Organism create(boolean randomAge, Field field, Location location);
}
