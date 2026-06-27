/**
 * A simple model of grass - a type of plant.
 */
public class Grass extends Plant
{
    private static final int MAX_AGE = 20;
    private static final double REPRODUCTION_PROBABILITY = 0.44;
    private static final int MAX_LITTER_SIZE = 6;
    private static final int REPRODUCTION_WATER_THRESHOLD = 9;
    private static final int INITIAL_WATER_UPPER_BOUND = 15;
    private static final int GRASS_FOOD_VALUE = 10;

    public Grass(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    @Override
    public int getFoodValue()
    {
        return GRASS_FOOD_VALUE;
    }

    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    @Override
    protected double getBreedingProbability()
    {
        return REPRODUCTION_PROBABILITY;
    }

    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    @Override
    protected int getBreedingWaterThreshold()
    {
        return REPRODUCTION_WATER_THRESHOLD;
    }

    @Override
    protected int getStartingWaterLevelUpperBound()
    {
        return INITIAL_WATER_UPPER_BOUND;
    }

    @Override
    protected int getWaterSearchThreshold()
    {
        return 5;
    }

    @Override
    protected Organism createOffspring(Field field, Location location)
    {
        return new Grass(false, field, location);
    }
}
