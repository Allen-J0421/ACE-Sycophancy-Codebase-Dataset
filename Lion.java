import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a lion.
 * Lions age, move, find food (gazelles), find water, find mates, breed and die.
 *
 * @version 2022.02.27
 */
public class Lion extends Animal
{
    private static final Random rand = Randomizer.getRandom();
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Gazelle"));

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        if(randomAge) {
            this.setAge(rand.nextInt(SimulationConfiguration.LION_MAX_AGE));
            this.setFoodLevel(SimulationConfiguration.LION_FOOD_VALUE + 5);
        }
    }

    @Override
    protected int getBreedingAge() { return SimulationConfiguration.LION_BREEDING_AGE; }

    @Override
    protected double getBreedingProbability() { return SimulationConfiguration.LION_BREEDING_PROBABILITY; }

    @Override
    protected int getMaxLitterSize() { return SimulationConfiguration.LION_MAX_LITTER_SIZE; }

    @Override
    public int getMaxAge() { return SimulationConfiguration.LION_MAX_AGE; }

    @Override
    protected Animal createOffspring(Field field, Location loc) {
        return new Lion(false, field, loc);
    }

    @Override
    protected double getHuntProbability() { return SimulationConfiguration.LION_HUNT_PROBABILITY; }

    public int getFoodValue() { return SimulationConfiguration.LION_FOOD_VALUE; }

    public double getHuntProb() { return SimulationConfiguration.LION_HUNT_PROBABILITY; }

    public ArrayList<String> getPrey() { return prey; }
}
