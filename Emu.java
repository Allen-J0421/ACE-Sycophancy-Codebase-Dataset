import java.util.Iterator;
import java.util.List;

/**
 * A simple model of an emu.
 * Emu age, move, eat grass, and die.
 *
 * @version 01.03.22
 */
public class Emu extends Animal
{
    // The food value of a single grass. In effect, this is the
    // number of steps an emu can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 60;

    /**
     * Create an emu. An emu can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the emu will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Emu(boolean randomAge, Field field, Location location) {
        super(field, location, SimulationConfig.EMU_CONFIG);
        initialise(randomAge);
    }

    /**
     * This is what the emu does most of the time: it eats grass.
     * In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newEmus A list to return newly born emus.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newEmus,int time) {
        incrementAge(getMaxAge());
        incrementHunger();

        if(isAlive() && ((time <= 9)||(time >= 21))) {
            if (getDisease()){
                spreadDisease();
            }
            spawnOffspring(newEmus);
            moveOrDie();
        }
    }

    @Override protected Animal createOffspring(Field field, Location loc) { return new Emu(false, field, loc); }

    /**
     * Look for grass adjacent to the current location.
     * Only the first grass is eaten.
     * If acacia is adjacent, it is 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
    @Override
    protected Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object searchPlant = field.getObjectAt(where);
            if(searchPlant instanceof Grass) {
                Grass grass = (Grass) searchPlant;
                if (grass.isAlive()) {
                    grass.setDead();
                    setFoodLevel(GRASS_FOOD_VALUE);
                    return where;
                }
            }
            else if (searchPlant instanceof Plant) {
                Plant plant = (Plant) searchPlant;
                if(plant.isAlive()) {
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }
}
