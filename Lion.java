import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a lion.
 */
public class Lion extends Animal
{
    private static final int BREEDING_AGE = 15;
    private static final int MAX_AGE = 200;
    private static final double BREEDING_PROBABILITY = 0.5;
    private static final int MAX_LITTER_SIZE = 4;
    private static final double SUCCESSFUL_HUNT_PROB = 0.65;
    private static final int LION_FOOD_VALUE = 15;
    private static final Random rand = Randomizer.getRandom();
    private static final List<String> PREY = Collections.unmodifiableList(Arrays.asList("Gazelle"));

    public Lion(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            setFoodLevel(LION_FOOD_VALUE + 5);
        }
    }

    @Override
    public void act(List<Actor> newLions)
    {
        super.act(newLions);
    }

    @Override
    public int getFoodValue()
    {
        return LION_FOOD_VALUE;
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
    protected Animal createOffspring(Field field, Location location)
    {
        return new Lion(false, field, location);
    }
}
