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
    // Characteristics shared by all emus (class variables).

    // The age at which an emu can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which an emu can live.
    private static final int MAX_AGE = 600;
    // The likelihood of an emu breeding.
    private static final double BREEDING_PROBABILITY = 0.17;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
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
        super(field, location);
        initialise(randomAge);
    }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected int getInitialFoodLevel() { return GRASS_FOOD_VALUE; }

    /**
     * Emus are active overnight, sleeping through the middle of the day.
     */
    protected boolean isActive(int time) { return (time <= 9) || (time >= 21); }

    protected Animal createYoung(Field field, Location location) {
        return new Emu(false, field, location);
    }

    /**
     * Look for grass adjacent to the current location.
     * Only the first grass is eaten.
     * If acacia is adjacent, it is 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
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
