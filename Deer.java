import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Deer.
 * Deer age, move, eat grass, breed, and die.
 *
 * @version 2022.03.02
 */
public class Deer extends Animal
{
    // Characteristics shared by all deer (class variables).
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 175;
    private static final double BREEDING_PROBABILITY = 0.10;
    private static final int MAX_LITTER_SIZE = 5;
    private static final Random rand = Randomizer.getRandom();
    private static final int FOOD_VALUE = 50;
    private static final int DEFAULT_FOOD_LEVEL = 250;

    // Individual characteristics (instance fields).
    private int foodLevel;

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
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEFAULT_FOOD_LEVEL);
        } else {
            foodLevel = DEFAULT_FOOD_LEVEL;
        }
    }

    @Override protected int getMaxAge()                      { return MAX_AGE; }
    @Override protected int getBreedingAge()                 { return BREEDING_AGE; }
    @Override protected double getBreedingProbability()      { return BREEDING_PROBABILITY; }
    @Override protected int getMaxLitterSize()               { return MAX_LITTER_SIZE; }
    @Override public    int foodValue()                      { return FOOD_VALUE; }

    /**
     * This is what the Deer does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newDeers A list to return newly born deer.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Entity> newDeers, int step, String weather)
    {
        incrementAge();
        incrementHunger();
        updateBurnStatus(weather);
        if (isAlive()) {
            giveBirth(newDeers);

            Location newLocation;
            if (weather.equals("Rainy")) {
                newLocation = null;
            } else {
                newLocation = findFood();
            }

            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                setDead();
            }
        }
    }

    /**
     * Make this deer more hungry. This could result in the deer's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for grass adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if (plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if (grass.isAlive()) {
                    if (grass.getSize() > 12 && foodLevel < 30) {
                        grass.decrementSize();
                        foodLevel += 20;
                    }
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this deer is to give birth at this step.
     * Requires an opposite-gender deer within a 2-step radius.
     * @param newDeers A list to return newly born deer.
     */
    private void giveBirth(List<Entity> newDeers)
    {
        boolean breedingPair = false;
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 2);
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal instanceof Deer) {
                Deer deer = (Deer) animal;
                if (deer.getGender() != getGender()) {
                    breedingPair = true;
                    break;
                }
            }
        }

        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0 && breedingPair; b++) {
            Location loc = free.remove(0);
            newDeers.add(new Deer(false, field, loc));
        }
    }
}
