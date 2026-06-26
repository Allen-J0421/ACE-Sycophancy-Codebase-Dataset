import java.util.HashMap;
import java.util.Map;

/**
 * A simple model of a Lion.
 * Lions age, move, eat gazelle & cheetahs, breed and die.
 *
 * @version 2022.03.01
 */
public class Lion extends Predator
{
    // Characteristics shared by all Lions (class variables).

    // The age at which a Lion can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Lion can live.
    private static final int MAX_AGE = 1500;
    // The likelihood of a Lion breeding.
    private static final double BREEDING_PROBABILITY = 0.40752995;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 20;
    private static final int PREY_GAZELLE_FOOD_VALUE = 24;
    private static final int PREY_CHEETAH_FOOD_VALUE = 25;
    private static final double MAX_FOOD_LEVEL = 50;
    private static final double GROWTH_RATE = 0.01;

    private static final Map<Class<? extends Actor>, Integer> FOOD = new HashMap<>();
    static {
        FOOD.put(Gazelle.class, PREY_GAZELLE_FOOD_VALUE);
        FOOD.put(Cheetah.class, PREY_CHEETAH_FOOD_VALUE);
    }

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(PREY_CHEETAH_FOOD_VALUE));
        } else {
            setAge(0);
            setFoodLevel(PREY_CHEETAH_FOOD_VALUE);
        }
        addGrowthLevel(getAge()/100.0);
    }

    protected double getGrowthRate() { return GROWTH_RATE; }

    protected double getFoodProbability(Weather weather) {
        switch(weather) {
            case RAINY: return 0.9;
            case FOGGY: return 0.8;
            default:    return 1.0;
        }
    }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getMaxTimeUntilBreedingAgain() { return MAX_TIME_UNTIL_BREEDING_AGAIN; }

    protected Animal createOffspring(Field field, Location location) {
        return new Lion(false, field, location);
    }

    protected Map<Class<? extends Actor>, Integer> getFood() { return FOOD; }

    protected double getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
}
