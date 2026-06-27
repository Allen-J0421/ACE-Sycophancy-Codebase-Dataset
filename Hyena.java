import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a hyena.
 */
public class Hyena extends Animal
{
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 150;
    private static final double BREEDING_PROBABILITY = 0.60;
    private static final int MAX_LITTER_SIZE = 2;
    private static final double SUCCESSFUL_HUNT_PROB = 0.63;
    private static final int HYENA_FOOD_VALUE = 15;
    private static final Random rand = Randomizer.getRandom();
    private static final List<String> PREY = Collections.unmodifiableList(Arrays.asList("Fennec Fox", "Gazelle"));

    public Hyena(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        setNocturnal();
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            setFoodLevel(HYENA_FOOD_VALUE);
        }
    }

    @Override
    public int getFoodValue()
    {
        return HYENA_FOOD_VALUE;
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
        return new Hyena(false, field, location);
    }
}
