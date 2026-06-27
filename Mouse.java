import java.awt.Color;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a mouse.
 * Mice age, move, eat grass, breed, and die.
 *
 * @version 2022.03.02
 */
public class Mouse extends Animal
{
    // Characteristics shared by all mice (class variables).
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 125;
    private static final double BREEDING_PROBABILITY = 0.25;
    private static final double INFECT_PROBABILITY = 0.01;
    private static final int MAX_LITTER_SIZE = 10;
    private static final int DEFAULT_FOOD_LEVEL = 5;
    private static final Random rand = Randomizer.getRandom();
    private static final int FOOD_VALUE = 5;
    private static final double RECOVER_PROBABILITY = 0.3;
    private static final double DETERIORATE_PROBABILITY = 0.15;

    // Individual characteristics (instance fields).
    private int foodLevel;
    // Infection level: 0 = healthy, 1-3 = infected (dies at 3).
    private int infect;

    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field<Entity> field, Location location)
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
    @Override public    Color getDisplayColor()              { return Color.YELLOW; }

    /**
     * This is what the mouse does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newMice A list to return newly born mice.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Entity> newMice, int step, String weather)
    {
        incrementAge();
        updateBurnStatus(weather);
        checkInfectLevel();
        if (isAlive()) {
            if (getInfected() != 0) {
                spreadDisease();
                diseaseRecover();
            } else {
                giveBirth(newMice);
                tryInfect();
            }

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
     * Check whether or not this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMice A list to return newly born mice.
     */
    private void giveBirth(List<Entity> newMice)
    {
        Field<Entity> field = getField();
        List<Location> free = getNavigator().getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newMice.add(new Mouse(false, field, loc));
        }
    }

    /**
     * Look for grass adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field<Entity> field = getField();
        List<Location> adjacent = getNavigator().adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Entity plant = field.getObjectAt(where);
            if (plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if (grass.isAlive()) {
                    foodLevel += 2;
                }
                return where;
            }
        }
        return null;
    }

    /**
     * @return the mouse's infection level (0 = healthy, 1-3 = infected).
     */
    private int getInfected()
    {
        return infect;
    }

    /**
     * Randomly infect this mouse with disease.
     */
    private void tryInfect()
    {
        if (rand.nextDouble() <= INFECT_PROBABILITY) {
            infect = 1;
        }
    }

    /**
     * Die from disease when the infection level reaches 3.
     */
    private void checkInfectLevel()
    {
        if (infect == 3) {
            setDead();
        }
    }

    /**
     * Spread disease to adjacent healthy mice.
     */
    private void spreadDisease()
    {
        Field<Entity> field = getField();
        List<Location> adjacent = getNavigator().adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Entity animal = field.getObjectAt(where);
            if (animal instanceof Mouse) {
                Mouse mouse = (Mouse) animal;
                if (mouse.getInfected() == 0) {
                    mouse.tryInfect();
                }
            }
        }
    }

    /**
     * Randomly recover from or deteriorate with disease.
     */
    private void diseaseRecover()
    {
        if (rand.nextDouble() <= DETERIORATE_PROBABILITY) {
            infect++;
        } else if (rand.nextDouble() <= RECOVER_PROBABILITY) {
            infect--;
        }
    }
}
