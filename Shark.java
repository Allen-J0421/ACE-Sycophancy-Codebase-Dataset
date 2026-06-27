import java.util.List;

/**
 * A simple model of a shark.
 *
 * Sharks age, move, eat cod or salmon, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Shark extends Animal
{
    private static final int BREEDING_AGE = 6;
    private static final int MAX_AGE = 150;
    private static final double BREEDING_PROBABILITY = 0.4;
    private static final int MAX_LITTER_SIZE = 8;
    private static final int COD_FOOD_VALUE = 8;

    /**
     * Create a shark.
     *
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        initializeLife(randomAge, MAX_AGE, COD_FOOD_VALUE);
    }

    /**
     * This is what the shark does most of the time.
     */
    public double act(List<Creature> newSharks, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {
        return standardAct(newSharks, atDayTime, oxygenLevel, disease, step, MAX_AGE);
    }

    /**
     * Look for Cod and Salmon adjacent to the current location.
     */
    public Location search(Disease disease, int step)
    {
        return searchForPrey(disease, step, 1, COD_FOOD_VALUE, Cod.class, Salmon.class);
    }

    /**
     * Decide whether two sharks have different sex.
     */
    public boolean encounterWithDiffSex()
    {
        return hasOppositeSexMate(Cod.class, 2);
    }

    @Override
    protected Creature createOffspring(Field field, Location location)
    {
        return new Shark(false, field, location);
    }

    @Override
    protected boolean canBreed()
    {
        return isOldEnoughToBreed(BREEDING_AGE);
    }

    @Override
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
}
