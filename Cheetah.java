import java.util.HashMap;

/**
 * A simple model of a Cheetah.
 * Cheetahs age, move, eat zebras and gazelles, breed and die.
 *
 * @version 2022.03.01
 */
public class Cheetah extends Predator
{
    // Characteristics shared by all Cheetahs (class variables).

    // The age at which a Cheetah can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Cheetah can live.
    private static final int MAX_AGE = 1200;
    // The likelihood of a Cheetah breeding.
    private static final double BREEDING_PROBABILITY = 0.4196975694969952;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 11;
    private static final int ZEBRA_FOOD_VALUE = 34;
    private static final int GAZELLE_FOOD_VALUE = 33;
    private static final double MAX_FOOD_LEVEL = 40;
    private static final double GROWTH_RATE = 0.012;

    // Individual characteristics (instance fields).
    private HashMap<Class<? extends Actor>, Integer> food;

    /**
     * Create a Cheetah. A Cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Cheetah will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cheetah(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(ZEBRA_FOOD_VALUE));
        } else {
            setAge(0);
            setFoodLevel(ZEBRA_FOOD_VALUE);
        }
        food = new HashMap<>();
        addGrowthLevel(getAge()/102.0);
        addFood();
    }

    protected double getGrowthRate() { return GROWTH_RATE; }

    protected double getFoodProbability(Weather weather) {
        switch(weather) {
            case RAINY: return 0.8;
            case FOGGY: return 0.7;
            default:    return 0.9;
        }
    }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getMaxTimeUntilBreedingAgain() { return MAX_TIME_UNTIL_BREEDING_AGAIN; }

    protected Animal createOffspring(Field field, Location location) {
        return new Cheetah(false, field, location);
    }

    private void addFood() {
        food.put(Zebra.class, ZEBRA_FOOD_VALUE);
        food.put(Gazelle.class, GAZELLE_FOOD_VALUE);
    }

    protected HashMap<Class<? extends Actor>, Integer> getFood() { return food; }

    protected double getMaxFoodLevel() { return MAX_FOOD_LEVEL; }
}
