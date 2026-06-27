import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a gazelle.
 * Gazelles age, move, find water, find food (grass), find mate, breed, and die.
 *
 * @version 2022.02.27
 */
public class Gazelle extends Animal
{
    // Characteristics shared by all gazelles (class variables).

    // The age at which a gazelle can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a gazelle can live.
    private static final int MAX_AGE = 45;
    // The likelihood of a gazelle breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The food value of a gazelle that predators get when they eat it.
    private static final int GAZELLE_FOOD_VALUE = 20;
    // The maximum number of births a gazelle can give.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // A list of the food that the gazelle eats
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Grass"));

    /**
     * Create a new gazelle. A gazelle may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        this.setAge(0);
        if(randomAge) {
            this.setAge(rand.nextInt(MAX_AGE));
            this.setFoodLevel(GAZELLE_FOOD_VALUE);
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
        return new Gazelle(false, field, loc);
    }

    /**
     * @return the food value of a gazelle, which a predator gains if
     * the gazelle is eaten
     */
    public int getFoodValue()
    {
        return GAZELLE_FOOD_VALUE;
    }

    /**
     * @return The list of prey which it eats
     */
    public ArrayList<String> getPrey()
    {
        return prey;
    }
}
