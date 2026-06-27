import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a mouse.
 */
public class Mouse extends Animal
{
    private static final int BREEDING_AGE = 4;
    private static final int MAX_AGE = 40;
    private static final double BREEDING_PROBABILITY = 0.2;
    private static final int MOUSE_FOOD_VALUE = 10;
    private static final int MAX_LITTER_SIZE = 4;
    private static final double SUCCESSFUL_HUNT_PROB = 0.7;
    private static final Random rand = Randomizer.getRandom();
    private static final List<String> PREY = Collections.unmodifiableList(Arrays.asList("Grass"));

    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            setFoodLevel(MOUSE_FOOD_VALUE);
        }
    }

    @Override
    public void act(List<Actor> newMice)
    {
        super.act(newMice);
    }

    @Override
    public int getFoodValue()
    {
        return MOUSE_FOOD_VALUE;
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
        return new Mouse(false, field, location);
    }
}
