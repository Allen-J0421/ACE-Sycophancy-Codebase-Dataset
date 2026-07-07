import java.util.Iterator;
import java.util.List;

/**
 * A simple model of an ant.
 * Ants age, move, eat acacia and grass, breed, and die.
 *
 * @version 01.03.22
 */
public class Ant extends Animal
{
    // The food value of a single acacia. In effect, this is the
    // number of steps an ant can go before it has to eat again.
    private static final int ACACIA_FOOD_VALUE = 60;
    // The food value of a single grass. In effect, this is the
    // number of steps an ant can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 60;

    /**
     * Create an ant. An ant can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the ant will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Ant(boolean randomAge, Field field, Location location) {
        super(field, location, SimulationConfig.ANT_CONFIG);
        initialise(randomAge);
    }

    /**
     * This is what the ant does most of the time: it eats grass
     * and acacia. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newAnts A list to return newly born ants.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newAnts, int time) {
        incrementAge(getMaxAge());
        incrementHunger();

        if(isAlive() && ((time >= 4)&&(time <= 20)))
        {
            if (getDisease()) {
                spreadDisease();
            }
            spawnOffspring(newAnts);
            moveOrDie();
        }
    }

    @Override protected Animal createOffspring(Field field, Location loc) { return new Ant(false, field, loc); }

    /**
     * Look for acacia and grass adjacent to the current location.
     * Only the first grass or acacia is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    @Override
    protected Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Acacia) {
                Acacia acacia = (Acacia) plant;
                if (acacia.isAlive()) {
                    acacia.setDead();
                    setFoodLevel(ACACIA_FOOD_VALUE);
                    return where;
                }
            }
            else if (plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) {
                    grass.setDead();
                    setFoodLevel(GRASS_FOOD_VALUE);
                    return where;
                }
            }
        }
        return null;
    }

}
