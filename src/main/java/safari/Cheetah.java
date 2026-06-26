package safari;

/**
 * A simple model of a Cheetah.
 *
 * @version 2022.03.01
 */
public class Cheetah extends Predator
{
    /**
     * Create a new Cheetah.
     *
     * @param randomAge If true, the Cheetah will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cheetah(boolean randomAge, Field field, Location location)
    {
        super(SpeciesType.CHEETAH, randomAge, field, location);
    }
}
