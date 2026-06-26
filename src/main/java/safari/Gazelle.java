package safari;

/**
 * A simple model of a Gazelle.
 *
 * @version 2022.03.01
 */
public class Gazelle extends Animal
{
    /**
     * Create a new Gazelle.
     *
     * @param randomAge If true, the Gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(SpeciesType.GAZELLE, randomAge, field, location);
    }
}
