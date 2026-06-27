import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a fennec fox.
 */
public class FennecFox extends Animal
{
    private static final int BREEDING_AGE = 12;
    private static final int MAX_AGE = 100;
    private static final double BREEDING_PROBABILITY = 0.5;
    private static final int MAX_LITTER_SIZE = 2;
    private static final double SUCCESSFUL_HUNT_PROB = 0.6;
    private static final int FENNECFOX_FOOD_VALUE = 12;
    private static final Random rand = Randomizer.getRandom();
    private static final List<String> PREY = Collections.unmodifiableList(Arrays.asList("Grass", "Mouse"));

    public FennecFox(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        setNocturnal();
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            setFoodLevel(FENNECFOX_FOOD_VALUE);
        }
    }

    @Override
    public void tick(SimulationContext context)
    {
        super.tick(context);
    }

    @Override
    public int getFoodValue()
    {
        return FENNECFOX_FOOD_VALUE;
    }

    @Override
    public List<String> getPrey()
    {
        return PREY;
    }

    @Override
    protected double getHuntProbability()
    {
        return SUCCESSFUL_HUNT_PROB;
    }

    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
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
    protected Organism createOffspring(Field field, Location location)
    {
        return new FennecFox(false, field, location);
    }
}
