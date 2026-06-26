import java.util.List;
import java.util.Random;

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
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create an emu. An emu can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the emu will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Emu(boolean randomAge, Field field, Location location) {
        super(field, location, MAX_AGE, BREEDING_AGE, BREEDING_PROBABILITY,
                MAX_LITTER_SIZE, randomAge, rand, GRASS_FOOD_VALUE);
    }

    protected boolean isActiveAt(int time) {
        return time <= 9 || time >= 21;
    }

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
        List<Location> adjacent = adjacentLocations();
        Location food = eatAdjacentPlant(adjacent, Grass.class, GRASS_FOOD_VALUE);
        if(food == null) {
            food = trampleAdjacentPlant(adjacent);
        }
        return food;
    }
}
