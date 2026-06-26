import java.util.HashMap;
import java.util.Map;

/**
 * A simple model of a Gazelle.
 * Gazelles age, move, eat grass, breed, and die.
 *
 * @version 2022.03.01
 */
public class Gazelle extends Animal
{
    // Characteristics shared by all Gazelles (class variables).

    // The age at which a Gazelle can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Gazelle can live.
    private static final int MAX_AGE = 80;
    // The likelihood of a Gazelle breeding.
    private static final double BREEDING_PROBABILITY = 0.8900995;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 2;
    private static final int GRASS_FOOD_VALUE = 14;
    private static final double MAX_FOOD_LEVEL = 28;
    private static final double GROWTH_RATE = 0.012;

    private static final Map<Class<? extends Actor>, Integer> FOOD = new HashMap<>();
    static {
        FOOD.put(Grass.class, GRASS_FOOD_VALUE);
    }

    /**
     * Create a new Gazelle. A Gazelle may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(GRASS_FOOD_VALUE));
        } else {
            setAge(0);
            setFoodLevel(GRASS_FOOD_VALUE);
        }
        addGrowthLevel(getAge()/75.0);
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
        return new Gazelle(false, field, location);
    }

    protected Map<Class<? extends Actor>, Integer> getFood() { return FOOD; }

    protected double getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
}
