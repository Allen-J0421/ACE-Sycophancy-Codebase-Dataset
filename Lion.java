import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Lion.
 * Lions age, move, eat deer/cats/owls/mice, and die.
 *
 * @version 2022.03.02
 */
public class Lion extends Animal
{
    // Characteristics shared by all lions (class variables).
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 225;
    private static final double BREEDING_PROBABILITY = 0.10;
    private static final int MAX_LITTER_SIZE = 5;
    private static final Random rand = Randomizer.getRandom();
    private static final int FOOD_VALUE = 20;
    private static final int DEFAULT_FOOD_LEVEL = 275;

    // Individual characteristics (instance fields).
    private int foodLevel;

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field<Entity> field, Location location)
    {
        super(field, location);
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEFAULT_FOOD_LEVEL);
        } else {
            foodLevel = 40;
        }
    }

    @Override protected int getMaxAge()                      { return MAX_AGE; }
    @Override protected int getBreedingAge()                 { return BREEDING_AGE; }
    @Override protected double getBreedingProbability()      { return BREEDING_PROBABILITY; }
    @Override protected int getMaxLitterSize()               { return MAX_LITTER_SIZE; }
    @Override public    int foodValue()                      { return FOOD_VALUE; }

    /**
     * This is what the Lion does most of the time: it hunts for prey.
     * If the weather is foggy, it stops moving.
     * @param newLions A list to return newly born lions.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Entity> newLions, int step, String weather)
    {
        incrementAge();
        incrementHunger();
        updateBurnStatus(weather);

        if (isAlive()) {
            giveBirth(newLions);
            Location newLocation;
            if (weather.equals("Foggy")) {
                newLocation = null;
            } else {
                newLocation = findFood();
            }
            if (newLocation == null) {
                newLocation = getNavigator().freeAdjacentLocation(getLocation());
            }
            if (step % 4 != 0) {
                if (newLocation != null) {
                    setLocation(newLocation);
                } else {
                    setDead();
                }
            }
        }
    }

    /**
     * Make this lion more hungry. This could result in the lion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for deer, cat, owl, or mouse adjacent to the current location.
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
            if (animal instanceof Deer) {
                Deer deer = (Deer) animal;
                if (deer.isAlive()) {
                    deer.setDead();
                    foodLevel += deer.foodValue();
                    return where;
                }
            } else if (animal instanceof Cat) {
                Cat cat = (Cat) animal;
                if (cat.isAlive()) {
                    cat.setDead();
                    foodLevel += cat.foodValue();
                    return where;
                }
            } else if (animal instanceof Owl) {
                Owl owl = (Owl) animal;
                if (owl.isAlive()) {
                    owl.setDead();
                    foodLevel += owl.foodValue();
                    return where;
                }
            } else if (animal instanceof Mouse) {
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
     * Check whether or not this lion is to give birth at this step.
     * Requires an opposite-gender lion within a 2-step radius.
     * @param newLions A list to return newly born lions.
     */
    private void giveBirth(List<Entity> newLions)
    {
        boolean breedingPair = false;
        Field<Entity> field = getField();
        List<Location> adjacent = getNavigator().adjacentLocations(getLocation(), 2);
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Entity animal = field.getObjectAt(where);
            if (animal instanceof Lion) {
                Lion lion = (Lion) animal;
                if (lion.getGender() != getGender()) {
                    breedingPair = true;
                    break;
                }
            }
        }

        List<Location> free = getNavigator().getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0 && breedingPair; b++) {
            Location loc = free.remove(0);
            newLions.add(new Lion(false, field, loc));
        }
    }
}
