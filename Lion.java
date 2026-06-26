/**
 * A simple model of a lion.
 * lions age, move, breed, eat prey, and die.
 *
 * @version 25/02/2022
 */
public class Lion extends Predator
{
    public static final double PREY_CATCHING_PROBABILITY = 0.80;
    public static final int BREEDING_AGE = 80; 
    public static final int MAX_AGE = 1400;
    public static final double BREEDING_PROBABILITY = 0.65;
    public static final int MAX_LITTER_SIZE = 2;
    public static final int MAX_FOOD_LEVEL = 50;
    public static final int FOOD_VALUE = 35;
    
    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Lion(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        super(randomAge, field, location, isInfected, isImmune,
              PREY_CATCHING_PROBABILITY, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY,
              MAX_LITTER_SIZE, MAX_FOOD_LEVEL, FOOD_VALUE);
    }

    /**
     * @Override
     *
     * Create a new-born lion.
     */
    protected Animal createOffspring(Field field, Location location, boolean isInfected, boolean isImmune)
    {
        return new Lion(false, field, location, isInfected, isImmune);
    }
}
