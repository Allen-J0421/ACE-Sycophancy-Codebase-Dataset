import java.util.HashMap;

/**
 * A simple model of a Zebra.
 * Zebras age, move, eat grass, breed, and die.
 *
 * @version 2022.03.01
 */
public class Zebra extends Animal
{
    // Characteristics shared by all Zebra (class variables).

    // The age at which a Zebra can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Zebra can live.
    private static final int MAX_AGE = 160;
    // The likelihood of a Zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.27587999058995;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 15;
    private static final int GRASS_FOOD_VALUE = 19;
    private static final double MAX_FOOD_LEVEL = 28;
    private static final double GROWTH_RATE = 0.015;

    // Individual characteristics (instance fields).
    private HashMap<Class<? extends Actor>, Integer> food;

    /**
     * Create a new Zebra. A Zebra may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Zebra will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(GRASS_FOOD_VALUE));
        } else {
            setAge(0);
            setFoodLevel(GRASS_FOOD_VALUE);
        }
        food = new HashMap<>();
        setGrowthLevel(getAge()/67.0);
        addFood();
    }

    protected double getGrowthRate() { return GROWTH_RATE; }

    protected double getFoodProbability(Weather weather) {
        return 1.0;
    }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getMaxTimeUntilBreedingAgain() { return MAX_TIME_UNTIL_BREEDING_AGAIN; }

    protected Animal createOffspring(Field field, Location location) {
        return new Zebra(false, field, location);
    }

    private void addFood() {
        food.put(Grass.class, GRASS_FOOD_VALUE);
    }

    protected HashMap<Class<? extends Actor>, Integer> getFood() { return food; }

    protected double getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
}
