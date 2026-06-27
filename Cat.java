/**
 * A simple model of a Cat.
 * Cats age, move, eat mice, and die.
 *
 * @version 2022.03.02 
 */
public class Cat extends MouseHunter
{
    // Characteristics shared by all Cats (class variables).
    
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 75;
    private static final double BREEDING_PROBABILITY = 0.15;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int DEFAULT_FOOD_LEVEL = 15;
    private static final int FOOD_VALUE = 10;

    /**
     * Create a Cat. A Cat can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cat(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY,
              MAX_LITTER_SIZE, DEFAULT_FOOD_LEVEL, FOOD_VALUE);
    }

    /**
     * Create a newborn cat.
     */
    protected Animal createYoung(Field field, Location location)
    {
        return new Cat(false, field, location);
    }
}
