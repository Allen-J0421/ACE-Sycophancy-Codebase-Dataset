import java.util.Random;
import java.util.List;

/**
 * A simple model of grass - a type of plant.
 *
 * @version 2022.02.28
 */
public class Grass extends Plant
{
    // Characteristics shared by all grass (class variables).

    // The age to which a grass can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a grass reproducing.
    private static final double REPRODUCTION_PROBABILITY = 0.44;
    // The maximum number of times it can reproduce.
    private static final int MAX_LITTER_SIZE = 6;
    // The food value of a single grass.
    private static final int GRASS_FOOD_VALUE = 10;
    // A shared random number generator to control reproduction of grass.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a piece of grass. Grass can be created as a new born (age zero)
     * or with a random age and water level.
     *
     * @param randomAge If true, the grass will have random age and water level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        this.setAge(0);
        if(randomAge) {
            this.setAge(rand.nextInt(MAX_AGE));
            setWaterLevel(rand.nextInt(15));
        }
    }

    /**
     * This is what the grass does each step of the simulation
     * - it grows and may reproduce.
     * @param newGrass A list to return newly grown grass.
     */
    public void act(List<Actor> newGrass)
    {
        super.act(newGrass);
        decreaseWaterLevel();
        if(isAlive()) {
            giveBirth(newGrass);
            findWater();
        }
    }

    @Override
    protected int getMaxAge() { return MAX_AGE; }

    @Override
    protected double getReproductionProbability() { return REPRODUCTION_PROBABILITY; }

    @Override
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    /** Grass can reproduce when its water level is sufficient. */
    @Override
    protected boolean canReproduce() { return getWaterLevel() >= 9; }

    @Override
    protected Plant createOffspring(Field field, Location loc) {
        return new Grass(false, field, loc);
    }

    /**
     * @return GRASS_FOOD_VALUE The food value of grass.
     */
    public int getFoodValue()
    {
        return GRASS_FOOD_VALUE;
    }
}
