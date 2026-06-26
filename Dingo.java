import java.util.Random;

/**
 * A simple model of a dingo.
 * Dingoes age, move, eat snakes, and die.
 *
 * @version 01.03.22
 */
public class Dingo extends Animal
{
    // Characteristics shared by all dingoes (class variables).
    
    // The age at which a dingo can start to breed.
    private static final int BREEDING_AGE = 50;
    // The age to which a dingo can live.
    private static final int MAX_AGE = 700;
    // The likelihood of a dingo breeding.
    private static final double BREEDING_PROBABILITY = 0.04;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single snake. In effect, this is the
    // number of steps a dingo can go before it has to eat again.
    private static final int SNAKE_FOOD_VALUE = 100;
    // The food sources dingoes eat, in search order.
    private static final FoodSource[] FOOD_SOURCES = {
        new FoodSource(Snake.class, SNAKE_FOOD_VALUE)
    };
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a dingo. A dingo can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the dingo will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dingo(boolean randomAge, Field field, Location location) {
        super(field, location);
        this.setGender();
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            setFoodLevel(rand.nextInt(SNAKE_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(SNAKE_FOOD_VALUE);
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
        return isActiveBetween(time, 8, 24);
    }

    protected Animal createYoung(Field field, Location location) {
        return new Dingo(false, field, location);
    }

    protected FoodSource[] getFoodSources() {
        return FOOD_SOURCES;
    }

    protected boolean canFindFood() {
        return !getFog() || rand.nextInt(2) == 0;
    }
}
