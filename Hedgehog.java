/**
 * Hedgehogs eat plants, move at night and are eaten by wolves, bears and
 * badgers.
 *
 * @version 2022.02.24
 */
public final class Hedgehog extends Animal
{
    private static final int BREEDING_AGE = 3;
    private static final int MAX_AGE = 35;
    private static final double BREEDING_PROBABILITY = 0.75;
    private static final int MAX_LITTER_SIZE = 5;

    private static final int PLANT_FOOD_VALUE = 13;
    private static final AnimalProfile PROFILE = profile(
        MAX_AGE, PLANT_FOOD_VALUE, PLANT_FOOD_VALUE / 2, true,
        BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
        food(Plant.class, PLANT_FOOD_VALUE)
    );

    /**
     * Create a hedgehog. A hedgehog can be created as a newborn or with a
     * random age and food level.
     * @param randomAge If true, the hedgehog will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hedgehog(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Hedgehog(false, field, location);
    }
}
