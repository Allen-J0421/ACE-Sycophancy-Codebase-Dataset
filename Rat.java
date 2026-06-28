import java.util.Iterator;
import java.util.List;

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

    /**
     * Create a rat. A rat can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the rat will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rat(boolean randomAge, Field field, Location location) {
        super(field, location);
        initialise(randomAge);
    }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected int getInitialFoodLevel() { return ANT_FOOD_VALUE; }

    /**
     * Rats are active in the first part of the day.
     */
    protected boolean isActive(int time) { return (time >= 0) && (time <= 18); }

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
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Ant) {
                Ant ant = (Ant) animal;
                if(ant.isAlive()) {
                    ant.setDead();
                    setFoodLevel(ANT_FOOD_VALUE);
                    return where;
                }
            }
            else if (animal instanceof Plant) {
                Plant plant = (Plant) animal;
                if(plant.isAlive()) {
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }
}
