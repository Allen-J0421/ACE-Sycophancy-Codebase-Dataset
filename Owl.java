import java.util.List;

/**
 * A simple model of a Owl.
 * Owls age, move, eat mice, and die.
 *
 * @version 2022.03.02 
 */
public class Owl extends Animal
{
    // Characteristics shared by all Owls (class variables).
    
    // The age at which a Owl can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a owl can live.
    private static final int MAX_AGE = 75;
    // The likelihood of a owl breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single owl. In effect, this is the
    // number of steps a owl can go before it has to eat again.
    private static final int DEFAULT_FOOD_LEVEL = 25;
    // The food value of a single owl. 
    private static final int FOOD_VALUE = 10;

    /**
     * Create a Owl. A Owl can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Owl(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        initializeAge(randomAge, MAX_AGE);
        initializeFoodLevel(randomAge, DEFAULT_FOOD_LEVEL, DEFAULT_FOOD_LEVEL);
    }
    
    /**
     * This is what the Owl does most of the time: it hunts for
     * mouses. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newOwls A list to return newly born owls.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Animal> newOwls, int step, Weather weather)
    {
        incrementAge(MAX_AGE);
        decrementFoodLevel();
        updateBurnStatus(weather);
        if(isAlive()) {
            giveBirth(newOwls);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = freeAdjacentLocation();
            }
            moveToOrDie(newLocation);
        }
    }

    /**
     * Look for mouses adjacent to the current location.
     * Only the first live mouse is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        return findAdjacentLocation(Mouse.class, 1, mouse -> {
            if(mouse.isAlive()) {
                mouse.setDead();
                changeFoodLevel(mouse.foodValue());
                return true;
            }
            return false;
        });
    }
    
    /**
     * Check whether or not this Owl is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newOwls A list to return newly born owls.
     */
    private void giveBirth(List<Animal> newOwls)
    {
        addOffspring(newOwls, breed(), (field, location) -> new Owl(false, field, location));
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        return calculateBirths(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
    }

    /**
     * @return owl's food value.
     */
    public int foodValue()
    {
        return FOOD_VALUE;
    }
}
