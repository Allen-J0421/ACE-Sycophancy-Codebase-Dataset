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
    private static final Random rand = Randomizer.getRandom();
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
            this.setAge(rand.nextInt(SimulationConfiguration.GAZELLE_MAX_AGE));
            this.setFoodLevel(SimulationConfiguration.GAZELLE_FOOD_VALUE);
        }
    }

    @Override
    protected int getBreedingAge() { return SimulationConfiguration.GAZELLE_BREEDING_AGE; }

    @Override
    protected double getBreedingProbability() { return SimulationConfiguration.GAZELLE_BREEDING_PROBABILITY; }

    @Override
    protected int getMaxLitterSize() { return SimulationConfiguration.GAZELLE_MAX_LITTER_SIZE; }

    @Override
    public int getMaxAge() { return SimulationConfiguration.GAZELLE_MAX_AGE; }

    @Override
    protected Animal createOffspring(Field field, Location loc) {
        return new Gazelle(false, field, loc);
    }

    public int getFoodValue() { return SimulationConfiguration.GAZELLE_FOOD_VALUE; }

    public ArrayList<String> getPrey() { return prey; }
}
