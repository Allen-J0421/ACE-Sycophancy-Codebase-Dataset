import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple model of a mouse.
 * Mice age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Mouse extends Animal
{
    private static final Random rand = Randomizer.getRandom();
    private static final ArrayList<String> prey = new ArrayList(Arrays.asList("Grass"));

    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        if(randomAge) {
            this.setAge(rand.nextInt(SimulationConfiguration.MOUSE_MAX_AGE));
            this.setFoodLevel(SimulationConfiguration.MOUSE_FOOD_VALUE);
        }
    }

    @Override
    protected int getBreedingAge() { return SimulationConfiguration.MOUSE_BREEDING_AGE; }

    @Override
    protected double getBreedingProbability() { return SimulationConfiguration.MOUSE_BREEDING_PROBABILITY; }

    @Override
    protected int getMaxLitterSize() { return SimulationConfiguration.MOUSE_MAX_LITTER_SIZE; }

    @Override
    public int getMaxAge() { return SimulationConfiguration.MOUSE_MAX_AGE; }

    @Override
    protected Animal createOffspring(Field field, Location loc) {
        return new Mouse(false, field, loc);
    }

    @Override
    protected double getHuntProbability() { return SimulationConfiguration.MOUSE_HUNT_PROBABILITY; }

    public int getFoodValue() { return SimulationConfiguration.MOUSE_FOOD_VALUE; }

    public ArrayList<String> getPrey() { return prey; }
}
