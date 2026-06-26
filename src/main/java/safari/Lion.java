package safari;

/**
 * A simple model of a Lion.
 *
 * @version 2022.03.01
 */
public class Lion extends Predator
{
    /**
     * Create a new Lion.
     *
     * @param randomAge If true, the Lion will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(SpeciesType.LION, randomAge, field, location);
    }
}
