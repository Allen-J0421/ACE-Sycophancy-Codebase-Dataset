import java.util.HashMap;

/**
 * A simple model of a Jaguar.
 * Jaguars age, move, eat gazelle, and die.
 *
 * @version 2022.03.01
 */
public class Jaguar extends Predator
{
    // Characteristics shared by all Jaguars (class variables).

    // The age at which a Jaguar can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Jaguar can live.
    private static final int MAX_AGE = 1000;
    // The likelihood of a Jaguar breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 6;
    private static final int GAZELLE_FOOD_VALUE = 35;
    private static final double MAX_FOOD_LEVEL = 39;
    private static final double GROWTH_RATE = 0.013;

    // Individual characteristics (instance fields).
    private HashMap<Class<? extends Actor>, Integer> food;

    /**
     * Create a Jaguar. A Jaguar can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Jaguar will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Jaguar(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(GAZELLE_FOOD_VALUE));
        } else {
            setAge(0);
            setFoodLevel(GAZELLE_FOOD_VALUE);
        }
        food = new HashMap<>();
        setGrowthLevel(getAge()/89.0);
        addFood();
    }

    protected double getGrowthRate() { return GROWTH_RATE; }

    protected double getFoodProbability(Weather weather) {
        switch(weather) {
            case RAINY: return 0.7;
            case FOGGY: return 0.4;
            default:    return 1.0;
        }
    }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getMaxTimeUntilBreedingAgain() { return MAX_TIME_UNTIL_BREEDING_AGAIN; }

    protected Animal createOffspring(Field field, Location location) {
        return new Jaguar(false, field, location);
    }

    private void addFood() {
        food.put(Gazelle.class, GAZELLE_FOOD_VALUE);
    }

    protected HashMap<Class<? extends Actor>, Integer> getFood() { return food; }

    protected double getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
}
