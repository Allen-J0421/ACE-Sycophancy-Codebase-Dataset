/**
 * A simple model of a Lemur.
 * Lemurs age, move, breed, eat, and die.
 *
 * @version 25/02/2022
 */
public class Lemur extends Prey
{
    public static final int BREEDING_AGE = 15; 
    public static final int MAX_AGE = 60;
    public static final double BREEDING_PROBABILITY = 0.4;
    public static final int MAX_LITTER_SIZE = 5;
    public static final int MAX_FOOD_LEVEL = 7;
    public static final int FOOD_VALUE = 10;
    // Fraction of MAX_FOOD_LEVEL a new-born starts with.
    public static final double NEWBORN_FOOD_FRACTION = 0.5;

    /**
     * Create a new Lemur. A Lemur may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the lemur will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Lemur(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        super(randomAge, field, location, isInfected, isImmune,
              BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
              MAX_FOOD_LEVEL, FOOD_VALUE, NEWBORN_FOOD_FRACTION);
    }

    /**
     * @Override
     *
     * Create a new-born lemur.
     */
    protected Animal createOffspring(Field field, Location location, boolean isInfected, boolean isImmune)
    {
        return new Lemur(false, field, location, isInfected, isImmune);
    }
}
