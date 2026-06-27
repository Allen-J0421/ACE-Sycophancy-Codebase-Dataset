/**
 * Frogs eat plants, move at night and are eaten by bears, wolves and badgers.
 *
 * @version 2022.02.24
 */
public class Frog extends Animal
{
    private static final int BREEDING_AGE = 3;
    private static final int MAX_AGE = 27;
    private static final double BREEDING_PROBABILITY = 0.75;
    private static final int MAX_LITTER_SIZE = 4;

    private static final int PLANT_FOOD_VALUE = 12;
    private static final AnimalProfile PROFILE = profile(
        MAX_AGE, PLANT_FOOD_VALUE, PLANT_FOOD_VALUE / 2, true,
        BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
        food(Plant.class, PLANT_FOOD_VALUE)
    );

    /**
     * Create a frog. A frog can be created as a newborn or with a random age
     * and food level.
     * @param randomAge If true, the frog will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Frog(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Frog(false, field, location);
    }
}
