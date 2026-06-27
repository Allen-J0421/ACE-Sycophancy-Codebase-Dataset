import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a gazelle.
 */
public class Gazelle extends Animal
{
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 45;
    private static final double BREEDING_PROBABILITY = 0.5;
    private static final int GAZELLE_FOOD_VALUE = 20;
    private static final int MAX_LITTER_SIZE = 4;
    private static final Random rand = Randomizer.getRandom();
    private static final List<String> PREY = Collections.unmodifiableList(Arrays.asList("Grass"));

    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            setFoodLevel(GAZELLE_FOOD_VALUE);
        }
    }

    @Override
    public void act(List<Actor> newGazelles)
    {
        super.act(newGazelles);
    }

    @Override
    public int getFoodValue()
    {
        return GAZELLE_FOOD_VALUE;
    }

    @Override
    public List<String> getPrey()
    {
        return PREY;
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
        return new Gazelle(false, field, location);
    }
}
