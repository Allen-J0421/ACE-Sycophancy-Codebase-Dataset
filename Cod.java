/**
 * A simple model of a cod.
 *
 * Cods age, move, eat seaweed, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Cod extends ForagingAnimal
{
    // The age at which a cod can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a cod can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a cod breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a cod.
    private static final int SEAWEED_FOOD_VALUE = 13;

    /**
     * Create a new cod.
     *
     * @param randomAge If true, the cod will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cod(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        initializeLife(randomAge, MAX_AGE, SEAWEED_FOOD_VALUE);
    }

    /**
     * Decide whether two cods have different sex.
     * @return true if two cods have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex()
    {
        return hasOppositeSexMate(Cod.class, 1);
    }

    /**
     * Look for seaweed adjacent to the current location.
     * @param disease disease.
     * @param step int step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step)
    {
        return searchForPrey(disease, step, 1, SEAWEED_FOOD_VALUE, Seaweed.class);
    }

    @Override
    protected Creature createOffspring(Field field, Location location)
    {
        return new Cod(false, field, location);
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
