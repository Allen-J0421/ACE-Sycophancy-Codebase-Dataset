import java.util.Iterator;
import java.util.List;

/**
 * A simple model of a snake.
 * Snakes age, move, eat rats, breed, and die.
 *
 * @version 01.03.22
 */
public class Snake extends Animal
{
// Characteristics shared by all snakes (class variables).

    // The age at which a snake can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which a snake can live.
    private static final int MAX_AGE = 700;
    // The likelihood of a snake breeding.
    private static final double BREEDING_PROBABILITY = 0.33;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 11;
    // The food value of a single rat. In effect, this is the
    // number of steps a snake can go before it has to eat again.
    private static final int RAT_FOOD_VALUE = 100;

    /**
     * Create a snake. A snake can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge, Field field, Location location) {
        super(field, location);
        initialise(randomAge);
    }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected int getInitialFoodLevel() { return RAT_FOOD_VALUE; }

    /**
     * Snakes are active from early morning until late at night.
     */
    protected boolean isActive(int time) { return (time >= 5) && (time <= 23); }

    protected Animal createYoung(Field field, Location location) {
        return new Snake(false, field, location);
    }

    /**
     * Look for rats adjacent to the current location.
     * Only the first live ant is eaten.
     * If it is a plant, it can be 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rat) {
                Rat rat = (Rat) animal;
                if(rat.isAlive()) {
                    rat.setDead();
                    setFoodLevel(RAT_FOOD_VALUE);
                    return where;
                }
            }
            else if (animal instanceof Plant){
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
