/**
 * Bears are apex predators. They eat hedgehogs, badgers and frogs, and are
 * active during the day.
 *
 * @version 2022.02.24 (2)
 */
public final class Bear extends Animal
{
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 1500;
    private static final double BREEDING_PROBABILITY = 0.99;
    private static final int MAX_LITTER_SIZE = 7;

    private static final int HEDGEHOG_FOOD_VALUE = 40;
    private static final int BADGER_FOOD_VALUE = 25;
    private static final int FROG_FOOD_VALUE = 40;
    private static final int INITIAL_FOOD_LEVEL =
            HEDGEHOG_FOOD_VALUE + BADGER_FOOD_VALUE + FROG_FOOD_VALUE;

    private static final AnimalProfile PROFILE = profile(
        MAX_AGE, INITIAL_FOOD_LEVEL, INITIAL_FOOD_LEVEL, false,
        BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
        food(Hedgehog.class, HEDGEHOG_FOOD_VALUE),
        food(Badger.class, BADGER_FOOD_VALUE),
        food(Frog.class, FROG_FOOD_VALUE)
    );

    /**
     * Create a bear. A bear can be created as a newborn or with a random age
     * and food level.
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Bear(false, field, location);
    }
}
