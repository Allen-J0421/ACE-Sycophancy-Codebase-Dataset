import java.util.List;

/**
 * A simple model of a Deer.
 * Deers age, move, eat grass, breed, and die.
 *
 * @version 2022.03.02 
 */
public class Deer extends Animal
{
    // Characteristics shared by all Deers (class variables).

    // The age at which a Deer can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Deer can live.
    private static final int MAX_AGE = 175;
    // The likelihood of a Deer breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single dear. In effect, this is the
    // number of steps it can go.
    private static final int FOOD_VALUE = 50;
    // The default food value of a single deer.
    private static final int DEFAULT_FOOD_LEVEL = 250;

    /**
     * Create a new Deer. A Deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        initializeAge(randomAge, MAX_AGE);
        initializeFoodLevel(randomAge, DEFAULT_FOOD_LEVEL, DEFAULT_FOOD_LEVEL);
    }
    
    /**
     * This is what the Deer does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newDeers A list to return newly born Deers.
     * @param step The current step
     * @param weather The current weather.
     */
    public void act(List<Animal> newDeers, int step, Weather weather)
    {
        incrementAge(MAX_AGE);
        decrementFoodLevel();
        updateBurnStatus(weather);
        if(isAlive()) {
            giveBirth(newDeers);  
           
            // Move towards a source of food if found.
            Location newLocation;
            if (weather == Weather.RAINY) {
                newLocation = null;
            }
            else {
                newLocation = findFood();
            }

            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = freeAdjacentLocation();
            }

            moveToOrDie(newLocation);
        }
    }

    /**
     * Check whether or not this Deer is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDeers A list to return newly born Deers.
     */
    private void giveBirth(List<Animal> newDeers)
    {
        if(hasAdjacentMate(Deer.class, 2)) {
            addOffspring(newDeers, breed(), (field, location) -> new Deer(false, field, location));
        }
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
     * Look for grass adjacent to the current location.
     * Only the first live grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        return findAdjacentLocation(Grass.class, 1, grass -> {
            if(grass.isAlive()) {
                if(grass.getSize() > 12 && getFoodLevel() < 30) {
                    grass.decrementSize();
                    changeFoodLevel(20);
                }
                return true;
            }
            return false;
        });
    }


    /**
     * @return deer's food value.
     */
    public int foodValue()
    {
        return FOOD_VALUE;
    }
}
