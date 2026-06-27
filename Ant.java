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
    // Characteristics shared by all ants (class variables).

    // The age at which an ant can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which an ant can live.
    private static final int MAX_AGE = 400;
    // The likelihood of an ant breeding.
    private static final double BREEDING_PROBABILITY = 0.32;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 14;
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
        super(randomAge, field, location, MAX_AGE, ACACIA_FOOD_VALUE);
    }

    protected int getMaxAge() { return MAX_AGE; }
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected boolean isActiveAt(int time) { return (time >= 4) && (time <= 20); }

    protected Animal createOffspring(Field field, Location location) {
        return new Ant(false, field, location);
    }

    /**
     * Look for acacia and grass adjacent to the current location.
     * Only the first grass or acacia is eaten.
     * @return Where food was found, or null if it wasn't.
     */
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
