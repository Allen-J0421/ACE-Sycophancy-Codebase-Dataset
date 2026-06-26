/**
 * A simple model of a cheetah.
 * cheetahs age, move, breed, eat prey, and die.
 *
 * @version 25/02/2022
 */
public class Cheetah extends Predator
{
    public static final double PREY_CATCHING_PROBABILITY = 0.6;
    public static final int BREEDING_AGE = 60; 
    public static final int MAX_AGE = 1000;
    public static final double BREEDING_PROBABILITY = 0.7;
    public static final int MAX_LITTER_SIZE = 3;
    public static final int MAX_FOOD_LEVEL = 40;
    public static final int FOOD_VALUE = 16;
    
    /**
     * Create a cheetah. A cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the cheetah will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Cheetah(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        super(field, location, isInfected, isImmune);

        configurePredator(BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                          MAX_FOOD_LEVEL, FOOD_VALUE, PREY_CATCHING_PROBABILITY);
        initializeAgeAndFood(randomAge, getNewbornFoodLevel());
    }

    protected Animal createOffspring(Location location, boolean isInfected, boolean isImmune)
    {
        return new Cheetah(false, field, location, isInfected, isImmune);
    }
}
