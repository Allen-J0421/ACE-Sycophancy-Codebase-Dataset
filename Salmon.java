/**
 * A simple model of a Salmon.
 *
 * Salmons age, move, eat seaweed, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Salmon extends ForagingAnimal
{
    private static final int BREEDING_AGE = 4;
    private static final int MAX_AGE = 50;
    private static final double BREEDING_PROBABILITY = 0.3;
    private static final int MAX_LITTER_SIZE = 15;
    private static final int SEAWEED_FOOD_VALUE = 13;

    /**
     * Create a new salmon.
     *
     * @param randomAge If true, the salmon will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Salmon(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        initializeLife(randomAge, MAX_AGE, SEAWEED_FOOD_VALUE);
    }

    /**
     * Decide if two salmons have different sex.
     */
    public boolean encounterWithDiffSex()
    {
        return hasOppositeSexMate(Salmon.class, 2);
    }

    /**
     * Look for seaweed adjacent to the current location.
     */
    public Location search(Disease disease, int step)
    {
        return searchForPrey(disease, step, 1, SEAWEED_FOOD_VALUE, Seaweed.class);
    }

    @Override
    protected Creature createOffspring(Field field, Location location)
    {
        return new Salmon(false, field, location);
    }

    @Override
    protected boolean canBreed()
    {
        return isOldEnoughToBreed(BREEDING_AGE) && encounterWithDiffSex();
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

    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
}
