/**
 * A simple model of a Owl.
 * Owls age, move, eat mice, and die.
 *
 * @version 2022.03.02 
 */
public class Owl extends MouseHunter
{
    // Characteristics shared by all Owls (class variables).
    
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 75;
    private static final double BREEDING_PROBABILITY = 0.10;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int DEFAULT_FOOD_LEVEL = 25;
    private static final int FOOD_VALUE = 10;

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
        super(randomAge, field, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY,
              MAX_LITTER_SIZE, DEFAULT_FOOD_LEVEL, FOOD_VALUE);
    }

    /**
     * Create a newborn owl.
     */
    protected Animal createYoung(Field field, Location location)
    {
        return new Owl(false, field, location);
    }
}
