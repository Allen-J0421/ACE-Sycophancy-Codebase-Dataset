/**
 * A simple model of a cod.
 * 
 * Cods age, move, eat seaweed, consume oxygen, propogate, 
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Cod extends Animal
{
    // Characteristics shared by all cods (class variables).

    // The age at which a cod can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a cod can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a cod breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;

    // The food value of a cod. In effect, this is the
    // number of steps a cod can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 13;

    /**
     * Create a new cod. A cod may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the cod will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cod(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        initializeState(randomAge, MAX_AGE, SEAWEED_FOOD_VALUE);
    }

    /**
     * A cod can breed if it has reached the breeding age.
     * @return true if the cod can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return getAge() >= BREEDING_AGE && encounterWithDiffSex();
    }

    /**
     * Decide whether two cods have different sex.
     * @return true if two cods have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex()
    {
        return hasDifferentSexNearby(Cod.class, 1);
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Only the first live seaweed is eaten., if the nearby animal is 
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step)
    {
        return findFood(disease, step, SEAWEED_FOOD_VALUE, Seaweed.class);
    }

    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    protected Animal createYoung(Field field, Location location)
    {
        return new Cod(false, field, location);
    }

}
