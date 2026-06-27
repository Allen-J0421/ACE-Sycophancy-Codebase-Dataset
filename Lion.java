import java.util.List;

/**
 * A simple model of a Lion.
 * Lions age, move, eat deers/cats/owls/mouse, and die.
 *
 * @version 2022.03.02 
 */
public class Lion extends Animal
{
    // Characteristics shared by all Liones (class variables).
    
    // The age at which a Lion can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Lion can live.
    private static final int MAX_AGE = 225;
    // The likelihood of a Lion breeding. 
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single lion.
    private static final int FOOD_VALUE = 20;
    // number of steps a lion can go before it has to eat again.
    private static final int DEFAULT_FOOD_LEVEL = 275;
    // The starting food level for newborn lions.
    private static final int NEWBORN_FOOD_LEVEL = 40;

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        initializeAge(randomAge, MAX_AGE);
        initializeFoodLevel(randomAge, DEFAULT_FOOD_LEVEL, NEWBORN_FOOD_LEVEL);
    }
    
    /**
     * This is what the Lion does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * If the weather is foggy, it stops moving.
     * @param newLiones A list to return newly born Liones.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Animal> newLions, int step, Weather weather)
    {
        incrementAge(MAX_AGE);
        decrementFoodLevel();
        updateBurnStatus(weather);

        if(isAlive()) {
            giveBirth(newLions);            
            // Move towards a source of food if found.
            Location newLocation;
            if (weather == Weather.FOGGY) {
                newLocation = null;
            }
            else {
                newLocation = findFood();
            }
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = freeAdjacentLocation();
            }
            // See if it was possible to move.
            if (!TimeOfDay.fromStep(step).isNight()) {
                moveToOrDie(newLocation);
            } 
        }
    }
    
    /**
     * Look for deer, cat, owl ,mouse adjacent to the current location.
     * Only the first live deer/cat/owl/mouse is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Location preyLocation = eat(Deer.class);
        if(preyLocation != null) {
            return preyLocation;
        }

        preyLocation = eat(Cat.class);
        if(preyLocation != null) {
            return preyLocation;
        }

        preyLocation = eat(Owl.class);
        if(preyLocation != null) {
            return preyLocation;
        }

        return eat(Mouse.class);
    }
    
    /**
     * Check whether or not this Lion is to give birth at this step.
     * New births will be made into free adjacent locations of the 24 nearest locations
     * surrounding the Lion.
     * @param newLions A list to return newly born Lions.
     */
    private void giveBirth(List<Animal> newLions)
    {
        if(hasAdjacentMate(Lion.class, 2)) {
            addOffspring(newLions, breed(), (field, location) -> new Lion(false, field, location));
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
     * Hunt a specific prey species adjacent to this lion.
     */
    private <T extends Animal> Location eat(Class<T> preyClass)
    {
        return findAdjacentLocation(preyClass, 1, prey -> {
            if(prey.isAlive()) {
                prey.setDead();
                changeFoodLevel(prey.foodValue());
                return true;
            }
            return false;
        });
    }

    /**
     * @return lion's food value.
     */
    public int foodValue()
    {
        return FOOD_VALUE;
    }
}
