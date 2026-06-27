import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a fennec fox.
 * Fennec foxes age, move, eat mice, and die.
 *
 * @version 2016.02.29 (2)
 */
public class FennecFox extends Animal
{
    private static final Random rand = Randomizer.getRandom();
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Grass", "Mouse"));

    /**
     * Create a fennec fox. A fennec fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the fennec fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public FennecFox(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        setNocturnal();
        if(randomAge) {
            this.setAge(rand.nextInt(SimulationConfiguration.FENNECFOX_MAX_AGE));
            this.setFoodLevel(SimulationConfiguration.FENNECFOX_FOOD_VALUE);
        }
    }

    @Override
    protected int getBreedingAge() { return SimulationConfiguration.FENNECFOX_BREEDING_AGE; }

    @Override
    protected double getBreedingProbability() { return SimulationConfiguration.FENNECFOX_BREEDING_PROBABILITY; }

    @Override
    protected int getMaxLitterSize() { return SimulationConfiguration.FENNECFOX_MAX_LITTER_SIZE; }

    @Override
    public int getMaxAge() { return SimulationConfiguration.FENNECFOX_MAX_AGE; }

    @Override
    protected Animal createOffspring(Field field, Location loc) {
        return new FennecFox(false, field, loc);
    }

    @Override
    protected double getHuntProbability() { return SimulationConfiguration.FENNECFOX_HUNT_PROBABILITY; }

    public int getFoodValue() { return SimulationConfiguration.FENNECFOX_FOOD_VALUE; }

    public ArrayList<String> getPrey() { return prey; }
}
