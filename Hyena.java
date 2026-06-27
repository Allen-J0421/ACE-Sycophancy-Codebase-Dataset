import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a hyena.
 * Hyenas age, move, eat gazelles, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Hyena extends Animal
{
    private static final Random rand = Randomizer.getRandom();
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Fennec Fox", "Gazelle"));

    /**
     * Create a hyena. A hyena can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the hyena will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hyena(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        setNocturnal();
        if(randomAge) {
            this.setAge(rand.nextInt(SimulationConfiguration.HYENA_MAX_AGE));
            this.setFoodLevel(SimulationConfiguration.HYENA_FOOD_VALUE);
        }
    }

    @Override
    protected int getBreedingAge() { return SimulationConfiguration.HYENA_BREEDING_AGE; }

    @Override
    protected double getBreedingProbability() { return SimulationConfiguration.HYENA_BREEDING_PROBABILITY; }

    @Override
    protected int getMaxLitterSize() { return SimulationConfiguration.HYENA_MAX_LITTER_SIZE; }

    @Override
    public int getMaxAge() { return SimulationConfiguration.HYENA_MAX_AGE; }

    @Override
    protected Animal createOffspring(Field field, Location loc) {
        return new Hyena(false, field, loc);
    }

    @Override
    protected double getHuntProbability() { return SimulationConfiguration.HYENA_HUNT_PROBABILITY; }

    public int getFoodValue() { return SimulationConfiguration.HYENA_FOOD_VALUE; }

    public double getHuntProb() { return SimulationConfiguration.HYENA_HUNT_PROBABILITY; }

    public ArrayList<String> getPrey() { return prey; }
}
