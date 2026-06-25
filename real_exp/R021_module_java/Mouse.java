import java.util.List;
import java.util.Random;

/**
 * A simple model of a mouse.
 * Mice age, move, and die.
 *
 * 
 * @version 2016.02.29 (2)
 */
public class Mouse extends Animal
{
    // Characteristics shared by all mice (class variables).

    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 5;  //8
    // The age to which a mouse can live.
    private static final int MAX_AGE = 45;  //20
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.16;    //0.06
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5; 
    // number of steps a mouse can go before it has to eat again.
    private static final int FOOD_REQUIREMENT_TIME = 8; 

    // Individual characteristics (instance fields).

    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE, FOOD_REQUIREMENT_TIME);
        foodTypes = new Class[]{Carpetweed.class, Ryegrass.class};
        maxAge = MAX_AGE;
    }

    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species spawn(boolean randomAge, Field field, Location location)
    {
        return new Mouse(randomAge, field, location);
    }

    /**
     * This is what the mouse does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * Mice only move during the day, and are dormant at night, but will still breed.
     * 
     * @param newMice A list to return newly born rabbits.
     */
    public void act(List<Species> newMice, boolean isDay)
    {
        incrementAge();
        incrementHunger();
        if (isAlive()){
            diseaseInfect();
            giveBirth(newMice, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE); 
            if(isDay) {
                findFoodAndMove(FOOD_REQUIREMENT_TIME);
            }
        }
    }
}
