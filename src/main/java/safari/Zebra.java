package safari;

/**
 * A simple model of a Zebra.
 *
 * @version 2022.03.01
 */
public class Zebra extends Animal
{
    /**
     * Create a new Zebra.
     *
     * @param randomAge If true, the Zebra will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Field field, Location location)
    {
        super(SpeciesType.ZEBRA, randomAge, field, location);
    }
}
