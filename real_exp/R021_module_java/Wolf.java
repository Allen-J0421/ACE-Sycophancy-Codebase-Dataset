import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat mice, breed and die.
 *
 * 
 * @version 2016.02.29 (2)
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).
    
    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 15;     //15
    // The age to which a wolf can live.
    private static final int MAX_AGE = 150;     //120
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.3;    //0.09
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;   //2
    // The food value of a single mouse or frog. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int FOOD_REQUIREMENT_TIME = 9;    //10
    

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE, FOOD_REQUIREMENT_TIME);
        foodTypes = new Class[]{Mouse.class, Frog.class};
        speed = 2;
        maxAge = MAX_AGE;
    }

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species spawn(boolean randomAge, Field field, Location location)
    {
        return new Wolf(randomAge, field, location);
    }
    
    /**
     * This is what the wolf does most of the time: it hunts for
     * mice and frogs. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWolves A list to return newly born wolves.
     */
    public void act(List<Species> newWolves, boolean isDay)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            diseaseInfect();
            giveBirth(newWolves, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);            
            findFoodAndMove(FOOD_REQUIREMENT_TIME);
        }
    }
}
