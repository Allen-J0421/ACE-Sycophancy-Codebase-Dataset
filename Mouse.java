import java.awt.Color;
import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a mouse.
 * Mice age, move, eat grass, breed, and die.
 *
 * @version 2022.03.02
 */
public class Mouse extends Animal
{
    // Individual characteristics (instance fields).
    // (All species parameters are defined in SpeciesConfig.MOUSE.)
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
        super(SpeciesConfig.MOUSE, field, location);
        if (randomAge) {
            age = rand.nextInt(getMaxAge());
            foodLevel = rand.nextInt(getMaxFoodLevel());
        } else {
            foodLevel = getInitialFoodLevel();
        }
    }

    @Override public Color getDisplayColor() { return Color.YELLOW; }

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
        if (rand.nextDouble() <= SpeciesConfig.MOUSE.infectProbability) {
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
        if (rand.nextDouble() <= SpeciesConfig.MOUSE.deteriorateProbability) {
            infect++;
        } else if (rand.nextDouble() <= SpeciesConfig.MOUSE.recoverProbability) {
            infect--;
        }
    }
}
