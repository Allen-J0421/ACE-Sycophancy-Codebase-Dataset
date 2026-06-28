/**
 ** A simple model of a shark.
 *
 * Sharks age, move, eat cod or salmon, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Shark extends Predator
{
    // Characteristics shared by all sharkes (class variables).

    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a shark can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value gained from eating a single prey. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int FOOD_VALUE = 8;

    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(field, location, randomAge);
    }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getMaxFoodValue() { return FOOD_VALUE; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected Animal createChild(Field field, Location location)
    {
        return new Shark(false, field, location);
    }

}
