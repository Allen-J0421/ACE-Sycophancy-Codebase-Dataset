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
        
        setSpeciesCharacteristics(BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY,
                                  MAX_LITTER_SIZE, MAX_FOOD_LEVEL, FOOD_VALUE);
        initialiseAgeAndFoodLevel(randomAge, () -> initialNewbornFoodLevel(0.25));
    }

    @Override
    protected Animal createOffspring(Location loc, boolean isInfected, boolean isImmune)
    {
        return new Zebra(false, field, loc, isInfected, isImmune);
    }
}
