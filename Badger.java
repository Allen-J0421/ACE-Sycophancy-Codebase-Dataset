import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Badgers are daytime omnivores. They eat hedgehogs, frogs and plants, and are
 * eaten by bears.
 *
 * @version 2022.02.24
 */
public class Badger extends Animal
{
    private static final int BREEDING_AGE = 2;
    private static final int MAX_AGE = 1000;
    private static final double BREEDING_PROBABILITY = 0.75;
    private static final int MAX_LITTER_SIZE = 10;

    private static final int HEDGEHOG_FOOD_VALUE = 23;
    private static final int FROG_FOOD_VALUE = 23;
    private static final int PLANT_FOOD_VALUE = 12;
    private static final int INITIAL_FOOD_LEVEL =
            HEDGEHOG_FOOD_VALUE + FROG_FOOD_VALUE + PLANT_FOOD_VALUE;

    private static final Map<Class<? extends LivingBeing>, Integer> FOOD_VALUES =
            new LinkedHashMap<>();

    static {
        FOOD_VALUES.put(Hedgehog.class, HEDGEHOG_FOOD_VALUE);
        FOOD_VALUES.put(Frog.class, FROG_FOOD_VALUE);
        FOOD_VALUES.put(Plant.class, PLANT_FOOD_VALUE);
    }

    /**
     * Create a badger. A badger can be created as a newborn or with a random
     * age and food level.
     * @param randomAge If true, the badger will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Badger(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE, INITIAL_FOOD_LEVEL, INITIAL_FOOD_LEVEL);
    }

    /**
     * Act for one simulation step.
     * @param newBadgers A list to return newly born badgers.
     */
    public void act(List<LivingBeing> newBadgers)
    {
        live(newBadgers, false, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
             FOOD_VALUES);
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Badger(false, field, location);
    }
}
