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
    // Characteristics shared by all fennec foxes (class variables).

    // The age at which a fennec fox can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which a fennec fox can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a fennec fox breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The probability of the animal successfully hunting their food.
    private static final double SUCCESSFUL_HUNT_PROB = 0.6;
    // The food value of a single fennec fox.
    private static final int FENNECFOX_FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // A list of the prey that the fennec fox eats.
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
            this.setAge(rand.nextInt(MAX_AGE));
            this.setFoodLevel(FENNECFOX_FOOD_VALUE);
        }
    }

    @Override
    protected int getBreedingAge() { return BREEDING_AGE; }

    @Override
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    @Override
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    @Override
    public int getMaxAge() { return MAX_AGE; }

    @Override
    protected Animal createOffspring(Field field, Location loc) {
        return new FennecFox(false, field, loc);
    }

    @Override
    protected double getHuntProbability() { return SUCCESSFUL_HUNT_PROB; }

    /**
     * @return the food value of a fennec fox, which a predator gains if
     * the fennec fox is eaten
     */
    public int getFoodValue()
    {
        return FENNECFOX_FOOD_VALUE;
    }

    /**
     * @return The list of prey which it eats
     */
    public ArrayList<String> getPrey()
    {
        return prey;
    }
}
