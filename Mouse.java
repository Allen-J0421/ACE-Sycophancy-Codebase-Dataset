import java.util.*;

/**
 * A simple model of a mouse.
 * Mice can age, move, contract diseases, eat plants and die. 
 *
 * @version 2022.03.02
 */
public class Mouse extends Animal
{
    // Characteristics shared by all Mice (class variables).

    // The age at which a Mouse can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Mouse can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a Mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    // The mouse's food level which is increased by eating prey.
    private static final int MAX_FOOD_LEVEL = 9;
    // The food value of a single mouse.
    private static final int FOOD_VALUE = 2;
    // A set of organisms that a mouse consumes
    private static final Set<Class<? extends Organism>> DIET = new HashSet<>(Arrays.asList(Grass.class));

    protected int getBreedingAge() { return BREEDING_AGE; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxAge() { return MAX_AGE; }
    protected int getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
    protected int getFoodValue() { return FOOD_VALUE; }
    protected Set<Class<? extends Organism>> getDiet() { return DIET; }

    
    /**
     * Create a new Mouse. A Mouse may be created with age
     * zero (a new born) or with a random age.
     * @param randomAge If true, the Mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the mouse.
     */
    public Mouse(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field.getRandomProvider(), field, location, randomAge, sex);
        this.isNocturnal = false;
    }


    /**
     * Check whether this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMouses A list to return newly born mice.
     * @param environment The environment that mouse resides in. 
     */
    protected Animal createYoung(Field field, Location location, Gender sex)
    {
        return new Mouse(false, field, location, sex);
    }
}
