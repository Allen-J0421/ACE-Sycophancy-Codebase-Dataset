import java.util.List;
import java.util.Random;

/**
 * A simple model of a rat.
 * Rats age, move, eat ants, breed, and die.
 *
 * @version 01.03.22
 */
public class Rat extends Animal
{
    // Characteristics shared by all rats (class variables).

    // The age at which a rat can start to breed.
    private static final int BREEDING_AGE = 25;
    // The age to which a rat can live.
    private static final int MAX_AGE = 600;
    // The likelihood of a rat breeding.
    private static final double BREEDING_PROBABILITY = 0.31;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 15;
    // The food value of a single ant. In effect, this is the
    // number of steps a rat can go before it has to eat again.
    private static final int ANT_FOOD_VALUE = 100;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a rat. A rat can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the rat will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rat(boolean randomAge, Field field, Location location) {
        super(field, location, MAX_AGE, BREEDING_AGE, BREEDING_PROBABILITY,
                MAX_LITTER_SIZE, randomAge, rand, ANT_FOOD_VALUE);
    }

    protected boolean isActiveAt(int time) {
        return time >= 0 && time <= 18;
    }

    protected Animal createYoung(Field field, Location location) {
        return new Rat(false, field, location);
    }

    /**
     * Look for ants adjacent to the current location.
     * Only the first live ant is eaten.
     * if there is a plant adjacent, it can be 'trampled'
     * @return where food was found, or null if it wasn't.
     */
    protected Location findFood() {
        List<Location> adjacent = adjacentLocations();
        Location food = eatAdjacentAnimal(adjacent, Ant.class, ANT_FOOD_VALUE);
        if(food == null) {
            food = trampleAdjacentPlant(adjacent);
        }
        return food;
    }
}
