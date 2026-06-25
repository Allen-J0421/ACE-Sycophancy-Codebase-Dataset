import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a frog.
 * Frogs age, move, breed, eat and die.
 *
 * 
 * @version 2016.02.29 (2)
 */
public class Frog extends Animal
{
    // Characteristics shared by all frogs (class variables).

    // The age at which a frog can start to breed.
    private static final int BREEDING_AGE = 3; 
    // The age to which a frog can live.
    private static final int MAX_AGE = 45; 
    // The likelihood of a frog breeding.
    private static final double BREEDING_PROBABILITY = 0.09;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    //The food value of a single piece of pray. In effect, this is the
    // number of steps a frog can go before it has to eat again.
    private static final int FOOD_REQUIREMENT_TIME = 16;
    
    /**
     * Create a new frog. A frog may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the frog will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Frog(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE, FOOD_REQUIREMENT_TIME);
        foodTypes = new Class[]{Insect.class};
        maxAge = MAX_AGE;
    }
    
    /**
     * Create a new frog. A frog may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the frog will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species spawn(boolean randomAge, Field field, Location location)
    {
        return new Frog(randomAge, field, location);
    }
    
    /**
     * This is what the frog does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newFrogs A list to return newly born frog.
     */
    public void act(List<Species> newFrogs, boolean isDay)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            diseaseInfect();
            giveBirth(newFrogs, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);            
            findFoodAndMove(FOOD_REQUIREMENT_TIME);
        }
    }
    
}
