
import java.util.List;
import java.util.Random;

/**
 * A simple model of a insect.
 * Insects age, move, breed, and die.
 *
 * 
 * @version 2016.02.29 (2)
 */
public class Insect extends Animal
{
    // Characteristics shared by all insects (class variables).

    // The age at which a insect can start to breed.
    private static final int BREEDING_AGE = 0;
    // The age to which a insect can live.
    private static final int MAX_AGE = 8;
    // The likelihood of a insect breeding.
    private static final double BREEDING_PROBABILITY = 0.20;   
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // number of steps a insect can go before it has to eat again.
    private static final int FOOD_REQUIREMENT_TIME = 4; 
    
    // Individual characteristics (instance fields).
    
    /**
     * Create a new insect. A insect may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the insect will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Insect(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE, FOOD_REQUIREMENT_TIME);
        foodTypes = new Class[]{Carpetweed.class, Ryegrass.class};
        maxAge = MAX_AGE;
    }
    
    /**
     * Create a new insect. A insect may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the insect will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species spawn(boolean randomAge, Field field, Location location)
    {
        return new Insect(randomAge, field, location);
    }
    
    /**
     * This is what the insect does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newInsects A list to return newly born insect.
     */
    public void act(List<Species> newInsects, boolean isDay)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            diseaseInfect();
            giveBirth(newInsects, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);            
            findFoodAndMove(FOOD_REQUIREMENT_TIME);
        }
    }
}