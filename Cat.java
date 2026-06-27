import java.util.List;
import java.util.Random;

/**
 * A simple model of a Cat.
 * Cats age, move, eat mice, and die.
 *
 * @version 2022.03.02 
 */
public class Cat extends Animal
{
    // Characteristics shared by all Cats (class variables).
    
    // The age at which a Cat can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a cat can live.
    private static final int MAX_AGE = 75;
    // The likelihood of a cat breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single cat. In effect, this is the
    // number of steps a cat can go.
    private static final int DEFAULT_FOOD_LEVEL = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single cat.,
    private static final int FOOD_VALUE = 10;
    
    // Individual characteristics (instance fields).
    // The Cat's age.
    private int age;
    // The Cat's food level, which is increased by eating mouse.
    private int foodLevel;

    /**
     * Create a Cat. A Cat can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cat(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEFAULT_FOOD_LEVEL);
        }
        else {
            age = 0;
            foodLevel = DEFAULT_FOOD_LEVEL;
        }
    }
    
    /**
     * This is what the Cat does most of the time: it hunts for
     * mouses. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newCats A list to return newly born cats.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Animal> newCats, int step, Weather weather)
    {
        incrementAge();
        incrementHunger();
        updateBurnStatus(weather);
        if(isAlive()) {
            giveBirth(newCats);            
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
     * Increase the age. This could result in the Cat's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this cat more hungry. This could result in the Cat's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for mouses adjacent to the current location.
     * Only the first live mice is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        return findAdjacentLocation(Mouse.class, 1, mouse -> {
            if(mouse.isAlive()) {
                mouse.setDead();
                foodLevel = foodLevel + mouse.foodValue();
                return true;
            }
            return false;
        });
    }
    
    /**
     * Check whether or not this Cat is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCats A list to return newly born cats.
     */
    private void giveBirth(List<Animal> newCats)
    {
        addOffspring(newCats, breed(), (field, location) -> new Cat(false, field, location));
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        return calculateBirths(age, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
    }

    /**
     * @return cat's food value.
     */
    public int foodValue()
    {
        return FOOD_VALUE;
    }
}
