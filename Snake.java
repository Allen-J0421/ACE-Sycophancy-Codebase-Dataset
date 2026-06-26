import java.util.Random;

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
    // The food sources snakes eat, in search order.
    private static final FoodSource[] FOOD_SOURCES = {
        new FoodSource(Rat.class, RAT_FOOD_VALUE)
    };
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

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
        this.setGender();
        if(randomAge) {
            setAge( rand.nextInt(MAX_AGE));
            setFoodLevel(rand.nextInt(RAT_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(RAT_FOOD_VALUE);
        }
    }

    protected int getMaxAge() {
        return MAX_AGE;
    }

    protected int getBreedingAge() {
        return BREEDING_AGE;
    }

    protected double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    protected int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    protected boolean isActive(int time) {
        return isActiveBetween(time, 5, 23);
    }

    protected Animal createYoung(Field field, Location location) {
        return new Snake(false, field, location);
    }

    protected FoodSource[] getFoodSources() {
        return FOOD_SOURCES;
    }

}
