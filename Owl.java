/**
 * A simple model of a Owl.
 * Owls age, move, eat mice, and die.
 *
 * @version 2022.03.02 
 */
public class Owl extends MouseHunter
{
    private static final AnimalProfile PROFILE = new AnimalProfile(75, 25, 25, 10);
    private static final BreedingProfile BREEDING_PROFILE = new BreedingProfile(5, 0.10, 3);

    /**
     * Create a Owl. A Owl can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Owl(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE, BREEDING_PROFILE);
    }

    /**
     * Create a newborn owl.
     */
    @Override
    protected Animal createYoung(Field field, Location location)
    {
        return new Owl(false, field, location);
    }
}
