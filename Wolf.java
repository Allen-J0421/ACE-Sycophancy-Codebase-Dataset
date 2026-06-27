/**
 * Wolves eat hedgehogs and frogs. They hunt and move at night.
 *
 * @version 2022.02.24
 */
public class Wolf extends Animal
{
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 500;
    private static final double BREEDING_PROBABILITY = 0.99;
    private static final int MAX_LITTER_SIZE = 8;

    private static final int HEDGEHOG_FOOD_VALUE = 25;
    private static final int FROG_FOOD_VALUE = 25;
    private static final int INITIAL_FOOD_LEVEL = HEDGEHOG_FOOD_VALUE + FROG_FOOD_VALUE;

    private static final AnimalProfile PROFILE = profile(
        MAX_AGE, INITIAL_FOOD_LEVEL, INITIAL_FOOD_LEVEL, true,
        BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
        food(Hedgehog.class, HEDGEHOG_FOOD_VALUE),
        food(Frog.class, FROG_FOOD_VALUE)
    );

    /**
     * Create a wolf. A wolf can be created as a newborn or with a random age
     * and food level.
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Wolf(false, field, location);
    }
}
