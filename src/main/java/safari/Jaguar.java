package safari;

/**
 * A simple model of a Jaguar.
 *
 * @version 2022.03.01
 */
public class Jaguar extends Predator
{
    /**
     * Create a new Jaguar.
     *
     * @param randomAge If true, the Jaguar will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Jaguar(boolean randomAge, Field field, Location location)
    {
        super(SpeciesType.JAGUAR, randomAge, field, location);
    }
}
