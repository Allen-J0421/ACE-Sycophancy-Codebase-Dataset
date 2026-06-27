/**
 * A simple model of a hyena.
 * Hyenas age, move, eat gazelles, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Hyena extends Animal
{
    /**
     * Create a hyena. A hyena can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the hyena will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hyena(boolean randomAge, Field field, Location location)
    {
        super(SpeciesRegistry.get("Hyena"), randomAge, field, location);
    }

    /**
     * Create a new-born hyena.
     */
    protected Animal createOffspring(Field field, Location location)
    {
        return new Hyena(false, field, location);
    }
}
