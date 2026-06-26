/**
 * A simple model of a whale.
 * 
 * Whales age, move, eat cod or salmon, consume oxygen, propogate, 
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Whale extends Animal
{
    // Characteristics shared by all whales (class variables).

    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a whale can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single Cod. In effect, this is the
    // number of steps a whale can go before it has to eat again.
    private static final int COD_FOOD_VALUE = 8;

    /**
     * Create a whale. A whale can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the whale will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Whale(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        initializeState(randomAge, MAX_AGE, COD_FOOD_VALUE);
    }

    /**
     * Look for Cods and Salmon adjacent to the current location.
     * Only the first live Cod or Salmon is eaten, if the nearby animal is 
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int current step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step)
    {
        return findFood(disease, step, COD_FOOD_VALUE, Cod.class, Salmon.class);
    }

    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * A whale can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }

    /**
     * Decide whether two whales have different sex.
     * @return true if two whales have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex()
    {
        return hasDifferentSexNearby(Whale.class, 2);
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
        return new Whale(false, field, location);
    }

}
