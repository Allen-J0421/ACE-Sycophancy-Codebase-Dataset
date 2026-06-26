/**
 * A simple model of a Zebra.
 * Zebras age, move, breed, eat, and die.
 *
 * @version 25/02/2022
 */
public class Zebra extends Prey
{
    public static final int BREEDING_AGE = 25; 
    public static final int MAX_AGE = 200;
    public static final double BREEDING_PROBABILITY = 0.45;
    public static final int MAX_LITTER_SIZE = 3;
    public static final int MAX_FOOD_LEVEL = 25;
    public static final int FOOD_VALUE = 13;
    
    /**
     * Create a new Zebra. A Zebra may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Zebra will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param isInfected Whether or not the animal will be infected.
     * @param isImmune Whether or not the animal will be immune.
     */
    public Zebra(boolean randomAge, Field field, Location location, boolean isInfected, boolean isImmune)
    {
        super(field, location, isInfected, isImmune);
        
        breedingAge = BREEDING_AGE; 
        maxAge = MAX_AGE;
        breedingProbability = BREEDING_PROBABILITY;
        maxLitterSize = MAX_LITTER_SIZE;
        maxFoodLevel = MAX_FOOD_LEVEL;
        foodValue = FOOD_VALUE;
        
        if (randomAge) 
        {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MAX_FOOD_LEVEL);
        }
        else
        {
            age = 0;
            foodLevel = (int) (0.25 * MAX_FOOD_LEVEL);
        }
    }

    /**
     * @Override
     *
     * Create a new-born zebra.
     */
    protected Animal createOffspring(Field field, Location location, boolean isInfected, boolean isImmune)
    {
        return new Zebra(false, field, location, isInfected, isImmune);
    }
}
