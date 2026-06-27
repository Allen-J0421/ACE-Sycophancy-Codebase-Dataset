import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of an Owl.
 * Owls age, move, eat mice, and die.
 *
 * @version 2022.03.02
 */
public class Owl extends Animal
{
    // Characteristics shared by all owls (class variables).
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 75;
    private static final double BREEDING_PROBABILITY = 0.10;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int DEFAULT_FOOD_LEVEL = 25;
    private static final Random rand = Randomizer.getRandom();
    private static final int FOOD_VALUE = 10;

    // Individual characteristics (instance fields).
    private int foodLevel;

    /**
     * Create an Owl. An Owl can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the owl will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Owl(boolean randomAge, Field<Entity> field, Location location)
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
     * This is what the Owl does most of the time: it hunts for mice.
     * @param newOwls A list to return newly born owls.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Entity> newOwls, int step, String weather)
    {
        incrementAge();
        incrementHunger();
        updateBurnStatus(weather);
        if (isAlive()) {
            giveBirth(newOwls);
            Location newLocation = findFood();
            if (newLocation == null) {
                newLocation = getNavigator().freeAdjacentLocation(getLocation());
            }
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                setDead();
            }
        }
    }

    /**
     * Make this owl more hungry. This could result in the owl's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for mice adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field<Entity> field = getField();
        List<Location> adjacent = getNavigator().adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Entity animal = field.getObjectAt(where);
            if (animal instanceof Mouse) {
                Mouse mouse = (Mouse) animal;
                if (mouse.isAlive()) {
                    mouse.setDead();
                    foodLevel += mouse.foodValue();
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this owl is to give birth at this step.
     * @param newOwls A list to return newly born owls.
     */
    private void giveBirth(List<Entity> newOwls)
    {
        Field<Entity> field = getField();
        List<Location> free = getNavigator().getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newOwls.add(new Owl(false, field, loc));
        }
    }
}
